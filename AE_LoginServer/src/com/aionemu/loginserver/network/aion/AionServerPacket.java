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
package com.aionemu.loginserver.network.aion;

import java.nio.ByteBuffer;

/**
 * Base class for every LS -> Aion Server Packet.
 * 
 * @author -Nemesiss-
 */
public abstract class AionServerPacket
{
	/**
	 * Write int to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeD(ByteBuffer buf, int value)
	{
		buf.putInt(value);
	}

	/**
	 * Write short to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeH(ByteBuffer buf, int value)
	{
		buf.putShort((short) value);
	}

	/**
	 * Write byte to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeC(ByteBuffer buf, int value)
	{
		buf.put((byte) value);
	}

	/**
	 * Write double to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeF(ByteBuffer buf, double value)
	{
		buf.putDouble(value);
	}

	/**
	 * Write long to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeQ(ByteBuffer buf, long value)
	{
		buf.putLong(value);
	}

	/**
	 * Write String to buffer
	 * 
	 * @param buf
	 * @param text
	 */
	protected final void writeS(ByteBuffer buf, String text)
	{
		if (text == null)
		{
			buf.putChar('\000');
		}
		else
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
				buf.putChar(text.charAt(i));
			buf.putChar('\000');
		}
	}

	/**
	 * Write byte array to buffer.
	 * 
	 * @param buf
	 * @param data
	 */
	protected final void writeB(ByteBuffer buf, byte[] data)
	{
		buf.put(data);
	}

	/**
	 * Write and encrypt this packet data for given connection, to given buffer.
	 * 
	 * @param con
	 * @param buf
	 */
	public final void write(AionConnection con, ByteBuffer buf)
	{
		buf.putShort((short) 0);
		writeImpl(con, buf);
		buf.flip();
		buf.putShort((short) 0);
		ByteBuffer b = buf.slice();

		short size = (short) (con.encrypt(b) + 2);
		buf.putShort(0, size);
		buf.position(0).limit(size);
	}

	/**
	 * Write data that this packet represents to given byte buffer.
	 * 
	 * @param con
	 * @param buf
	 */
	protected abstract void writeImpl(AionConnection con, ByteBuffer buf);

	/**
	 * @return String - packet name.
	 */
	@Override
	public String toString()
	{
		return getType();
	}

	/**
	 * @return String - packet name.
	 */
	public abstract String getType();
}
