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
package com.aionemu.commons.network.packet;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * Base class for every Client Packet
 * 
 * @author -Nemesiss-
 */
public abstract class BaseClientPacket extends BasePacket implements Runnable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger		log	= Logger.getLogger(BaseClientPacket.class);
	/**
	 * ByteBuffer that contains this packet data
	 */
	private final ByteBuffer		buf;

	/**
	 * Constructs a new client packet with specified id and data buffer.
	 *
	 * @param buf       packet data container.
	 * @param opcode    packet opcode.
	 */
	public BaseClientPacket(ByteBuffer buf, int opcode)
	{
		super(PacketType.CLIENT, opcode);

		this.buf = buf;
	}

	/**
	 * @return number of bytes remaining in this packet buffer.
	 */
	public final int getRemainingBytes()
	{
		return buf.remaining();
	}

	/**
	 * Read int from this packet buffer.
	 * 
	 * @return int
	 */
	public final int readD()
	{
		try
		{
			return buf.getInt();
		}
		catch (Exception e)
		{
			log.error("Missing D for: " + this);
		}
		return 0;
	}

	/**
	 * Read byte from this packet buffer.
	 * 
	 * @return int
	 */
	public final int readC()
	{
		try
		{
			return buf.get() & 0xFF;
		}
		catch (Exception e)
		{
			log.error("Missing C for: " + this);
		}
		return 0;
	}

	/**
	 * Read short from this packet buffer.
	 * 
	 * @return int
	 */
	public final int readH()
	{
		try
		{
			return buf.getShort() & 0xFFFF;
		}
		catch (Exception e)
		{
			log.error("Missing H for: " + this);
		}
		return 0;
	}

	/**
	 * Read double from this packet buffer.
	 * 
	 * @return double
	 */
	public final double readDF()
	{
		try
		{
			return buf.getDouble();
		}
		catch (Exception e)
		{
			log.error("Missing DF for: " + this);
		}
		return 0;
	}

	/**
	 * Read double from this packet buffer.
	 * 
	 * @return double
	 */
	public final float readF()
	{
		try
		{
			return buf.getFloat();
		}
		catch (Exception e)
		{
			log.error("Missing F for: " + this);
		}
		return 0;
	}

	/**
	 * Read long from this packet buffer.
	 * 
	 * @return long
	 */
	public final long readQ()
	{
		try
		{
			return buf.getLong();
		}
		catch (Exception e)
		{
			log.error("Missing Q for: " + this);
		}
		return 0;
	}

	/**
	 * Read String from this packet buffer.
	 * 
	 * @return String
	 */
	public final String readS()
	{
		StringBuffer sb = new StringBuffer();
		char ch;
		try
		{
			while ((ch = buf.getChar()) != 0)
				sb.append(ch);
		}
		catch (Exception e)
		{
			log.error("Missing S for: " + this);
		}
		return sb.toString();
	}

	/**
	 * Read n bytes from this packet buffer, n = length.
	 * 
	 * @param length
	 * @return byte[]
	 */
	public final byte[] readB(int length)
	{
		byte[] result = new byte[length];
		try
		{
			buf.get(result);
		}
		catch (Exception e)
		{
			log.error("Missing byte[] for: " + this);
		}
		return result;
	}

	/**
	 * Execute this packet action.
	 */
	protected abstract void runImpl();
}
