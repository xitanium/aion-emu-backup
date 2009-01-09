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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import aionemu.database.DB;
import aionemu.database.IUStH;

/**
 * @author -Nemesiss-, KID
 */
public class AccountController
{
	public static final AuthResponse tryAuth(final String login, String pass, final String address)
	{
		if(BanIpList.isRestricted(address)) 
			return AuthResponse.RESTRICTED_IP;
		
		AccountData ad = new AccountData(login, pass, address);
		
		if(!ad.exist())
			return AuthResponse.INVALID_PASSWORD;
		
		if(!ad.validatePassword(pass))
			return AuthResponse.INVALID_PASSWORD;
		
		if(ad.penaltyActive()) 
			return AuthResponse.PENALTY_ACTIVE;

		if(ad.timeExpired())
			return AuthResponse.TIME_EXPIRED;
		
		if(!ad.checkIP(address))
			return AuthResponse.RESTRICTED_IP;
		
		DB.insertUpdate("UPDATE account_data SET time_last_active=?,last_ip=? WHERE name=?",  new IUStH(){
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