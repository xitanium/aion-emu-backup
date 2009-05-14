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

import java.util.HashMap;
import java.util.Map;

import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsConnection;

/**
 * This class represents GameServer at LoginServer side.
 * It contain info about id, ip etc.
 * @author -Nemesiss-
 *
 */
public class GameServerInfo
{
	/**
	 * Id of this GameServer
	 */
	private final int		id;
	/**
	 * Allowed IP for this GameServer
	 * if gs will connect from another ip
	 * wont be registered.
	 */
	private final String	ip;
	/**
	 * Password
	 */
	private final String	password;
	/**
	 * Host on with this GameServer is accepting external
	 * connections.
	 */
	private String externalHost;
	/**
	 * Host on with this GameServer is accepting internal
	 * connections.
	 */
	private String internalHost;
	/**
	 * Port on with this GameServer
	 * is accepting clients.
	 */
	private int port;
	/**
	 * gsConnection - if GameServer is connected
	 * to LoginServer.
	 */
	private GsConnection	gsConnection;
	/**
	 * Max players count that may play on this GameServer.
	 */
	private int				maxPlayers;
	/**
	 * Map<AccId,Account> of accounts logged in on this
	 * GameServer.
	 */
	private final Map<Integer, Account> accountsOnGameServer = new HashMap<Integer, Account>();

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param ip
	 * @param password
	 */
	public GameServerInfo(int id, String ip, String password)
	{
		this.id = id;
		this.ip = ip;
		this.password = password;
	}

	/**
	 * Returns id of this GameServer.
	 * @return int id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns Password of this GameServer.
	 * @return String password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Returns allowed IP for this GameServer.
	 * @return String ip
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * Returns port of this GameServer.
	 * @return in port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Set port for this GameServer.
	 * @param port
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Returns host on with this GameServer
	 * is accepting external connections.
	 * 
	 * @return String externalHost
	 */
	public String getExternalHost()
	{
		return externalHost;
	}

	/**
	 * Set host on with this GameServer will be accepting
	 * external connections.
	 * 
	 * @param externalHost
	 */
	public void setExternalHost(String externalHost)
	{
		this.externalHost = externalHost;
	}

	/**
	 * Returns host on with this GameServer
	 * is accepting internal connections.
	 * 
	 * @return String internalHost
	 */
	public String getInternalHost()
	{
		return internalHost;
	}

	/**
	 * Set host on with this GameServer will be accepting
	 * internal connections.
	 * 
	 * @param internalHost
	 */
	public void setInternalHost(String internalHost)
	{
		this.internalHost = internalHost;
	}

	/**
	 * Returns active GsConnection for this GameServer
	 * or null if this GameServer is down.
	 * @return GsConnection
	 */
	public final GsConnection getGsConnection()
	{
		return gsConnection;
	}

	/**
	 * Set active GsConnection.
	 * @param gsConnection
	 */
	public final void setGsConnection(GsConnection gsConnection)
	{
		if(gsConnection == null)
			setPort(0);

		this.gsConnection = gsConnection;
	}

	/**
	 * Returns number of max allowed players
	 * for this GameServer.
	 * @return int maxPlayers
	 */
	public final int getMaxPlayers()
	{
		return maxPlayers;
	}

	/**
	 * Set max allowed players for this GameServer.
	 * @param maxPlayers
	 */
	public final void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Check if given account is already on This GameServer
	 * @param accountId
	 * @return true if account is on this GameServer
	 */
	public final boolean isAccountOnGameServer(int accountId)
	{
		return accountsOnGameServer.containsKey(accountId);
	}

	/**
	 * Remove account from this GameServer
	 * @param accountId
	 */
	public final void removeAccountFromGameServer(int accountId)
	{
		accountsOnGameServer.remove(accountId);
	}

	/**
	 * Add account to this GameServer
	 * @param acc 
	 */
	public final void addAccountToGameServer(Account acc)
	{
		accountsOnGameServer.put(acc.getId(), acc);
	}
}