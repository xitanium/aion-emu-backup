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
package aionemu_commons.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import aionemu_commons.network.AConnection;

/**
 * @author -Nemesiss-
 */
public class NioServer
{
	private static Logger					log				= Logger.getLogger(NioServer.class.getName());

	// The channels on which we'll accept connections
	private final List<ServerSocketChannel>	serverChannels	= new ArrayList<ServerSocketChannel>();

	private Dispatcher						acceptDispatcher;
	private int								currentReadDispatcher;
	private Dispatcher[]					readDispatchers;
	private int								currentWriteDispatcher;
	private Dispatcher[]					writeDispatchers;

	public NioServer(int readThreads, int writeThreads, ServerCfg... cfgs)
	{
		try
		{
			this.initDispatchers(readThreads, writeThreads);

			// Create a new non-blocking server socket channel for clients
			for (ServerCfg cfg : cfgs)
			{
				ServerSocketChannel serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);

				// Bind the server socket to the specified address and port
				InetSocketAddress isa;
				if ("*".equals(cfg.hostName))
				{
					isa = new InetSocketAddress(cfg.port);
					log.info("LoginServer listening on all available IPs on Port " + cfg.port + " for "
						+ cfg.acceptor.getName());
				}
				else
				{
					isa = new InetSocketAddress(cfg.hostName, cfg.port);
					log.info("LoginServer listening on IP: " + cfg.hostName + " Port " + cfg.port + " for "
						+ cfg.acceptor.getName());
				}
				serverChannel.socket().bind(isa);

				// Register the server socket channel, indicating an interest in
				// accepting new connections
				getAcceptDispatcher().register(serverChannel, SelectionKey.OP_ACCEPT, cfg.acceptor);
				serverChannels.add(serverChannel);
			}
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "NioServer Initialization Error: " + e, e);
			throw new Error("NioServer Initialization Error!");
		}
	}

	public final Dispatcher getAcceptDispatcher()
	{
		return acceptDispatcher;
	}

	public final Dispatcher getReadDispatcher()
	{
		if (readDispatchers == null)
			return acceptDispatcher;

		if (readDispatchers.length == 1)
			return readDispatchers[0];

		if (currentReadDispatcher >= readDispatchers.length)
			currentReadDispatcher = 0;
		return readDispatchers[currentReadDispatcher++];
	}

	public final Dispatcher getWriteDispatcher()
	{
		if (writeDispatchers == null)
			return acceptDispatcher;

		if (writeDispatchers.length == 1)
			return writeDispatchers[0];

		if (currentWriteDispatcher >= writeDispatchers.length)
			currentReadDispatcher = 0;
		return writeDispatchers[currentWriteDispatcher++];
	}

	public final int getActiveConnections()
	{
		int count = 0;
		if (readDispatchers != null)
		{
			for (Dispatcher d : readDispatchers)
				count += d.selector().keys().size();
		}
		else if (writeDispatchers != null)
		{
			for (Dispatcher d : writeDispatchers)
				count += d.selector().keys().size();
		}
		else
		{
			count = acceptDispatcher.selector().keys().size() - 2;
		}
		return count;
	}

	private final int disconnectAll()
	{
		int count = 0;
		if (readDispatchers != null)
		{
			for (Dispatcher d : readDispatchers)
				for (SelectionKey sk : d.selector().keys())
				{
					if (sk != null && sk.attachment() != null)
					{
						AConnection con = (AConnection) sk.attachment();
						con.close();
					}
				}
		}
		else if (writeDispatchers != null)
		{
			for (Dispatcher d : writeDispatchers)
				for (SelectionKey sk : d.selector().keys())
				{
					if (sk != null && sk.attachment() != null)
					{
						AConnection con = (AConnection) sk.attachment();
						con.close();
					}
				}
		}
		else
		{
			for (SelectionKey sk : acceptDispatcher.selector().keys())
			{
				if (sk != null && sk.attachment() != null)
				{
					AConnection con = (AConnection) sk.attachment();
					con.close();
				}
			}
		}
		return count;
	}

	private final void initDispatchers(int readThreads, int writeThreads) throws IOException
	{
		acceptDispatcher = new Dispatcher("Accept");
		acceptDispatcher.start();

		if (readThreads > 0)
		{
			readDispatchers = new Dispatcher[readThreads];
			for (int i = 0; i < readDispatchers.length; i++)
			{
				readDispatchers[i] = new Dispatcher("Read-" + i);
				readDispatchers[i].start();
			}
		}

		if (writeThreads > 0)
		{
			writeDispatchers = new Dispatcher[writeThreads];
			for (int i = 0; i < writeDispatchers.length; i++)
			{
				writeDispatchers[i] = new Dispatcher("Write-" + i);
				writeDispatchers[i].start();
			}
		}
	}

	public final void shutdown()
	{
		log.info("Closing ServerChannels...");
		try
		{
			for (ServerSocketChannel ssc : serverChannels)
				ssc.close();
			log.info("ServerChannel closed.");
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error during closing ServerChannel, " + e, e);
		}
		log.info(" Active connections: " + getActiveConnections());

		/** DC all */
		log.info("Forced Disconnecting all connections...");
		disconnectAll();
		log.info(" Active connections: " + getActiveConnections());

		/** Wait 1s */
		try
		{
			Thread.sleep(1000);
		}
		catch (Throwable t)
		{
		}
	}
}
