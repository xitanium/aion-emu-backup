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
package com.aionemu.loginserver.network.aion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.loginserver.LoginController;
import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.aion.serverpackets.SM_INIT;
import com.aionemu.loginserver.network.crypt.LoginCrypt;
import com.aionemu.loginserver.network.crypt.ScrambledKeyPair;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * Object representing connection between LoginServer and Aion Client.
 * @author -Nemesiss-
 */
public class AionConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(AionConnection.class);

	/**
	 * Possible states of AionConnection
	 */
	public static enum State
	{
		CONNECTED, AUTHED_GG, AUTHED_LOGIN
	}

	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<AionServerPacket>	sendMsgQueue	= new ArrayDeque<AionServerPacket>();

	/**
	 * Current state of this connection
	 */
	private State							state;
	/**
	 * Crypt to encrypt/decrypt packets
	 */
	private LoginCrypt						loginCrypt;
	private ScrambledKeyPair				scrambledPair;
	private byte[]							blowfishKey;

	/**
	 * Account object for this connection.
	 * if state = AUTHED_LOGIN account cant
	 * be null.
	 * 
	 */
	private Account account;
	/**
	 * Last played server
	 */
	private int								lastServer;
	/**
	 * If this connection should use internalIp
	 * for reconnecting to GameServer [ie when GameServer
	 * is in the same local net as client]
	 */
	private boolean							usesInternalIP;
	/**
	 * Session Key for this connection.
	 */
	private SessionKey						sessionKey;
	/**
	 * Unique Session Id of this connection
	 */
	private int								sessionId		= Rnd.nextInt();
	/**
	 * True if this user is connecting to GS.
	 */
	private boolean joinedGs;

	/**
	 * Constructor
	 * 
	 * @param sc
	 * @param d
	 * @throws IOException
	 */
	public AionConnection(SocketChannel sc, Dispatcher d) throws IOException
	{
		super(sc, d);
		state = State.CONNECTED;

		String ip = getIP();
		log.info("connection from: " + ip);
		usesInternalIP = ip.startsWith("192.168") || ip.startsWith("10.0") || ip.equals("127.0.0.1");

		scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		blowfishKey = LoginController.getInstance().getBlowfishKey();
		loginCrypt = new LoginCrypt();
		loginCrypt.setKey(blowfishKey);

		/** Send Init packet */
		sendPacket(new SM_INIT(this));
	}

	/**
	 * Called by Dispatcher. ByteBuffer data contains one packet that should be processed.
	 * 
	 * @param data
	 * @return True if data was processed correctly, False if some error occurred and connection should be closed NOW.
	 */
	@Override
	protected final boolean processData(ByteBuffer data)
	{
		if(!decrypt(data))
			return false;

		AionClientPacket pck = AionPacketHandler.handle(data, this);
		log.debug("recived packet: " + pck);
		if (pck != null)
			ThreadPoolManager.getInstance().executeAionPacket(pck);
		return true;
	}

	/**
	 * This method will be called by Dispatcher, and will be repeated till return false.
	 * 
	 * @param data
	 * @return True if data was written to buffer, False indicating that there are not any more data to write.
	 */
	@Override
	protected final boolean writeData(ByteBuffer data)
	{
		synchronized(guard)
		{
			AionServerPacket packet = sendMsgQueue.pollFirst();
			if(packet == null)
				return false;

			packet.write(this, data);
			return true;
		}
	}

	/**
	 * This method is called by Dispatcher when connection is ready to be closed.
	 * 
	 * @return time in ms after witch onDisconnect() method will be called. Always return 0.
	 */
	@Override
	protected final long getDisconnectionDelay()
	{
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onDisconnect()
	{
		/**
		 * Remove account only if not joined GameServer yet.
		 */
        if(account != null && !joinedGs){
            AccountController.removeAccountOnLS(account);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onServerClose()
	{
		// TODO mb some packet should be send to client before closing?
		close(/*packet,*/ true);
	}

	/**
	 * Decrypt packet.
	 * @param buf
	 * @return true if success
	 */
	private boolean decrypt(ByteBuffer buf)
	{
		int size = buf.remaining();
		final int offset = buf.arrayOffset() + buf.position();
		boolean ret = false;
		try
		{
			ret = loginCrypt.decrypt(buf.array(), offset, size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		if (!ret)
		{
			byte[] dump = new byte[size];
			System.arraycopy(buf.array(), buf.position(), dump, 0, size);
			log.warn("Wrong checksum from client: " + this);
		}
		return ret;
	}

	/**
	 * Encrypt packet.
	 * @param buf
	 * @return encrypted packet size.
	 */
	public final int encrypt(ByteBuffer buf)
	{
		int size = buf.limit() - 2;
		final int offset = buf.arrayOffset() + buf.position();
		try
		{
			size = loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
		return size;
	}

	/**
	 * Sends AionServerPacket to this client.
	 * 
	 * @param bp
	 *            AionServerPacket to be sent.
	 */
	public final void sendPacket(AionServerPacket bp)
	{
		synchronized (guard)
		{
			/**
			 * Connection is already closed or waiting for last (close packet) to be sent
			 */
			if (isWriteDisabled())
				return;

			log.debug("sending packet: " + bp);

			sendMsgQueue.addLast(bp);
			enableWriteInterest();
		}
	}

	/**
	 * Its guaranted that closePacket will be sent before closing connection, but all past and future packets wont.
	 * Connection will be closed [by Dispatcher Thread], and onDisconnect() method will be called to clear all other
	 * things. forced means that server shouldn't wait with removing this connection.
	 * 
	 * @param closePacket
	 *            Packet that will be send before closing.
	 * @param forced
	 *            have no effect in this implementation.
	 */
	public final void close(AionServerPacket closePacket, boolean forced)
	{
		synchronized (guard)
		{
			if (isWriteDisabled())
				return;

			log.info("sending packet: " + closePacket+ " and closing connection after that.");

			pendingClose = true;
			isForcedClosing = forced;
			sendMsgQueue.clear();
			sendMsgQueue.addLast(closePacket);
			enableWriteInterest();
		}
	}

	/**
	 * True if this connection should use internalIp
	 * for reconnecting to GameServer [ie when GameServer
	 * is in the same local net as client]
	 * @return usesInternalIP
	 */
	public final boolean usesInternalIP()
	{
		return usesInternalIP;
	}

	public final byte[] getBlowfishKey()
	{
		return blowfishKey;
	}

	public final byte[] getScrambledModulus()
	{
		return scrambledPair._scrambledModulus;
	}

	public final RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) scrambledPair._pair.getPrivate();
	}

	/**
	 * Returns unique sessionId of this connection.
	 * @return SessionId
	 */
	public final int getSessionId()
	{
		return sessionId;
	}

	/**
	 * Current state of this connection
	 * @return state
	 */
	public final State getState()
	{
		return state;
	}

	/**
	 * Set current state of this connection
	 * @param state
	 */
	public final void setState(State state)
	{
		this.state = state;
	}

	/**
	 * Returns Account object that this
	 * client logged in or null
	 * @return Account
	 */
	public final Account getAccount()
	{
		return account;
	}

	/**
	 * Set Account object for this connection.
	 * @param account
	 */
	public final void setAccount(Account account)
	{
		this.account = account;
	}

	/**
	 * Returns Session Key of this connection
	 * @return SessionKey
	 */
	public final SessionKey getSessionKey()
	{
		return sessionKey;
	}

	/**
	 * Set Session Key for this connection
	 * @param sessionKey
	 */
	public final void setSessionKey(SessionKey sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	/**
	 * Set joinedGs value to true
	 */
	public final void setJoinedGs()
	{
		joinedGs = true;
	}

	/**
	 * @return String info about this connection
	 */
	@Override
	public String toString()
	{
		return account != null? account+" "+getIP() : "not loged "+getIP();
	}
}
