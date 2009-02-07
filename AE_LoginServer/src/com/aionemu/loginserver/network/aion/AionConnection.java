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
package com.aionemu.loginserver.network.aion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

import com.aionemu.commons.network.AConnection;
import com.aionemu.loginserver.LoginController;
import com.aionemu.loginserver.network.crypt.LoginCrypt;
import com.aionemu.loginserver.network.crypt.ScrambledKeyPair;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-
 */
public class AionConnection extends AConnection
{
	private static final Logger	log	= Logger.getLogger(AionConnection.class.getName());

	public static enum State
	{
		CONNECTED, AUTHED_GG, AUTHED_LOGIN
	}

	private State				state;
	private LoginCrypt			loginCrypt;
	private ScrambledKeyPair	scrambledPair;
	private byte[]				blowfishKey;

	private String				account;
	private int					lastServer;
	private boolean				usesInternalIP;
	private SessionKey			sessionKey;
	private int					sessionId	= 1;

	public AionConnection(SocketChannel sc)
	{
		super(sc);
		state = State.CONNECTED;

		String ip = getIP();
		log.info("connection from: " + ip);
		usesInternalIP = ip.startsWith("192.168") || ip.startsWith("10.0") || ip.equals("127.0.0.1");

		scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		blowfishKey = LoginController.getInstance().getBlowfishKey();
		loginCrypt = new LoginCrypt();
		loginCrypt.setKey(blowfishKey);
	}

	public boolean usesInternalIP()
	{
		return usesInternalIP;
	}

	@Override
	public boolean processData(ByteBuffer data)
	{
		decrypt(data);
		AionClientPacket pck = AionPacketHandler.handle(data, this);
		log.info("recived packet: " + pck);
		if (pck != null)
			ThreadPoolManager.getInstance().executeAionPacket(pck);
		return true;
	}

	public boolean decrypt(ByteBuffer buf)
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
			close();
			return false;
		}

		if (!ret)
		{
			byte[] dump = new byte[size];
			System.arraycopy(buf.array(), buf.position(), dump, 0, size);
			log.warning("Wrong checksum from client: " + this.toString());
			// TODO!
			// close();
			ret = true;
		}

		return ret;
	}

	public int encrypt(ByteBuffer buf)
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

	public void sendPacket(AionServerPacket bp)
	{
		log.info("sending packet: " + bp);
		bp = (AionServerPacket) bp.setConnection(this);
		sendMsgQueue.put(bp);
		enableWriteInterest();
	}

	public byte[] getBlowfishKey()
	{
		return blowfishKey;
	}

	public byte[] getScrambledModulus()
	{
		return scrambledPair._scrambledModulus;
	}

	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) scrambledPair._pair.getPrivate();
	}

	public int getSessionId()
	{
		return sessionId;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public SessionKey getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(SessionKey sessionKey)
	{
		this.sessionKey = sessionKey;
		// TODO! register etc
	}

	/**
	 * This will close the Connection And take care of everything that should be done on disconnection (onDisconnect())
	 * if the active char is not nulled yet
	 */
	@Override
	public void close()
	{
		onlyClose();
	}

	@Override
	public void exception(IOException e, boolean read)
	{
		log.info("exception " + e);
		close();
	}

	@Override
	public void terminate()
	{
		log.info("terminate!");
	}
}
