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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * @author xitanium
 *
 */
public class CM_PRIVATESTORE_START extends AionClientPacket
{
	private int unk1; // 1
	private int unk2;
	private int unk3;
	/**
	 * 
	 */
	public CM_PRIVATESTORE_START(int opcode)
	{
		// TODO Auto-generated constructor stub
		super(opcode);
	}
	
	protected void readImpl() 
	{
		unk1 = readH();
		unk2 = readC();
	}
	
	protected void runImpl() 
	{
		
	}
}
