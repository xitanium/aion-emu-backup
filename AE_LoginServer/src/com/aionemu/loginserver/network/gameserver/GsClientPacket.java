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
package com.aionemu.loginserver.network.gameserver;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * @author -Nemesiss-
 */
public abstract class GsClientPacket implements Runnable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(GsClientPacket.class);
	/**
	 * ByteBuffer that contains this packet data
	 */
	private final ByteBuffer	buf;
	/**
	 * Owner of this packet.
	 */
	private final GsConnection	client;

	/**
	 * COnstructor.
	 * 
	 * @param buf
	 * @param client
	 */
	protected GsClientPacket(ByteBuffer buf, GsConnection client)
	{
		this.buf = buf;
		this.client = client;
	}

	/**
	 * run runImpl catching and logging Throwable.
	 */
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Throwable e)
		{
			log.warn("error handling gs (" + getConnection().getIP() + ") message " + getType(), e);
		}
	}

	/**
	 * @return Connection that is owner of this packet.
	 */
	public final GsConnection getConnection()
	{
		return client;
	}

	/**
	 * Execute this packet action.
	 */
	protected abstract void runImpl();

	/**
	 * Send new GsServerPacket to connection that is owner of this packet. This method is equivalent to:
	 * getConnection().sendPacket(msg);
	 * 
	 * @param msg
	 */
	protected void sendPacket(GsServerPacket msg)
	{
		getConnection().sendPacket(msg);
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
			log.info("Missing D for: " + this);
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
			log.info("Missing C for: " + this);
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
			log.info("Missing H for: " + this);
		}
		return 0;
	}

	/**
	 * Read double from this packet buffer.
	 * 
	 * @return double
	 */
	public final double readF()
	{
		try
		{
			return buf.getDouble();
		}
		catch (Exception e)
		{
			log.info("Missing F for: " + this);
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
			log.info("Missing Q for: " + this);
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
			log.info("Missing byte[] for: " + this);
		}
		return result;
	}

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
