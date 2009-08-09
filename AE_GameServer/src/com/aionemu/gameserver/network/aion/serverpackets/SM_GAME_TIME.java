/*
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;

/**
 * Sends the current time in the server in minutes since 1/1/00 00:00:00
 * 
 * @author Ben
 *
 */
public class SM_GAME_TIME extends AionServerPacket
{
	int time;
	/**
	 * 
	 * @param minutes Minutes since 1/1/00 00:00:00
	 */
	public SM_GAME_TIME() 
	{
		super(0x27);
		time = GameTimeManager.getGameTime().getTime();
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.network.aion.AionServerPacket#writeImpl(com.aionemu.gameserver.network.aion.AionConnection, java.nio.ByteBuffer)
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeOP(buf, getOpcode()); // Opcode
		writeD(buf, time); // Minutes since 1/1/00 00:00:00
	}

}
