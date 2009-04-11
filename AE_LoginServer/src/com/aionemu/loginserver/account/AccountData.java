/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.loginserver.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.utils.NetworkUtils;

import sun.misc.BASE64Encoder;

/**
 * @author KID, -Nemesiss-
 */
public class AccountData implements ParamReadStH, IUStH
{
	/**
	 * Logger for this class.
	 */
	private static Logger	log				= Logger.getLogger(AccountData.class);

	/**
	 * account name (login)
	 */
	private final String	name;
	/**
	 * password typed by client
	 */
	private final String	clientPassword;
	/**
	 * IP of client that is trying to login
	 */
	private final String	clientIp;
	/**
	 * encrypted password that exist in sql.
	 */
	private String			password;
	/**
	 * allowed ip's that may login on this acc.
	 */
	private String			ipForce			= "*";
	/**
	 * Time when this account expire.
	 */
	private long			expirationTime	= -1;
	/**
	 * Time to with this account stays banned.
	 */
	private long			timePenalty		= -1;
	/**
	 * access - for gm.
	 */
	private int				access			= 0;
	/**
	 * last server where this account was playing
	 */
	private int				lastServer		= 0;
	/**
	 * is this account exist.
	 */
	private boolean			exist			= false;
	/**
	 * was exception during sql quere.
	 */
	private boolean			exception		= false;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param clientPassword
	 * @param address
	 */
	public AccountData(String name, String clientPassword, String address)
	{
		this.name = name;
		this.clientPassword = clientPassword;
		this.clientIp = address;

		exception = !DB.select("SELECT * FROM account_data WHERE name=?", this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParams(PreparedStatement stmt) throws SQLException
	{
		stmt.setString(1, name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Reads account data from sql.
	 */
	@Override
	public void handleRead(ResultSet rset) throws SQLException
	{
		/** acc exist */
		if (rset.next())
		{
			this.password = rset.getString("password");
			this.expirationTime = rset.getLong("expiration_time");
			this.timePenalty = rset.getLong("penalty_end");
			this.access = rset.getInt("access");
			this.lastServer = rset.getInt("last_server");
			this.ipForce = rset.getString("ip_force");
			this.exist = true;
		}
		else if (Config.ACCOUNT_AUTO_CREATION)
		{
			password = encryptPassword(clientPassword);
			this.exist = DB
				.insertUpdate(
					"INSERT INTO account_data(`name`,`password`,`last_active`,`expiration_time`,`penalty_end`,`access`,`last_server`,`last_ip`,`ip_force`) VALUES (?,?,?,?,?,?,?,?,?)",
					this, "Error while creating new account");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Insert new account into sql.
	 */
	@Override
	public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
	{
		stmt.setString(1, name);
		stmt.setString(2, password);
		stmt.setLong(3, System.currentTimeMillis());
		stmt.setLong(4, expirationTime);
		stmt.setLong(5, timePenalty);
		stmt.setInt(6, access);
		stmt.setInt(7, lastServer);
		stmt.setString(8, clientIp);
		stmt.setString(9, ipForce);

		stmt.executeUpdate();
	}

	/**
	 * Check if account exist in sql.
	 * 
	 * @return true if account exist in sql.
	 */
	public final boolean exist()
	{
		return exist;
	}

	/**
	 * Check if sql quere was ok.
	 * 
	 * @return true if sql quere was ok.
	 */
	public final boolean exception()
	{
		return exception;
	}

	/**
	 * Check if typed password by user match password in sql.
	 * 
	 * @param pass
	 * @return true if pass match password in sql.
	 */
	public final boolean validatePassword(String pass)
	{
		return password.equals(encryptPassword(clientPassword));
	}

	/**
	 * Check if its possible to login from given ip address on this account.
	 * 
	 * @param address
	 * @return true if given ip is allowed to login on this account.
	 */
	public final boolean checkIP(String address)
	{
		return NetworkUtils.checkIPMatching(ipForce, address);
	}

	/**
	 * Check if this account is banned forever or time banned.
	 * 
	 * @return true if this account is banned.
	 */
	public boolean isBanned()
	{
		if (timePenalty == -1)
			return false;
		return System.currentTimeMillis() < timePenalty;
	}

	/**
	 * Check if this account is still valid [for trial acc etc]
	 * 
	 * @return true if this account is not valid.
	 */
	public boolean timeExpired()
	{
		if (expirationTime == -1)
			return false;
		return System.currentTimeMillis() > expirationTime;
	}

	/**
	 * Check if this account have gm permissions.
	 * 
	 * @return true if this account have gm permissions.
	 */
	public boolean isGM()
	{
		return access == 1;
	}

	/**
	 * Encrypt given password with SHA-1
	 * 
	 * @param pass
	 * @return encrypted password
	 */
	private String encryptPassword(String pass)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			log.warn("Error while obtaining decript algorithm", e);
			throw new RuntimeException("AccountData.encryptPassword()");
		}
		try
		{
			md.update(pass.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			log.warn("Problem with decript algorithm occured.", e);
			throw new RuntimeException("AccountData.encryptPassword()");
		}

		return new BASE64Encoder().encode(md.digest());
	}
}