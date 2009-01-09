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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import aionemu.Shutdown;
import aionemu.configs.Config;
import aionemu.network.AConnection;
import aionemu.network.aion.AionAcceptor;
import aionemu.network.gameserver.GsAcceptor;

/**
 * @author -Nemesiss-
 */
public class NioServer
{
	private static Logger			log			= Logger.getLogger(NioServer.class.getName());

	private final static NioServer	instance	= new NioServer(Config.LOGIN_BIND_ADDRESS, Config.LOGIN_PORT,
													"127.0.0.1", 9014);

	// The host:port combination to listen on for aion clients
	private String					aionHostAddress;
	private int						aionPort;

	// The host:port combination to listen on for gameservers
	private String					gsHostAddress;
	private int						gsPort;

	// The channel on which we'll accept connections
	private ServerSocketChannel		serverChannel;
	private ServerSocketChannel		gameServerChannel;

	private Dispatcher				acceptDispatcher;
	private int						currentReadDispatcher;
	private Dispatcher[]			readDispatchers;
	private int						currentWriteDispatcher;
	private Dispatcher[]			writeDispatchers;

	private NioServer(String aionHostAddress, int aionPort, String gsHostAddress, int gsPort)
	{
		this.aionHostAddress = aionHostAddress;
		this.aionPort = aionPort;
		this.gsHostAddress = gsHostAddress;
		this.gsPort = gsPort;
		this.init();
	}

	public static final NioServer getInstance()
	{
		return instance;
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

	private final void init()
	{
		try
		{
			this.initDispatchers();
			// Create a new non-blocking server socket channel for clients and
			// gs
			this.serverChannel = ServerSocketChannel.open();
			this.gameServerChannel = ServerSocketChannel.open();
			this.serverChannel.configureBlocking(false);
			this.gameServerChannel.configureBlocking(false);

			// Bind the server socket to the specified address and port
			InetSocketAddress isa;
			if ("*".equals(aionHostAddress))
			{
				isa = new InetSocketAddress(aionPort);
				log.info("LoginServer listening on all available IPs on Port " + aionPort);
			}
			else
			{
				isa = new InetSocketAddress(aionHostAddress, aionPort);
				log.info("LoginServer listening on IP: " + aionHostAddress + " Port " + aionPort);
			}
			serverChannel.socket().bind(isa);

			if ("*".equals(gsHostAddress))
			{
				isa = new InetSocketAddress(gsPort);
				log.info("LoginServer listening on all available IPs on Port " + aionPort + " for GS connections");
			}
			else
			{
				isa = new InetSocketAddress(gsHostAddress, gsPort);
				log.info("LoginServer listening on IP: " + gsHostAddress + " Port " + gsPort + " for GS connections");
			}
			gameServerChannel.socket().bind(isa);

			// Register the server socket channel, indicating an interest in
			// accepting new connections
			getAcceptDispatcher().register(serverChannel, SelectionKey.OP_ACCEPT, new AionAcceptor());
			getAcceptDispatcher().register(gameServerChannel, SelectionKey.OP_ACCEPT, new GsAcceptor());
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "NioServer Initialization Error: " + e, e);
			throw new Error("NioServer Initialization Error!");
		}
	}

	private final void initDispatchers() throws IOException
	{
		acceptDispatcher = new Dispatcher("Accept");
		acceptDispatcher.start();

		if (Config.NIO_READ_THREADS > 0)
		{
			readDispatchers = new Dispatcher[Config.NIO_READ_THREADS];
			for (int i = 0; i < readDispatchers.length; i++)
			{
				readDispatchers[i] = new Dispatcher("Read-" + i);
				readDispatchers[i].start();
			}
		}

		if (Config.NIO_WRITE_THREADS > 0)
		{
			writeDispatchers = new Dispatcher[Config.NIO_WRITE_THREADS];
			for (int i = 0; i < writeDispatchers.length; i++)
			{
				writeDispatchers[i] = new Dispatcher("Write-" + i);
				writeDispatchers[i].start();
			}
		}
	}

	public final void shutdown()
	{
		Shutdown.logln("Closing ServerChannel...");
		try
		{
			serverChannel.close();
			Shutdown.log(" ServerChannel closed.");
		}
		catch (Exception e)
		{
			Shutdown.log("Error during closing ServerChannel, " + e, e);
		}

		Shutdown.logln("Closing WebServerChannel...");
		try
		{
			if (gameServerChannel != null)
			{
				gameServerChannel.close();
			}
			Shutdown.log(" WebServerChannel closed.");
		}
		catch (Exception e)
		{
			Shutdown.log("Error during closing WebServerChannel: ", e);
		}
		Shutdown.log(" Active connections: " + getActiveConnections());

		/** DC all */
		Shutdown.logln("Forced Disconnecting all connections...");
		disconnectAll();
		Shutdown.log(" Active connections: " + getActiveConnections());

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
