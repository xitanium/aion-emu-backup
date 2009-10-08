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
import com.aionemu.gameserver.model.SkillTargetType;
import com.aionemu.gameserver.model.SkillType;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "skill_template")
public class SkillTemplate
{
	@XmlAttribute(name ="skill_id", required = true)
	private int	skill_id;
	
	@XmlAttribute(name = "skill_type")
	private SkillType skill_type;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "level", required = true)
	private int level;

	@XmlAttribute(name = "element")
	private SkillElement element;
	
	@XmlAttribute(name = "inflicts")
	private int inflicts;
	
	@XmlAttribute(name = "heals")
	private int heals;
	
	@XmlAttribute(name = "cost")
	private int cost;
	
	@XmlAttribute(name = "probability")
	private int probability;

	@XmlAttribute(name = "target")
	private SkillTargetType target;
	
	@XmlAttribute(name = "recharge_time")
	private int recharge_time;
	
	@XmlAttribute(name = "launch_time")
	private int launch_time;

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skill_id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public SkillElement getElement()
	{
		if (element == null) {
			element = SkillElement.NONE;
		}
		return element;
	}
	/**
	 * @return the skillType
	 */
	public SkillType getType()
	{
		if (skill_type==null) {
			skill_type = SkillType.DEFAULT;
		}
		return skill_type;
	}

	/**
	 * @return the gain
	 */
	public int getGain()
	{
		return heals;
	}
	
	/**
	 * @return the damages
	 */
	public int getDamages()
	{
		return inflicts;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the duration
	 */
	public int getRechargeTime ()
	{
		return recharge_time;
	}

	public int getLaunchTime ()
	{
		return launch_time;
	}
	/**
	 * @return the target
	 */
	public SkillTargetType getTarget()
	{
		if (target==null) {
			target = SkillTargetType.TARGET;
		}
		return target;
	}
	/**
	 * @return the probability
	 */
	public int getProbability()
	{
		return probability;
	}
	/**
	 * @return the cost in MP
	 */
	public int getCost()
	{
		return cost;
	}
}
