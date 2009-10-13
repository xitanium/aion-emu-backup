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
package com.aionemu.gameserver.model;

/**
 * This enum is defining inventory slots, to which items can be equipped.
 * @author xavier
 *
 */
public enum ItemSlot
{
	NONE(0),
	UNK1(1),
	UNK2(2),
	UNK3(3),
	UNK4(4),
	MAIN_HAND(5),
	UNK6(6),
	UNK7(7),
	UNK8(8),
	ARMOR(9),
	UNK10(10),
	UNK11(11),
	PANTS(12),
	UNK13(13),
	UNK14(14),
	UNK15(15),
	UNK16(16);
	
	private int slotId;
	
	private ItemSlot(int slotId)
	{
		this.slotId = slotId;
	}
	
	public int getSlotId()
	{
		return slotId;
	}
	
	public short getSlotMask() {
		return (short)Math.round(Math.pow(2, slotId));
	}
}
