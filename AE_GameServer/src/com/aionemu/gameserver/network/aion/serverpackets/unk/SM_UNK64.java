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
package com.aionemu.gameserver.network.aion.serverpackets.unk;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNK64 extends AionServerPacket
{
	private static final byte[]	data	= new byte[] { (byte) 0x39, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xEC,
		(byte) 0x51, (byte) 0x38, (byte) 0x3E, (byte) 0xB8, (byte) 0x1E, (byte) 0x85, (byte) 0x3E, (byte) 0x29,
		(byte) 0x5C, (byte) 0x0F, (byte) 0x3F, (byte) 0x01, (byte) 0x00, (byte) 0x10, (byte) 0xAB, (byte) 0xD7,
		(byte) 0x17, (byte) 0xEC, (byte) 0x51, (byte) 0x38, (byte) 0x3E, (byte) 0xB8, (byte) 0x1E, (byte) 0x85,
		(byte) 0x3E, (byte) 0x29, (byte) 0x5C, (byte) 0x0F, (byte) 0x3F };
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeB(buf, data);
	}
}
