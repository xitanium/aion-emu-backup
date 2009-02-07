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
package com.aionemu.loginserver.network.gameserver;

import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import com.aionemu.loginserver.network.gameserver.GsConnection.State;
import com.aionemu.loginserver.network.gameserver.clientpackets.GsAuth;

/**
 * @author -Nemesiss-
 */
public class GsPacketHandler
{
	private static final Logger	log	= Logger.getLogger(GsPacketHandler.class);

	public static GsClientPacket handle(ByteBuffer data, GsConnection client)
	{
		GsClientPacket msg = null;
		State state = client.getState();
		int id = data.get() & 0xff;

		switch (state)
		{
			case CONNECTED:
			{
				switch (id)
				{
					case 0x00:
						msg = new GsAuth(data, client);
						break;
					default:
						unkownPacket(state, id);
				}
				break;
			}
			case AUTHED:
			{
				switch (id)
				{
					case 0x02:
						// msg = new RequestServerList(data, client);
						break;
					case 0x03:
						// msg = new RequestServerLogin(data, client);
						break;
					default:
						unkownPacket(state, id);
				}
				break;
			}
		}
		return msg;
	}

	private static final void unkownPacket(State state, int id)
	{
		log.info("Unkown packet recived from Game Server: " + id + " state=" + state);
	}
}
