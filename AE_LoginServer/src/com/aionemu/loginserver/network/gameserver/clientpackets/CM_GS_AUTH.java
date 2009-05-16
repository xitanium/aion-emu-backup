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
package com.aionemu.loginserver.network.gameserver.clientpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsConnection.State;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_GS_AUTH_RESPONSE;

/**
 * This is authentication packet that gs will send to login server for registration.
 * 
 * @author -Nemesiss-
 */
public class CM_GS_AUTH extends GsClientPacket
{
	/**
	 * Password for authentication
	 */
	private final String	password;
	/**
	 * Id of GameServer
	 */
	private final byte		gameServerId;
	/**
	 * Maximum number of players that this Gameserver can accept.
	 */
	private final int		maxPalyers;
	/**
	 * Port of this Gameserver.
	 */
	private final int		port;
	/**
	 * External hostname of this Gameserver.
	 */
	private final String	externalHost;
	/**
	 * Internal hostname of this Gameserver.
	 */
	private final String	internalHost;

	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_GS_AUTH(ByteBuffer buf, GsConnection client)
	{
		super(buf, client);
		gameServerId = (byte) readC();
		externalHost = readS();
		internalHost = readS();
		port = readH();
		maxPalyers = readD();
		password = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		GsConnection client = getConnection();

		GsAuthResponse resp = GameServerTable.registerGameServer(client, gameServerId, externalHost, internalHost,
			port, maxPalyers, password);

		switch (resp)
		{
			case AUTHED:
				getConnection().setState(State.AUTHED);
				sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;

			default:
				client.close(new SM_GS_AUTH_RESPONSE(resp), true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x00 CM_GS_AUTH";
	}
}
