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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import aionemu.database.DB;
import aionemu.database.IUStH;
import aionemu.database.ReadStH;
import aionemu.utils.NetworkUtils;

/**
 * @author KID
 */
public class BanIpList
{
	protected static final Logger		_log		= Logger.getLogger(BanIpList.class.getName());
	
	private static Map<String, Long>	restricted	= new ConcurrentHashMap<String, Long>();

	public static boolean isRestricted(String address)
	{
		for (String mask : restricted.keySet())
		{
			if (restricted.get(mask) > System.currentTimeMillis() && NetworkUtils.checkIPMatching(mask, address))
				return true;
		}

		return false;
	}

	public static void addBannedIp(final String address, int minutes)
	{
		final long time = (System.currentTimeMillis() + minutes * 60000);
		
		restricted.put(address, time);
		
		_log.info("Banned IP: adding " + address + " address up to " + minutes + " mins.");

		DB.insertUpdate("REPLACE INTO ban_ip(mask, time_end) VALUES (?,?)", new IUStH()
		{
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setString(1, address);
				stmt.setLong(2, time);
				
				stmt.executeUpdate();
			}
		});
	}

	public static void removeBannedIp(String address)
	{
		if(restricted.remove(address) != null)
				_log.info("Banned IP: removed " + address + " address.");

	}

	public static void load()
	{
		restricted = new HashMap<String, Long>();
		DB.select("SELECT mask, time_end FROM ban_ip", new ReadStH()
		{
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					long tEnd = rset.getLong("time_end");
					String mask = rset.getString("mask");

					if (System.currentTimeMillis() < tEnd)
					{
						restricted.put(mask, tEnd);
					}
				}
			}
		});

		_log.info("Banned IP: Loaded " + restricted.size() + " masks.");
	}

	public static void store()
	{
		DB.insertUpdate("DELETE FROM ban_ip"); // clear table
		
		DB.insertUpdate("INSERT INTO ban_ip(mask, time_end) VALUES (?,?)", new IUStH()
		{
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				for(Map.Entry<String, Long> entry: restricted.entrySet())
				{
					long timeEnd = entry.getValue();
					if (timeEnd < System.currentTimeMillis())
						continue;

					stmt.setString(1, entry.getKey());
					stmt.setLong(2, timeEnd);
					
					stmt.addBatch();
				}

				stmt.executeBatch();
			}
		});
	}
}