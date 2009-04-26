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

/**
 * This packet is send when client was connected to game server
 * and now is reconnection to login server.
 * @author -Nemesiss-
 */
public class CM_UPDATE_SESSION extends AionClientPacket
{
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private final int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private final int loginOk;
	/**
	 * reconectKey is key that server sends to client for fast reconnection
	 * to login server - we will check if this key is valid.
	 */
	private final int reconectKey;

	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_UPDATE_SESSION(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		accountId = readD();
		loginOk = readD();
		reconectKey = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		//TODO check if loginOk and reconnect
		//keys are valid for this account
		//if ok: generate new sessionKey and
		//	get account for this accountId and set
		//  it to this connection
		//	send SM_UPDATE_SESSION
		//else: dc
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x08 CM_UPDATE_SESSION";
	}
}
