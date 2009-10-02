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

import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.model.gameobjects.stats.GameStats;
import com.aionemu.gameserver.model.gameobjects.stats.LifeStats;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class is representing movable objects, its base class for all in game objects that may move
 * 
 * @author -Nemesiss-
 * 
 */
public abstract class Creature extends VisibleObject
{
	private Creature  target;

	public Creature(int objId, CreatureController<? extends Creature> controller, WorldPosition position)
	{
		super(objId, controller, position);
	}

	public Creature getTarget()
	{
		return target;
	}

	public void setTarget(Creature creature)
	{
		target = creature;
	}

	/**
	 * Return CreatureController of this Creature object.
	 * 
	 * @return CreatureController.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CreatureController<? extends Creature> getController()
	{
		return (CreatureController<? extends Creature>) super.getController();
	}

	public abstract byte getLevel();
	
	public abstract LifeStats getLifeStats();
	public abstract void setLifeStats (LifeStats lifeStats);
	public abstract GameStats getGameStats();
	public abstract void setGameStats (GameStats gameStats);
	
//	public int getHP() {
//		return this.currentHP;
//	}
//	public int getMP() {
//		return this.currentMP;
//	}
//	public int getDP() {
//		return this.currentDP;
//	}
//	public abstract int getMaxHP();
//	public abstract int getMaxMP();
//	public abstract int getMaxDP();
//	public abstract void setHP(int hp);
//	public abstract void setMP(int mp);
//	public abstract void setDP(int dp);
//	public abstract int getPower();
//	public abstract int getBlock();
//	public abstract void onDie();
}
