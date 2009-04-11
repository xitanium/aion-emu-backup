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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;

/**
 * @author -Nemesiss-, KID
 */
public class AccountController
{
	/**
	 * Check if login by this user on given account is possible. If its not possible return reason as AuthResponse.
	 * 
	 * @param login
	 * @param pass
	 * @param address
	 * @return AuthResponse for this login try
	 */
	public static final AuthResponse tryAuth(final String login, String pass, final String address)
	{
		/** check if ip is banned */
		if (BanIpList.isRestricted(address))
			return AuthResponse.BAN_IP;

		/** load AccountData from sql */
		AccountData ad = new AccountData(login, pass, address);

		/** check if account was loaded successful from sql */
		if (ad.exception())
			return AuthResponse.FAILED_ACCOUNT_INFO;

		/** check if account exist */
		if (!ad.exist())
			return AuthResponse.INVALID_PASSWORD;

		/** check if pass is ok */
		if (!ad.validatePassword(pass))
			return AuthResponse.INVALID_PASSWORD;

		/** check if acc is banned */
		if (ad.isBanned())
			return AuthResponse.BAN_IP;

		/** check if account have reaming playing time */
		if (ad.timeExpired())
			return AuthResponse.TIME_EXPIRED;

		/** check if this address is allowed to login on this account */
		if (!ad.checkIP(address))
			return AuthResponse.BAN_IP;

		/** Update account info in sql like: last login time, last used itp */
		DB.insertUpdate("UPDATE account_data SET last_active=?,last_ip=? WHERE name=?", new IUStH() {
			public void handleInsertUpdate(PreparedStatement st) throws SQLException
			{
				st.setLong(1, System.currentTimeMillis());
				st.setString(2, address);
				st.setString(3, login);
				st.executeUpdate();
			}
		});

		return AuthResponse.AUTHED;
	}
}