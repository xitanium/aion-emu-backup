/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;

/**
 * In this packet Server is sending Inventory Info?
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * @finisher Avol ;d
 */
public class SM_INVENTORY_INFO extends AionServerPacket
{
	// private int uniqueItemId;
	// private int itemId;
	// private int itemNameId;
	// private int itemQuanty;
	// private int entries;
	private int				ownerId;
	// private ItemSlot slot;
	@SuppressWarnings("unused")
	private static Logger	log	= Logger.getLogger(SM_INVENTORY_INFO.class);

	/**
	 * Constructs new <tt>SM_INVENTORY_INFO </tt> packet
	 */
	public SM_INVENTORY_INFO(int ownerId)
	{
		// this.uniqueItemId = uniqueItemId;
		// this.itemId = itemId;
		// this.itemQuanty = itemQuanty;
		// this.entries = entries;
		// this.slot = slot;
		this.ownerId = ownerId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		// get item name id.
		Inventory inventory = new Inventory();
		inventory.getInventoryFromDb(ownerId);
		int itemsCount = inventory.getItemsCount();
		// ItemList itemName = new ItemList();
		// itemName.getItemList(itemId);
		// itemNameId = itemName.getItemNameId();

		// ItemList itemSlot = new ItemList();

		// itemSlot.getItemList(itemId);
		// String slotName = itemSlot.getEquipmentSlots();

		// char test = slotName.charAt(0);
		// boolean isAnInt= test>='0' && test<='9';
		//	
		// if (isAnInt){
		// try {
		// slot = ItemSlot.values()[Integer.parseInt(slotName)];
		// } catch (Exception e) {
		// log.error("Invalid item slot "+slotName);
		// slot = ItemSlot.NONE;
		// }
		// } else {
		// slot = ItemSlot.NONE;
		// }

		// something wrong with cube part.

		writeC(buf, 1); // TRUE/FALSE (1/0) update cube size
		writeC(buf, 0); // cube size
		writeH(buf, 0); // padding?
		writeH(buf, itemsCount); // number of entries

		for(int itemNumber = 0; itemNumber < itemsCount; itemNumber++)
		{
			ItemList itemName = new ItemList();
			int itemId = inventory.getItemIdArray(itemNumber);
			itemName.getItemList(itemId);
			int itemNameId = itemName.getItemNameId();
			ItemSlot itemSlot = inventory.getItemSlotArray(itemNumber);
			writeD(buf, inventory.getItemUniqueIdArray(itemNumber)); // Unique Id
			writeD(buf, inventory.getItemIdArray(itemNumber)); // item Id 162000007
			writeH(buf, 0x24); // always 36
			writeD(buf, itemNameId); // item name id
			writeH(buf, 0); // always 0
			writeH(buf, 0x16); // lenght of item details
			if(itemSlot != ItemSlot.INVENTORY)
			{
				writeC(buf, 0x06); // equiped data follows
				writeC(buf, inventory.getItemSlotArray(itemNumber).getSlotMask()&0x00FF); // where this can be equiped. or
				writeC(buf, inventory.getItemSlotArray(itemNumber).getSlotMask()&0xFF00);// whatever

				// ---------------

				writeC(buf, 0x02);
				writeC(buf, 0x00);
				writeC(buf, 0x08);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeH(buf, 0);
				writeC(buf, 0);
				/*
				 * //--------------- writeC(buf, 0x0B); // appearance info follows? writeH(buf, 0); writeD(buf,
				 * 0x7C85AE06); // changing this value tags item as skinned writeD(buf, 0); // 4608 manastone type
				 * writeD(buf, 0); // 14 mana stone atribute bonus writeD(buf, 0); writeD(buf, 0); writeD(buf, 0);
				 * writeD(buf, 0); writeC(buf, 0); writeC(buf, 0); writeC(buf, 0); //------------ writeC(buf, 0x0A);
				 * writeD(buf, 196628); writeC(buf, 0); writeC(buf, 0); writeC(buf, 0); //------------ writeC(buf,
				 * 0x0A); writeD(buf, 20971923); writeC(buf, 0); writeC(buf, 0); writeC(buf, 0); //------------
				 * writeC(buf, 0x0A); writeD(buf, 327784); writeC(buf, 0); writeC(buf, 0); writeC(buf, 0);
				 * //------------
				 */

				writeC(buf, 0); // general info fallows
				writeH(buf, 0xFFFF); // sets the varios bits of attribute test on the tooltip
				writeC(buf, inventory.getItemCountArray(itemNumber)); // quanty
			}
			else
			{
				writeC(buf, 0x00);
				writeH(buf, 0x633E);
				writeD(buf, inventory.getItemCountArray(itemNumber));
				writeD(buf, 0);
				writeD(buf, 0);
				writeH(buf, 0);
				writeC(buf, 0);
				writeD(buf, 0);
//				writeD(buf, 0);
//				writeD(buf, 0);
//				writeD(buf, 0);
//				writeD(buf, 0);
//				writeD(buf, 0);
//				writeH(buf, 0);
				writeC(buf, 0);
				writeC(buf, 0x18); // location in inventory -?
				writeC(buf, 0); //
				writeC(buf, 0); // sometimes 0x01
			}
		}
	}
}
