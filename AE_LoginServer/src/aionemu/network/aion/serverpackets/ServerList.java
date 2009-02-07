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
package aionemu.network.aion.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;

import aionemu.configs.Config;
import aionemu.network.aion.AionServerPacket;

/**
 * @author -Nemesiss-
 */
public class ServerList extends AionServerPacket
{
	@Override
	protected void writeImpl()
	{
		// TODO!
		writeC(0x04);
		writeC(1);// servers
		writeC(0);// last server
		// for(servers...)
		writeC(1);// server id

		try
		{
			InetAddress i4 = InetAddress.getByName(Config.LOGIN_BIND_ADDRESS);
			byte[] raw = i4.getAddress();
			writeC(raw[0] & 0xff);
			writeC(raw[1] & 0xff);
			writeC(raw[2] & 0xff);
			writeC(raw[3] & 0xff);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			writeC(127);
			writeC(0);
			writeC(0);
			writeC(1);
		}

		writeD(7777);// port
		writeC(0x00); // age limit
		writeC(0x01);// pvp=1
		writeH(0);// currentPlayers
		writeH(1000);// maxPlayers
		writeC(1);// ServerStatus, up=1
		writeD(0);// bits);
		writeC(0);// server.brackets ? 0x01 : 0x00);

	}

	@Override
	public String getType()
	{
		return "0x04 ServerList";
	}
}
