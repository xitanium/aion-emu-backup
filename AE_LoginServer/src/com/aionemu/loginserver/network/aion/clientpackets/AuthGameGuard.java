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
package com.aionemu.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.account.AuthResponse;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionConnection.State;
import com.aionemu.loginserver.network.aion.serverpackets.GGAuth;
import com.aionemu.loginserver.network.aion.serverpackets.LoginFail;

/**
 * @author -Nemesiss-
 */
public class AuthGameGuard extends AionClientPacket
{
	/**
	 * session id - its should match sessionId that was send in Init packet.
	 */
	private final int	sessionId;

	/*
	 * private final int data1; private final int data2; private final int data3; private final int data4;
	 */

	/**
	 * Constructor
	 * 
	 * @param buf
	 * @param client
	 */
	public AuthGameGuard(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		sessionId = readD();
		/*
		 * data1 = readD(); data2 = readD(); data3 = readD(); data4 = readD();
		 */
	}

	/**
	 * {@inheritDoc}
	 */
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
			/** Session id is not ok
			 * - inform client that smth went wrong
			 * - dc client
			 */
			con.close(new LoginFail(AuthResponse.SYSTEM_ERROR), true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x07 AuthGameGuard";
	}
}
