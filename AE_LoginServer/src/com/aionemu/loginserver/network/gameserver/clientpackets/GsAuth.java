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
package com.aionemu.loginserver.network.gameserver.clientpackets;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.GsAuthResponse;

/**
 * @author -Nemesiss-
 */
public class GsAuth extends GsClientPacket
{
	private static final Logger	log	= Logger.getLogger(GsAuth.class.getName());
	private final byte[]		hexID;
	private final int			desiredID;
	private final boolean		hostReserved;
	private final int			max_palyers;
	private final int			port;
	private final String		externalHost;
	private final String		internalHost;

	public GsAuth(ByteBuffer buf, GsConnection client)
	{
		super(buf, client);
		desiredID = readC();
		hostReserved = (readC() != 0);
		externalHost = readS();
		internalHost = readS();
		port = readH();
		max_palyers = readD();
		int size = readD();
		hexID = readB(size);
	}

	@Override protected void runImpl()
	{
		int resp = GameServerTable.registerGameServer(getConnection(), desiredID, hostReserved, externalHost,
			internalHost, port, max_palyers, new BigInteger(hexID).toString(16));
		sendPacket(new GsAuthResponse(resp));
	}

	@Override public String getType()
	{
		return "0x00 GsAuth";
	}
}
