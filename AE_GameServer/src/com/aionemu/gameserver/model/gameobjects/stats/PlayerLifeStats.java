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
public class PlayerLifeStats extends LifeStats
{	
	private int currentDp;
	private int maxDp;
	/**
	 * @param currentHp
	 * @param currentMp
	 * @param maxHp
	 * @param maxMp
	 */
	public PlayerLifeStats(Player player, int currentHp, int currentMp, int currentDp, int maxHp, int maxMp, int maxDp)
	{
		super(player, currentHp, currentMp, maxHp, maxMp);
		this.currentDp = currentDp;
		this.maxDp = maxDp;
	}
	
	public void setDp (int dp) {
		this.currentDp = dp;
	}
	
	public void setHp (int hp) {
		this.currentHp = hp;
	}
	
	public void setMp (int mp) {
		this.currentMp = mp;
	}
	
	public int getDp () {
		return currentDp;
	}
	
	public int getMaxDp () {
		return maxDp;
	}
	
	@Override
	public Player getOwner () {
		return (Player)super.getOwner();
	}
}
