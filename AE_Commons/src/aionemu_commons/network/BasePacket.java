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
package aionemu_commons.network;

import java.nio.ByteBuffer;

/**
 * @author mkizub
 */
public abstract class BasePacket<T extends AConnection> implements Cloneable
{
	/** The connection this packet was received from or to be sent to. */
	T						connection;

	/**
	 * Messages are organized into Double-linked lists, maintained by network
	 * and thread scheduler threads
	 */
	BasePacket<?>			_prev, _next;
	BasePacketQueue			_queue;

	/** A field to store ByteBuffer used to decode/encode packet. */
	protected ByteBuffer	_buf;

	protected BasePacket()
	{
	}

	protected BasePacket(T con)
	{
		connection = con;
	}

	public BasePacket<T> setConnection(T con)
	{
		if (connection == null)
		{
			connection = con;
			return this;
		}
		return duplicate(con);
	}

	public final T getConnection()
	{
		return connection;
	}

	@SuppressWarnings("unchecked") public BasePacket<T> duplicate(T con)
	{
		try
		{
			BasePacket<T> bp = (BasePacket<T>) super.clone();
			bp.connection = con;
			bp._prev = null;
			bp._next = null;
			bp._queue = null;
			return bp;
		}
		catch (CloneNotSupportedException e)
		{
			return null; /* never happens */
		}
	}

	public int getLength()
	{
		return (_buf != null) ? _buf.limit() : 0;
	}

	/**
	 * just for information and debug purposes
	 * 
	 * @return text for trace message
	 */
	public abstract String getType();

	@Override public String toString()
	{
		return getType();
	}
}
