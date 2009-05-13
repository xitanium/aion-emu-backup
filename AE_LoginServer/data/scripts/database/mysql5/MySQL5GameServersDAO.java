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
package mysql5;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.commons.scripting.metadata.OnClassUnload;
import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.dao.GameServersDAO;

/**
 * GameServers DAO implementation for MySQL5
 * 
 * @author -Nemesiss-
 *
 */
public class MySQL5GameServersDAO extends GameServersDAO
{
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(MySQL5GameServersDAO.class);

	/**
	 * Register dao on class load
	 */
	@OnClassLoad
	public static void onClassLoad()
	{
		try
		{
			DAOManager.registerDAO(MySQL5GameServersDAO.class);
		}
		catch (IllegalAccessException e)
		{
			log.error("Can't register DAO", e);
		}
		catch (InstantiationException e)
		{
			log.error("Can't register DAO", e);
		}
	}

	/**
	 * Unregister DAO on class unload
	 */
	@OnClassUnload
	public static void onClassUnload()
	{
		DAOManager.unregisterDAO(MySQL5GameServersDAO.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Integer, GameServerInfo> getAllGameServers()
	{

		final Map<Integer, GameServerInfo> result = new HashMap<Integer, GameServerInfo>();
		DB.select("SELECT * FROM gameservers", new ReadStH()
		{
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					int id = resultSet.getInt("id");
					String ipMask = resultSet.getString("mask");
					String password = resultSet.getString("password");
					GameServerInfo gsi = new GameServerInfo(id, ipMask, password);
					result.put(id, gsi);
				}
			}
		});
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
