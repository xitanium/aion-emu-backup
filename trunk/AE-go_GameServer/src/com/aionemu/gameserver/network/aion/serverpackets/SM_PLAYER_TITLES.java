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
 *  
 *  @author xitanium
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PLAYER_TITLES extends AionServerPacket {
	
	private int titleId;
	private Player player;
	
	public SM_PLAYER_TITLES(int titleID) {
		titleId = titleID;
	}
	public void writeImpl(AionConnection con, ByteBuffer buf) {
		
		// write opcode (OxAE)
		writeOP(buf, getOpcode());
		writeC(buf, 0);
		// write count of player titles
		writeH(buf, 1);
		//TODO: make loop on player titles (player.getTitles())
		// here we write the titleid
		writeD(buf, titleId);
		
		// close packet
		writeD(buf, 0);
		
	}
	
}