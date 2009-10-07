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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author xavier
 *
 */
public class NpcGameStats extends CreatureGameStats<Npc>
{

	public NpcGameStats () {
		super(null,0,0,0,0,0,0,0,0,0,0);
	}
	/**
	 * @param owner
	 * @param power
	 * @param health
	 * @param agility
	 * @param accuracy
	 * @param knowledge
	 * @param will
	 * @param mainHandAttack
	 * @param mainHandCritRate
	 * @param otherHandAttack
	 * @param otherHandCritRate
	 * @param attackCounter
	 */
	public NpcGameStats(Npc owner, int power, int health, int agility, int accuracy, int knowledge, int will, int mainHandAttack, int mainHandCritRate, int otherHandAttack, int otherHandCritRate)
	{
		super(owner, power, health, agility, accuracy, knowledge, will, mainHandAttack, mainHandCritRate, otherHandAttack, otherHandCritRate);
	}
}
