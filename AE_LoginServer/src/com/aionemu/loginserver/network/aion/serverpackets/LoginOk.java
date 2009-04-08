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
package com.aionemu.loginserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.SessionKey;

/**
 * @author -Nemesiss-
 */
public class LoginOk extends AionServerPacket
{
	private final int	loginOk1;
	private final int	loginOk2;

	public LoginOk(SessionKey key)
	{
		this.loginOk1 = key.loginOkID1;
		this.loginOk2 = key.loginOkID2;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, 0x03);
		writeD(buf, loginOk1);
		writeD(buf, loginOk2);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, 0x000003ea);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeB(buf, new byte[16]);
	}

	@Override
	public String getType()
	{
		return "0x03 LoginOk";
	}
}
