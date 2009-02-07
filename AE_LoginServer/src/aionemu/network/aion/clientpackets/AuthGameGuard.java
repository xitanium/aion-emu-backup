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
import aionemu.network.aion.AionConnection.State;
import aionemu.network.aion.serverpackets.GGAuth;

/**
 * @author -Nemesiss-
 */
public class AuthGameGuard extends AionClientPacket
{
	private final int	sessionId;

	/*
	 * private final int data1; private final int data2; private final int data3; private final int data4;
	 */

	public AuthGameGuard(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		sessionId = readD();
		/*
		 * data1 = readD(); data2 = readD(); data3 = readD(); data4 = readD();
		 */
	}

	@Override
	protected void runImpl()
	{
		AionConnection con = getConnection();
		if (con.getSessionId() == sessionId)
		{
			con.setState(State.AUTHED_GG);
			con.sendPacket(new GGAuth(sessionId));
		}
		else
		{
			// TODO! send dc packet!
		}

	}

	@Override
	public String getType()
	{
		return "0x07 AuthGameGuard";
	}
}
