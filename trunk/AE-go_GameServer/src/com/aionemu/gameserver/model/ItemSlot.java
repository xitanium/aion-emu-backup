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
	INVENTORY(0),
	MAIN_HAND(1),
	OFF_HAND(2),
	HELMET(3),
	TORSO(4),
	GLOVES(5),
	BOOTS(6),
	EARRINGS_LEFT(7),
	EARRINGS_RIGHT(8),
	RING_LEFT(9),
	RING_RIGHT(10),
	NECKLACE(11),
	PAULDRON(12),
	PANTS(13),
	POWER_SHARD_LEFT(14),
	POWER_SHARD_RIGHT(15),
	WINGS(16),
	WAIST(17),
	SECONDARY_MAIN_HAND(18),
	SECONDARY_OFF_HAND(19);
	
	private int slotId;
	
	private ItemSlot(int slotId)
	{
		this.slotId = slotId;
	}
	
	public int getSlotId()
	{
		return slotId;
	}
	
	public short getSlotMask() throws IllegalArgumentException {
		if ((slotId<0)||(slotId>=ItemSlot.values().length)) {
			throw new IllegalArgumentException("Invalid slot id "+slotId);
		}
		if (slotId==0)
			return 0;
		return (short)Math.round(Math.pow(2, slotId-1));
	}
}
