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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;


/**
 * In this packets aion client is sending the players settings.
 * 
 * @author -Niato-
 * 
 */
public class CM_PLAYERS_SETTINGS extends AionClientPacket
{
	/**
	 * unkdata - encrypted?...
	 */
	byte[] unkdata;
	@Inject
	PlayerService playerservice;
	/**
	 * objectid of player
	 */
	int objectid;
	/**
	 * Constructs new instance of <tt>CM_PLAYERS_SETTINGS </tt> packet.
	 * @param opcode
	 */
	public CM_PLAYERS_SETTINGS(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		 readB(4);
		 unkdata = readB(getRemainingBytes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection	client = getConnection();
		objectid = client.getActivePlayer().getObjectId();
		playerservice.setPlayerPreferences(objectid, unkdata);
	}
	
}
