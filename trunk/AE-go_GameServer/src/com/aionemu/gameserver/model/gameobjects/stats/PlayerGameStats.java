/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerStatsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 *
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int itemId; //TODO remove
	private int itemNameId; //TODO remove
	private int itemCount; //todo remove

	public PlayerGameStats () {
		super(null,0,0,0,0,0,0,0,0,0,0,0,0);
		this.setInitialized(false);
	}
	
	public PlayerGameStats (Player player, int power, int health, int agility, int accuracy, int knowledge, int will, int mainHandAttack, int mainHandCritRate, int otherHandAttack, int otherHandCritRate, float attackSpeed, float attackRange)
	{
		super(player,power,health,agility,accuracy,knowledge,will,mainHandAttack,mainHandCritRate,otherHandAttack,otherHandCritRate,attackSpeed,attackRange);
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return the itemNameId
	 */
	public int getItemNameId()
	{
		return itemNameId;
	}

	/**
	 * @param itemNameId the itemNameId to set
	 */
	public void setItemNameId(int itemNameId)
	{
		this.itemNameId = itemNameId;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount()
	{
		return itemCount;
	}

	/**
	 * @param itemCount
	 */
	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
	}
	
	// TODO Find the good stats evolution rates according to level
	public void doEvolution (int fromLevel, int toLevel) {
		setPower(getPower() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setHealth(getHealth() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setAgility(getAgility() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setAccuracy(getAccuracy() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setKnowledge(getKnowledge() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setWill(getWill() + (int) Math.round((toLevel - fromLevel) * 1.1688));
		setMainHandAttack(getMainHandAttack() + (int) Math.round((toLevel - fromLevel) * 0.108));
		setMainHandCritRate(getMainHandCritRate() + (int) Math.round((toLevel - fromLevel) * 0.108));
		setOffHandAttack(getOffHandAttack() + (int) Math.round((toLevel - fromLevel) * 0.108));
		setOffHandCritRate(getOffHandCritRate() + (int) Math.round((toLevel - fromLevel) * 0.108));
		setAttackSpeed(getAttackSpeed() + (int) Math.round((toLevel - fromLevel) * 0.108));
		setAttackRange(getAttackRange() + (int) Math.round((toLevel - fromLevel) * 0.108));
		DAOManager.getDAO(PlayerStatsDAO.class).storeGameStats(this.getOwner().getObjectId(), this);
	}
}
