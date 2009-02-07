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
import java.util.logging.Level;
import java.util.logging.Logger;

import aionemu_commons.network.BasePacket;

/**
 * @author -Nemesiss-
 */
public abstract class AionClientPacket extends BasePacket<AionConnection> implements Runnable
{
	private static Logger	log	= Logger.getLogger(AionClientPacket.class.getName());

	protected AionClientPacket(ByteBuffer buf, AionConnection client)
	{
		super(client);
		_buf = buf;
	}

	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Throwable e)
		{
			String name = getConnection().getAccount();
			if (name == null)
				name = getConnection().getIP();

			log.log(Level.SEVERE, "error handling client (" + name + ") message " + getType(), e);
		}
	}

	/**
	 * This is only called once per packet instane ie: when you construct a packet and send it to many players, it will
	 * only run when the first packet is sent
	 */
	protected abstract void runImpl();

	protected void sendPacket(AionServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}

	public final int getRemainingBytes()
	{
		return _buf.remaining();
	}

	public final int readD()
	{
		try
		{
			return _buf.getInt();
		}
		catch (Exception e)
		{
			System.out.print("Missing D for: " + this);
		}
		return 0;
	}

	public final int readC()
	{
		try
		{
			return _buf.get() & 0xFF;
		}
		catch (Exception e)
		{
		}
		return 0;
	}

	public final int readH()
	{
		try
		{
			return _buf.getShort() & 0xFFFF;
		}
		catch (Exception e)
		{
		}
		return 0;
	}

	public final double readF()
	{
		try
		{
			return _buf.getDouble();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public final long readQ()
	{
		try
		{
			return _buf.getLong();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public final String readS()
	{
		StringBuffer sb = new StringBuffer();
		char ch;
		try
		{
			while ((ch = _buf.getChar()) != 0)
				sb.append(ch);
		}
		catch (Exception e)
		{
		}
		return sb.toString();
	}

	public final byte[] readB(int length)
	{
		byte[] result = new byte[length];
		try
		{
			_buf.get(result);
		}
		catch (Exception e)
		{
		}
		return result;
	}

	/**
	 * To samo co readB tylko, ze 20%-25% szybsze
	 * 
	 * @param length
	 * @return
	 */
	public final byte[] readBN(int length)
	{
		byte[] result = new byte[length];
		try
		{
			System.arraycopy(_buf.array(), _buf.arrayOffset(), result, 0, result.length);
		}
		catch (Exception e)
		{
		}
		return result;
	}
}
