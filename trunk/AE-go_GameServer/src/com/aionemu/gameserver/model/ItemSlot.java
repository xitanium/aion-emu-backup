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

import java.util.ArrayList;
import java.util.List;

/**
 * This enum is defining inventory slots, to which items can be equipped.
 * @author xavier
 *
 */
public enum ItemSlot
{
	INVENTORY(0x0000),
	MAIN_HAND(0x0001),
	OFF_HAND(0x0002),
	BOTH_HANDS(0x0003),
	HELMET(0x0004),
	TORSO(0x0008),
	GLOVES(0x0010),
	BOOTS(0x0020),
	EARRINGS_LEFT(0x0040),
	EARRINGS_RIGHT(0x0080),
	RING_LEFT(0x0100),
	RING_RIGHT(0x0200),
	NECKLACE(0x0400),
	PAULDRON(0x0800),
	PANTS(0x1000),
	POWER_SHARD_LEFT(0x2000),
	POWER_SHARD_RIGHT(0x4000),
	WINGS(0x8000),
	WAIST(0x10000),
	SECONDARY_MAIN_HAND(0x20000),
	SECONDARY_OFF_HAND(0x40000);
	
	private int slotMask;
	
	private static final List<ItemSlot> slotIds = new ArrayList<ItemSlot> ();
	
	static {
		for (ItemSlot slot : ItemSlot.values()) {
			slotIds.add(slot);
		}
	}
	
	private ItemSlot(int slotMask)
	{
		this.slotMask = slotMask;
	}
	
	public static ItemSlot getItemSlot (int slotId) throws AssertionError {
		if (slotIds.get(slotId)==null) {
			throw new AssertionError ("Invalid slot id "+slotId);
		}
		return slotIds.get(slotId);
	}
	
	public int getSlotId () {
		return slotIds.indexOf(this);
	}
	
	public int getSlotMask() {
		return slotMask;
	}
}
