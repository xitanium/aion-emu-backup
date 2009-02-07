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
package aionemu.network.aion;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import aionemu_commons.network.BasePacket;
import aionemu_commons.network.IServerPacket;

/**
 * @author -Nemesiss-
 */
public abstract class AionServerPacket extends BasePacket<AionConnection> implements IServerPacket
{
	private static final Logger	log	= Logger.getLogger(AionServerPacket.class.getName());

	protected AionServerPacket()
	{

	}

	@Override
	public final BasePacket<AionConnection> setConnection(AionConnection con)
	{
		BasePacket<AionConnection> bp = super.setConnection(con);
		return bp;
	}

	protected final void writeD(int value)
	{
		_buf.putInt(value);
	}

	protected final void writeH(int value)
	{
		_buf.putShort((short) value);
	}

	protected final void writeC(int value)
	{
		_buf.put((byte) value);
	}

	protected final void writeF(double value)
	{
		_buf.putDouble(value);
	}

	protected final void writeQ(long value)
	{
		_buf.putLong(value);
	}

	protected final void writeS(String text)
	{
		if (text == null)
		{
			_buf.putChar('\000');
		}
		else
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
				_buf.putChar(text.charAt(i));
			_buf.putChar('\000');
		}
	}

	protected final void writeB(byte[] data)
	{
		_buf.put(data);
	}

	@Override
	public boolean write(ByteBuffer buf)
	{
		_buf = buf;
		try
		{
			_buf.putShort((short) 0);
			writeImpl();
			_buf.flip();
			_buf.putShort((short) 0);
			ByteBuffer b = _buf.slice();
			short size = (short) (getConnection().encrypt(b) + 2);
			_buf.putShort(0, size);
			_buf.position(0).limit(size);
		}
		finally
		{
			_buf = null;
		}
		return true;
	}

	protected abstract void writeImpl();
}
