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
 */
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author ATracer
 */
public class Item extends AionObject
{
	
	private int itemCount = 1;
	
	private ItemTemplate itemTemplate;
	
	/**
	 * @param objId
	 */
	public Item(int objId)
	{
		super(objId);
	}

	@Override
	public String getName()
	{
		//TODO
		//item description should return probably string and not id
		return String.valueOf(itemTemplate.getDescription());
	}

	/**
	 * @return the itemTemplate
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
	}

	/**
	 * @param itemTemplate the itemTemplate to set
	 */
	public void setItemTemplate(ItemTemplate itemTemplate)
	{
		this.itemTemplate = itemTemplate;
	}

	/**
	 * @return the itemCount
	 *  Number of this item in stack. Should be not more than template maxstackcount ?
	 */
	public int getItemCount()
	{
		return itemCount;
	}

	/**
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
	}
	
	/**
	 * @param addCount 
	 */
	public void increaseItemCount(int addCount)
	{
		//TODO overflow check
		this.itemCount += addCount;
	}
}
