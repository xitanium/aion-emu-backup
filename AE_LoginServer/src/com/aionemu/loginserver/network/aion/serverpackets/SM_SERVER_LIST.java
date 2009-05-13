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
package com.aionemu.loginserver.network.aion.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.GameServerTable.GameServerInfo;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionServerPacket;

/**
 * @author -Nemesiss-
 */
public class SM_SERVER_LIST extends AionServerPacket
{
	/**
	 * Logger for this class.
	 */
	protected static Logger	log	= Logger.getLogger(SM_SERVER_LIST.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		Collection<GameServerInfo> servers = GameServerTable.getGameServers();

		writeC(buf, 0x04);
		writeC(buf, servers.size());// servers
		writeC(buf, con.getAccount().getLastServer());// last server
		for(GameServerInfo gsi : servers)
		{
			writeC(buf, gsi.getId());// server id

			String host = con.usesInternalIP()? gsi.getInternalHost() : gsi.getExternalHost();
			try
			{
				InetAddress i4 = InetAddress.getByName(host);
				byte[] raw = i4.getAddress();
				writeC(buf, raw[0] & 0xff);
				writeC(buf, raw[1] & 0xff);
				writeC(buf, raw[2] & 0xff);
				writeC(buf, raw[3] & 0xff);
			}
			catch (UnknownHostException e)
			{
				log.error("GameServer with unknown host: "+e, e);
				writeC(buf, 127);
				writeC(buf, 0);
				writeC(buf, 0);
				writeC(buf, 1);
			}

			writeD(buf, gsi.getPort());// port
			writeC(buf, 0x00); // age limit
			writeC(buf, 0x01);// pvp=1
			writeH(buf, 0);// currentPlayers
			writeH(buf, gsi.getMaxPlayers());// maxPlayers
			writeC(buf, gsi.getGsConnection() != null? 1 : 0);// ServerStatus, up=1
			writeD(buf, 1);// bits);
			writeC(buf, 0);// server.brackets ? 0x01 : 0x00);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x04 SM_SERVER_LIST";
	}
}
