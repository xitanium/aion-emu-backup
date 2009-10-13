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
	OFF_HAND(1),
	HELMET(2),
	EARRINGS_LEFT(3),
	GLOVES(4),
	BOOTS(5),
	MAIN_HAND(6),
	EARRINGS_RIGHT(7),
	RING_LEFT(8),
	RING_RIGHT(9),
	TORSO(10),
	PAULDRON(11),
	NECKLACE(12),
	PANTS(13),
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
		if ((slotId<0)||(slotId>16)) {
			throw new IllegalArgumentException("Invalid slot id "+slotId);
		}
		if (slotId==0)
			return 0;
		return (short)Math.round(Math.pow(2, slotId-1));
	}
}
