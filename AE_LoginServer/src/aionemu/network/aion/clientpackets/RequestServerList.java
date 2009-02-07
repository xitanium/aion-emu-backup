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
package aionemu.network.aion.clientpackets;

import java.nio.ByteBuffer;

import aionemu.network.aion.AionClientPacket;
import aionemu.network.aion.AionConnection;
import aionemu.network.aion.serverpackets.ServerList;

/**
 * @author -Nemesiss-
 */
public class RequestServerList extends AionClientPacket
{
	private final int	loginOk1;
	private final int	loginOk2;

	public RequestServerList(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		loginOk1 = readD();
		loginOk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		if (getConnection().getSessionKey().checkLogin(loginOk1, loginOk2))
		{
			sendPacket(new ServerList());
		}
		else
		{
			// TODO!
		}

	}

	@Override
	public String getType()
	{
		return "0x05 RequestServerList";
	}
}
