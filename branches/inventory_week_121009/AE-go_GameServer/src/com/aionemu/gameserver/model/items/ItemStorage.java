/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author ATracer
 */
public class ItemStorage
{

	private List<Item> storageItems ;

	private int limit = 0;

	public ItemStorage(int limit)
	{
		this.limit = limit;
		storageItems = new ArrayList<Item>(limit);
	}

	/**
	 * @return the storageItems
	 */
	protected List<Item> getStorageItems()
	{
		return storageItems;
	}

	/**
	 * @return the limit
	 */
	protected int getLimit()
	{
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	protected void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * @param itemId
	 * @return Item by itemId or null if there is no such item
	 */
	public Item getItemFromStorageByItemId(int itemId)
	{
		int index = 0;

		for(Item item : storageItems)
		{
			ItemTemplate itemTemplate = item.getItemTemplate();
			if(itemTemplate.getItemId() == itemId)
			{
				return item;
			}
			index++;
		}

		return null;
	}

	/**
	 * @param itemId
	 * @return
	 */
	public int getSlotIdByItemId(int itemId)
	{
		int index = 0;

		for(Item item : storageItems)
		{
			ItemTemplate itemTemplate = item.getItemTemplate();
			if(itemTemplate.getItemId() == itemId)
			{
				return index;
			}
			index++;
		}

		return -1;
	}

	/**
	 * @return index of available slot
	 *  If storage is null - return -1
	 */
	protected int getNextAvailableSlot()
	{
		int index = 0;

		for(Item item : storageItems)
		{
			if(item == null)
			{
				return index;
			}
			index++;
		}
		return -1;
	}

	protected boolean isSlotEmpty(int slot)
	{
		return slot <= limit && storageItems.get(slot) != null;
	}

	/**
	 * @param item
	 * @return
	 */
	public boolean addItemToStorage(Item item)
	{
		Item existingItem = getItemFromStorageByItemId(item.getItemTemplate().getItemId());

		if(existingItem != null)
		{
			//TODO overflow check
			existingItem.increaseItemCount(item.getItemCount());
		}
		int availableSlot = getNextAvailableSlot();
		if(availableSlot != -1)
		{
			addItemToSlot(item, availableSlot);
		}
		return false;
	}

	/**
	 * @param item
	 * @param slot
	 * @return
	 */
	public boolean addItemToSlot(Item item, int slot)
	{
		if(slot > limit)
		{
			return false;
		}

		if(isSlotEmpty(slot))
		{
			storageItems.add(slot, item); //TODO stackable items
			return true;
		}

		return false;
	}
	
	/**
	 * @param item
	 * @return
	 */
	public boolean removeItemFromStorage(Item item)
	{
		int slot = getSlotIdByItemId(item.getItemTemplate().getItemId());
		if(slot != 1)
		{
			storageItems.remove(slot);
			return true;
		}
		
		return false;
	}
}
