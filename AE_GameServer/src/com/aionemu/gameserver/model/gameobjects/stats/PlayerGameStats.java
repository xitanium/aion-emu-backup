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

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author xavier
 * 
 */
public class PlayerGameStats extends GameStats 
{	
	public PlayerGameStats (Player player, int power, int health, int agility, int accuracy, int knowledge, int will, int mainHandAttack, int mainHandCritRate, int otherHandAttack, int otherHandCritRate)
	{
		super(player,power,health,agility,accuracy,knowledge,will,mainHandAttack,mainHandCritRate,otherHandAttack,otherHandCritRate, 0);
	}
	
	@Override
	public Player getOwner () {
		return (Player)super.getOwner();
	}
}
