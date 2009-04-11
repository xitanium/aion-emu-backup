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

import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AuthResponse;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.LoginFail;
import com.aionemu.loginserver.network.aion.serverpackets.PlayOk;

/**
 * @author -Nemesiss-
 */
public class RequestServerLogin extends AionClientPacket
{
	/**
	 * loginOk1 is part of session key - its used for security purposes
	 */
	private final int	loginOk1;
	/**
	 * loginOk2 is part of session key - its used for security purposes
	 */
	private final int	loginOk2;
	/**
	 * Id of game server that this client is trying to play on.
	 */
	private final int	servId;

	/**
	 * Constructor.
	 * @param buf
	 * @param client
	 */
	public RequestServerLogin(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		loginOk1 = readD();
		loginOk2 = readD();
		servId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection con = getConnection();
		SessionKey key = con.getSessionKey();
		if (key.checkLogin(loginOk1, loginOk2))
		{
			//TODO!
			//if(serv down)
			//	con.sendPacket(new PlayFail(AuthResponse.SERVER_DOWN));
			//if(serv gm only)
			//	con.sendPacket(new PlayFail(AuthResponse.GM_ONLY));
			//if(serv full)
			//	con.sendPacket(new PlayFail(AuthResponse.SERVER_FULL));
			sendPacket(new PlayOk(key));
		}
		con.close(new LoginFail(AuthResponse.SYSTEM_ERROR), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x02 RequestServerLogin";
	}
}
