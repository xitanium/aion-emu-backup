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
package com.aionemu.gameserver.model.templates;

import com.aionemu.gameserver.model.PlayerClass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is only a container for Stats.
 *
 * Created on: 04.08.2009 14:59:10
 *
 * @author Aquanox
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "stats_template")
public class StatsTemplate
{
	@XmlAttribute(name = "class", required = true)
	protected PlayerClass playerClass;
	@XmlAttribute(name = "level", required = true)
	protected int level;

	@XmlElement(name = "maxHp", required = true)
	protected int maxHp;
	@XmlElement(name = "maxMp", required = true)
	protected int maxMp;
	
	@XmlElement(name = "power", required = true)
	protected int power;
	@XmlElement(name = "health", required = true)
	protected int health;
	@XmlElement(name = "agility", required = true)
	protected int agility;
	@XmlElement(name = "accuracy", required = true)
	protected int accuracy;
	@XmlElement(name = "knowledge", required = true)
	protected int knowledge;
	@XmlElement(name = "will", required = true)
	protected int will;

	@XmlElement(name = "walk_speed", required = true)
	protected int walkSpeed;
	@XmlElement(name = "run_speed", required = true)
	protected int runSpeed;
	@XmlElement(name = "fly_speed", required = true)
	protected int flySpeed;

	@XmlElement(name = "attack_speed", required = true)
	protected int attackSpeed;

	@XmlElement(name = "evasion", required = true)
	protected int evasion;
	@XmlElement(name = "block", required = true)
	protected int block;
	@XmlElement(name = "parry", required = true)
	protected int parry;

	@XmlElement(name = "main_hand_attack", required = true)
	protected int mainHandAttack;
	@XmlElement(name = "main_hand_accuracy", required = true)
	protected int mainHandAccuracy;
	@XmlElement(name = "main_hand_crit_rate", required = true)
	protected int mainHandCritRate;

	@XmlElement(name = "magic_accuracy", required = true)
	protected int magicAccuracy;

	/* ======================================= */

	public int getMaxHp()
	{
		return maxHp;
	}

	public int getMaxMp()
	{
		return maxMp;
	}

	/* ======================================= */

	public int getPower()
	{
		return power;
	}

	public int getHealth()
	{
		return health;
	}

	public int getAgility()
	{
		return agility;
	}

	public int getAccuracy()
	{
		return accuracy;
	}

	public int getKnowledge()
	{
		return knowledge;
	}

	public int getWill()
	{
		return will;
	}

	/* ======================================= */

	public int getWalkSpeed()
	{
		return walkSpeed;
	}

	public int getRunSpeed()
	{
		return runSpeed;
	}

	public int getFlySpeed()
	{
		return flySpeed;
	}

	public int getAttackSpeed()
	{
		return attackSpeed;
	}

	/* ======================================= */

	public int getEvasion()
	{
		return evasion;
	}

	public int getBlock()
	{
		return block;
	}

	public int getParry()
	{
		return parry;
	}

	/* ======================================= */

	public int getMainHandAttack()
	{
		return mainHandAttack;
	}

	public int getMainHandAccuracy()
	{
		return mainHandAccuracy;
	}

	public int getMainHandCritRate()
	{
		return mainHandCritRate;
	}

	/* ======================================= */

	public int getMagicAccuracy()
	{
		return magicAccuracy;
	}

	/* ======================================= */

	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public int getLevel()
	{
		return level;
	}
}
