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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;

/**
 * This packet is displaying visible npc/monsters.
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_NPC_INFO extends AionServerPacket
{
	/**
	 * Visible npc
	 */
	private final Npc		npc;

	private final byte[]	unk2	= new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	/**
	 * Constructs new <tt>SM_NPC_INFO </tt> packet
	 * 
	 * @param npc
	 *            visible npc.
	 */
	public SM_NPC_INFO(Npc npc)
	{
		this.npc = npc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeF(buf, npc.getX());// x
		writeF(buf, npc.getY());// y
		writeF(buf, npc.getZ());// z
		writeD(buf, npc.getObjectId());
		writeD(buf, npc.getNpcId());
		writeD(buf, npc.getNpcId());

		writeC(buf, 0x26);// unk 0x00, 0x26=not attackable?
		writeC(buf, 0x41);// unk 0x41=normal,0x47=no drop,0x21=fight state,0x07=no drop
		writeC(buf, 0x00);// unk
		writeC(buf, npc.getHeading());
		writeD(buf, npc.getNpcNameId());

		writeD(buf, 0x00);// titleID
		writeD(buf, 0x00);// unk
		writeD(buf, 0x00);// unk
		writeD(buf, 0x00);// unk
		writeC(buf, 0x00);// unk
		writeC(buf, 100);// %hp

		writeD(buf, 0xB91);// unk 0x8F,0xAC, 0xB91
		writeC(buf, 1);// lvl
		writeH(buf, 0x00);// items count!
		// for(items count)
		// {
		// D - itemId
		// D - unk = 0
		// D - unk = 0
		// }

		writeD(buf, 1051931443);// 0x3F066666
		writeD(buf, 1073741824);// 0x3F7AE148
		writeD(buf, 1069547520);// 0x3F19999A

		writeH(buf, 2000);// 0x834
		writeH(buf, 2000);// 0x834
		writeC(buf, 0x00);// unk

		/**
		 * Movement
		 */
		writeF(buf, /* npc.getX() */0);// x
		writeF(buf, /* npc.getY() */0);// y
		writeF(buf, /* npc.getZ() */0);// z
		writeC(buf, 0x00); // move type

		writeB(buf, unk2);

		writeH(buf, 1);// unk
		writeC(buf, 0x00);// unk
	}
}
