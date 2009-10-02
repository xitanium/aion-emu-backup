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

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author xavier
 *
 */
public class LifeStats
{
	protected int currentHp;
	protected int currentMp;
	protected int maxHp;
	protected int maxMp;
	private Creature owner;
	
	public LifeStats (Creature owner, int currentHp, int currentMp, int maxHp, int maxMp) {
		this.currentHp = currentHp;
		this.currentMp = currentMp;
		this.maxHp = maxHp;
		this.maxMp = maxMp;
		this.owner = owner;
	}
	
	public int getMaxHp () {
		return maxHp;
	}
	
	public int getMaxMp () {
		return maxMp;
	}
	
	public int getHp () {
		return currentHp;
	}
	
	public int getMp () {
		return currentMp;
	}
	
	public boolean isAlive () {
		return (currentHp>0);
	}
	
	public int reduceHp (int hpAmount) {
		currentHp -= hpAmount;
		if (currentHp<=0) {
			currentHp = 0;
		}
		return currentHp;
	}
	
	public int reduceMp (int mpAmount) {
		currentMp -= mpAmount;
		if (currentMp<=0) {
			currentMp = 0;
		}
		return currentMp;
	}
	
	public void setOwner (Creature owner) {
		this.owner = owner;
	}
	
	public void reset () {
		this.currentHp = this.maxHp;
		this.currentMp = this.maxMp;
	}
	
	public Creature getOwner () {
		return owner;
	}
}
