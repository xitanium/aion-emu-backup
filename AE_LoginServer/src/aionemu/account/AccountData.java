/**
 * This file is part of aion-emu.
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
package aionemu.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.BASE64Encoder;
import aionemu.configs.Config;
import aionemu.database.DB;
import aionemu.database.IUStH;
import aionemu.database.ParamReadStH;
import aionemu.utils.NetworkUtils;

/**
 * @author KID, -Nemesiss-
 */
public class AccountData implements ParamReadStH, IUStH
{
	private static Logger	log			= Logger.getLogger(AccountData.class.getName());

	private final String	name;
	private final String	clientPassword;
	private final String	lastIp;

	private String			password;
	private String			ipPattern	= "*";
	private long			expirationTime	= -1;
	private long			timePenalty	= -1;
	private int				access		= 0;
	private int				lastServer	= 0;
	private boolean			exist	= false;

	public AccountData(String name, String clientPassword, String address)
	{
		this.name = name;
		this.clientPassword = clientPassword;
		this.lastIp = address;

		DB.select("SELECT * FROM account_data WHERE name=?", this);
	}

	@Override
	public void setParams(PreparedStatement stmt) throws SQLException
	{
		stmt.setString(1, name);
	}

	@Override
	public void handleRead(ResultSet rset) throws SQLException
	{
		// acc exist
		if (rset.next())
		{
			this.password = rset.getString("password");
			this.expirationTime = rset.getLong("expiration_time");
			this.timePenalty = rset.getLong("penalty_end");
			this.access = rset.getInt("access");
			this.lastServer = rset.getInt("last_server");
			this.ipPattern = rset.getString("ip_pattern");
			this.exist = true;
		}
		else if (Config.ACCOUNT_AUTO_CREATION)
		{
			password = encryptPassword(clientPassword);
			this.exist = DB.insertUpdate(
					"INSERT INTO account_data(`name`,`password`,`last_active`,`expiration_time`,`penalty_end`,`access`,`last_server`,`last_ip`,`ip_force`) VALUES (?,?,?,?,?,?,?,?,?)",
					this, "Error while creating new account");
		}
	}

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
		stmt.setString(8, lastIp);
		stmt.setString(9, ipPattern);
		
		stmt.executeUpdate();
	}

	public final boolean exist()
	{
		return exist;
	}

	public final boolean validatePassword(String pass)
	{
		return password.equals(encryptPassword(clientPassword));
	}

	public final boolean checkIP(String address)
	{
		return NetworkUtils.checkIPMatching(ipPattern, address);
	}

	/** Ban for time, example: for 20 hours */
	public boolean penaltyActive()
	{
		if (timePenalty == -1)
			return false;
		return System.currentTimeMillis() < timePenalty;
	}

	/** Support for trial accounts */
	public boolean timeExpired()
	{
		if (timePenalty == -1)
			return false;

		return System.currentTimeMillis() > expirationTime;
	}

	public boolean isGM()
	{
		return access == 1;
	}

	private final String encryptPassword(String pass)
	{
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			log.log(Level.WARNING, "Error while obtaining decript algorithm", e);
			throw new RuntimeException("AccountData.encryptPassword()");
		}
		try
		{
			md.update(pass.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			log.log(Level.WARNING, "Problem with decript algorithm occured.", e);
			throw new RuntimeException("AccountData.encryptPassword()");
		}

		return new BASE64Encoder().encode(md.digest());
	}
}