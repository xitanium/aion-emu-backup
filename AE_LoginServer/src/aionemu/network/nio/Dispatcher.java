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
package aionemu.network.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import aionemu.network.AConnection;
import aionemu.network.BasePacketQueue;
import aionemu.network.IAcceptor;
import aionemu.network.IServerPacket;
import aionemu.utils.ThreadUncaughtExceptionHandler;

/**
 * @author -Nemesiss-
 */
public class Dispatcher extends Thread
{
	private final static Logger	log			= Logger.getLogger(Dispatcher.class.getName());

	// The buffer into which we'll read data when it's available
	ByteBuffer					readBuffer	= ByteBuffer.allocate(8192);

	// The buffer into which we'll write data
	ByteBuffer					writeBuffer	= ByteBuffer.allocate(8192 * 4);

	private final Selector		selector;

	public Dispatcher(String name) throws IOException
	{
		super(name + " Dispatcher");
		this.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		this.writeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.readBuffer.order(ByteOrder.LITTLE_ENDIAN);
		// Create a new selector
		this.selector = SelectorProvider.provider().openSelector();
	}

	@Override public void run()
	{
		for (;;)
		{
			try
			{
				log.info("....");
				selector.select();
				dispatch();
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "Dispatcher error! " + e, e);
			}

		}
	}

	public final SelectionKey register(SelectableChannel ch, int ops, Object att) throws IOException
	{
		synchronized (gate)
		{
			selector.wakeup();
			return ch.register(selector, ops, att);
		}
	}

	final Selector selector()
	{
		return this.selector;
	}

	private Object	gate	= new Object();

	private final void dispatch()
	{
		// Iterate over the set of keys for which events are available
		Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
		while (selectedKeys.hasNext())
		{
			SelectionKey key = selectedKeys.next();
			selectedKeys.remove();

			if (!key.isValid())
				continue;

			// Check what event is available and deal with it
			switch (key.readyOps())
			{
				case SelectionKey.OP_ACCEPT:
					this.accept(key);
					break;
				case SelectionKey.OP_READ:
					this.read(key);
					break;
				case SelectionKey.OP_WRITE:
					this.write(key);
					break;
				case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
					this.read(key);
					this.write(key);
			}
		}
		synchronized (gate)
		{
		};
	}

	private final void accept(SelectionKey key)
	{
		try
		{
			((IAcceptor) key.attachment()).accept(key);
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "Error while accepting connection: +" + e, e);
		}
	}

	private final void read(SelectionKey key)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		AConnection con = (AConnection) key.attachment();

		if (!socketChannel.isOpen())
		{
			con.close();
			log.info("Error! - read on closed channel");
			return;
		}
		ByteBuffer b = readBuffer;
		b.clear();

		// Attempt to read off the channel
		int numRead;
		try
		{
			numRead = socketChannel.read(b);
		}
		catch (IOException e)
		{
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			con.exception(e, true);
			return;
		}

		if (numRead == -1)
		{
			con.terminate();
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			con.close();
			return;
		}
		else if (numRead == 0)
		{
			log.info("Read 0");
			return;
		}
		b.flip();
		// we've got some part already readed
		if (con.read_buffer.hasRemaining())
		{
			con.read_buffer.put(b);
			con.read_buffer.flip();
			b = con.read_buffer;
		}
		while (b.remaining() > 2)
		{
			short sz = b.getShort(b.position());
			if (sz <= b.remaining())
			{
				// got full message
				if (!parse(con, b))// client will crash
				{
					b.position(b.limit());
					con.close();
					return;
				}
			}
			else
			{
				// dont get full packet
				break;
			}
		}
		// there arent any reaming bytes
		if (!b.hasRemaining())
		{
			con.read_buffer.position(0).limit(0);
			return;
		}
		// Put not complet packet to readbuffer for future reading
		// prepare buffer for pending read
		else if (b != con.read_buffer)
		{
			con.read_buffer.position(0).limit(8192 * 2);
			con.read_buffer.put(b);
			return;
		}
		else
		{
			b.position(b.limit());
			con.close();
			log.info("This shouldnt happend!!!!!");
		}
		return;
	}

	private final boolean parse(AConnection con, ByteBuffer buf)
	{
		short sz = 0;
		try
		{
			sz = buf.getShort();
			if (sz > 1)
				sz -= 2;
			ByteBuffer b = (ByteBuffer) buf.slice().limit(sz);
			b.order(ByteOrder.LITTLE_ENDIAN);
			buf.position(buf.position() + sz); // read message fully

			return con.processData(b);
		}
		catch (IllegalArgumentException e)
		{
			log.log(Level.SEVERE, "Error on parsing input from client - account: " + con + " packet size: " + sz
				+ " real size:" + buf.remaining(), e);
			con.close();
			return false;
		}
	}

	private final void write(SelectionKey key)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();

		AConnection con = (AConnection) key.attachment();
		BasePacketQueue queue = con.sendMsgQueue;

		// We have not writted data
		if (con.write_buffer.hasRemaining())
		{
			try
			{
				socketChannel.write(con.write_buffer);
			}
			catch (IOException e)
			{
				con.exception(e, false);
				return;
			}
			// Again not all data was send
			if (con.write_buffer.hasRemaining())
				return;
			// Make this buffer ready for future write
			con.write_buffer.position(0).limit(0);
		}
		else
		{
			while (!queue.isEmpty())
			{
				writeBuffer.clear();

				IServerPacket msg = (IServerPacket) queue.get();
				msg.write(writeBuffer);
				// Attempt to write to the channel
				try
				{
					socketChannel.write(writeBuffer);
				}
				catch (IOException e)
				{
					con.exception(e, false);
					return;
				}
				if (writeBuffer.hasRemaining())
				{
					// _log.warning("Not all data was written!");
					con.write_buffer.limit(con.write_buffer.limit() + writeBuffer.remaining());
					con.write_buffer.put(writeBuffer);
					con.write_buffer.flip();
					break;
				}
			}
		}
		if (queue.isEmpty())
		{
			// We wrote away all data, so we're no longer interested
			// in writing on this socket.
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		}
	}
}
