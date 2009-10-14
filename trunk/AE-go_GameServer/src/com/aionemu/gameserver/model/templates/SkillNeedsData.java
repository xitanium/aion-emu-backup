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
package com.aionemu.gameserver.model.templates;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author xavier
 *
 */
@XmlRootElement(name = "needs")
@XmlAccessorType(XmlAccessType.NONE)
public class SkillNeedsData
{
	@XmlAttribute(name = "item", required=true)
	private int itemId;

	@XmlAttribute(name ="quantity")
	private int quantity;

	/**
	 * @return the item identifier
	 */
	public int getNeededItemId () {
		return itemId;
	}

	/**
	 * @return the quantity of needed items
	 */
	public int getQuantityNeeded () {
		return quantity;
	}
}
