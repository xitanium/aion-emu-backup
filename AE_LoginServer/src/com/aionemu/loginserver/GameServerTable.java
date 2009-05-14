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
package com.aionemu.loginserver;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.dao.GameServersDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;

/**
 * GameServerTable contains list of GameServers registered on this
 * LoginServer. GameServer may by online or down.
 * @author -Nemesiss-
 */
public class GameServerTable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger							log			= Logger.getLogger(GameServerTable.class);

	/**
	 * Map<Id,GameServer>
	 */
	private static Map<Integer, GameServerInfo>	gameservers;

	/**
	 * Return collection contains all registered [up/down]
	 * GameServers.
	 * @return collection of GameServers.
	 */
	public static final Collection<GameServerInfo> getGameServers()
	{
		return Collections.unmodifiableCollection(gameservers.values());
	}

	/**
	 * Load GameServers from database.
	 */
	public static void load()
	{
		gameservers = getDAO().getAllGameServers();
		log.info("GameServerTable loaded "+gameservers.size()+" registered GameServers.");
	}

	/**
	 * Register GameServer if its possible.
	 * 
	 * @param gsConnection
	 * @param requestedId
	 * @param externalHost
	 * @param internalHost
	 * @param port
	 * @param maxPlayers
	 * @param password
	 * @return GsAuthResponse
	 */
	public static GsAuthResponse registerGameServer(GsConnection gsConnection, int requestedId, String externalHost,
		String internalHost, int port, int maxPlayers, String password)
	{
		GameServerInfo gsi = gameservers.get(requestedId);

		/**
		 * This id is not Registered at LoginServer.
		 */
		if(gsi == null)
		{
			log.info(gsConnection+" requestedID="+requestedId+" not aviable!");
			return GsAuthResponse.NOT_AUTHED;
		}

		/**
		 * Check if this GameServer is not already registered.
		 */
		if(gsi.getGsConnection() != null)
			return GsAuthResponse.ALREADY_REGISTERED;

		/**
		 * Check if password and ip are ok.
		 */
		if(!gsi.getPassword().equals(password) || !NetworkUtils.checkIPMatching(gsi.getIp(), gsConnection.getIP()))
		{
			log.info(gsConnection+" wrong ip or password!");
			return GsAuthResponse.NOT_AUTHED;
		}

		gsi.setExternalHost(externalHost);
		gsi.setInternalHost(internalHost);
		gsi.setPort(port);
		gsi.setMaxPlayers(maxPlayers);
		gsi.setGsConnection(gsConnection);

		gsConnection.setGameServerInfo(gsi);
		return GsAuthResponse.AUTHED;
	}

	/**
	 * Returns GameSererInfo object for given gameserverId.
	 * @param gameServerId
	 * @return GameSererInfo object for given gameserverId.
	 */
	public static final GameServerInfo getGameServerInfo(int gameServerId)
	{
		return gameservers.get(gameServerId);
	}

    /**
     * Check if account is already in use on any
     * GameServer. If so - kick account from GameServer.
     * @param acc
     * @return true is account is logged in on one of GameServers
     */
    public static final boolean isAccountOnAnyGameServerAndKick(Account acc)
    {
    	for(GameServerInfo gsi : getGameServers())
    	{
    		if(gsi.isAccountOnGameServer(acc.getId()))
    		{
    			gsi.getGsConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(acc.getId()));
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Retuns {@link com.aionemu.loginserver.dao.GameServersDAO} , just a shortcut
     *
     * @return {@link com.aionemu.loginserver.dao.GameServersDAO}
     */
    private static GameServersDAO getDAO()
    {
        return DAOManager.getDAO(GameServersDAO.class);
    }
}
