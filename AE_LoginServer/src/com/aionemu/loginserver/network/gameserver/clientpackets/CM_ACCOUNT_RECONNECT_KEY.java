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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;

/**
 * This packet is sended by GameServer when player
 * is requesting fast reconnect to login server.
 * LoginServer in response will send reconectKey.
 * @author -Nemesiss-
 *
 */
public class CM_ACCOUNT_RECONNECT_KEY extends GsClientPacket
{
	/**
	 * accountName of account that will be reconnecting.
	 */
	private final String accountName;
	/**
	 * accoundId of account that will be reconnecting.
	 */
	private final int accountId;
	/**
	 * loginOk of account that will be reconnecting.
	 */
	private final int loginOk;

	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_ACCOUNT_RECONNECT_KEY(ByteBuffer buf, GsConnection client)
	{
		super(buf, client);
		accountName = readS();
		accountId = readD();
		loginOk = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		int reconectKey = Rnd.nextInt();
		//TODO! add to account reconnecting list.
		this.sendPacket(new SM_ACCOUNT_RECONNECT_KEY(accountName, reconectKey));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType()
	{
		return "0x02 CM_ACCOUNT_RECONECT_KEY";
	}
}
