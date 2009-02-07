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
package com.aionemu.loginserver.network.gameserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import com.aionemu.commons.network.AConnection;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-
 */
public class GsConnection extends AConnection
{
	private static final Logger	log	= Logger.getLogger(GsConnection.class.getName());

	public static enum State
	{
		CONNECTED, AUTHED
	}

	private State	state;

	public GsConnection(SocketChannel sc)
	{
		super(sc);
		state = State.CONNECTED;

		String ip = getIP();
		log.info("GS connection from: " + ip);
	}

	@Override
	public boolean processData(ByteBuffer data)
	{
		GsClientPacket pck = GsPacketHandler.handle(data, this);
		log.info("recived packet: " + pck);
		if (pck != null)
			ThreadPoolManager.getInstance().executeGsPacket(pck);
		return true;
	}

	public void sendPacket(GsServerPacket bp)
	{
		log.info("sending GS packet: " + bp);
		bp = (GsServerPacket) bp.setConnection(this);
		sendMsgQueue.put(bp);
		enableWriteInterest();
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	@Override
	public void close()
	{
		GameServerTable.unregisterGameServer(this);
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
		log.info("gs terminate!");
	}
}
