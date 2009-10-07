/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.skillengine.SkillType;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "skill_template")
public class SkillTemplate
{
	@XmlAttribute(name ="skill_id", required = true)
	private int	skillId;
	
	@XmlAttribute(name = "skill_type")
	private SkillType skillType;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "element")
	private SkillElement element;
	
	@XmlAttribute(name = "level", required = true)
	private int level;
	
	@XmlAttribute(name = "damages")
	private int damages;
	
	@XmlAttribute(name = "cost")
	private int cost;
	
	@XmlAttribute(name = "probability")
	private float probability;
	
	@XmlAttribute(name = "recharge_time")
	private int recharge_time;
	
	@XmlAttribute(name = "launch_time")
	private int launch_time;
	
	@XmlAttribute(name = "scope")
	private int scope;

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skillId;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the element
	 */
	public SkillElement getElement()
	{
		return element;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the damages
	 */
	public int getDamages()
	{
		return damages;
	}

	/**
	 * @return the cost
	 */
	public int getCost()
	{
		return cost;
	}

	/**
	 * @return the probability
	 */
	public float getProbability()
	{
		return probability;
	}

	/**
	 * @return the recharge_time
	 */
	public int getRechargeTime()
	{
		return recharge_time;
	}

	/**
	 * @return the launch_time
	 */
	public int getLaunchTime()
	{
		return launch_time;
	}

	/**
	 * @return the scope
	 */
	public int getScope()
	{
		return scope;
	}
	
	public SkillType getType () {
		if (skillType==null) {
			return SkillType.DEFAULT;
		}
		return skillType;
	}
}
