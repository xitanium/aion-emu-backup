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
package com.aionemu.commons.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author -Nemesiss-
 */
public abstract class AConnection
{
	final SocketChannel				socketChannel;
	private SelectionKey			writeKey;

	/** Queue of messages to be sent, if writeBuffer is not null */
	public final BasePacketQueue	sendMsgQueue;

	public final ByteBuffer			write_buffer;
	public final ByteBuffer			read_buffer;

	public AConnection(SocketChannel sc)
	{
		socketChannel = sc;
		sendMsgQueue = new BasePacketQueue();
		write_buffer = ByteBuffer.allocate(8192 * 2);
		write_buffer.flip();
		write_buffer.order(ByteOrder.LITTLE_ENDIAN);
		read_buffer = ByteBuffer.allocate(8192 * 2);
		read_buffer.flip();
		read_buffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	public final void setWriteKey(SelectionKey writeKey)
	{
		this.writeKey = writeKey;
	}

	public final void enableWriteInterest()
	{
		if (this.writeKey.isValid())
		{
			this.writeKey.interestOps(writeKey.interestOps() | SelectionKey.OP_WRITE);
			writeKey.selector().wakeup();
		}
	}

	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}

	/**
	 * This will only close the connection without taking care of the active
	 * char
	 */
	public void onlyClose()
	{
		try
		{
			if (socketChannel != null && socketChannel.isOpen())
			{
				socketChannel.close();
			}
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Return IP adress of this Client Connection.
	 */
	public String getIP()
	{
		return socketChannel.socket().getInetAddress().getHostAddress();
	}

	abstract public void exception(IOException e, boolean read);

	abstract public void terminate();

	abstract public boolean processData(ByteBuffer data);

	abstract public void close();
}
