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

import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * @author ATracer, Avol
 * 
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int				itemId;			// TODO remove
	private int				itemNameId;		// TODO remove
	private int				itemCount;			// todo remove
	private PlayerStatsData	playerStatsData;
	private int[]			itemIdArray;
	private int[]			itemCountArray;
	private int				itemIdArrayLength;

	public PlayerGameStats()
	{
		super(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		setInitialized(false);
		this.playerStatsData = null;
	}

	public PlayerGameStats(PlayerStatsData playerStatsData, Player owner)
	{
		super(owner, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		this.playerStatsData = playerStatsData;
		PlayerStatsTemplate pst = playerStatsData.getTemplate(owner.getPlayerClass(), 1);
		setAttackCounter(1);
		setPower(pst.getPower());
		setHealth(pst.getHealth());
		setAgility(pst.getAgility());
		setAccuracy(pst.getAccuracy());
		setKnowledge(pst.getKnowledge());
		setWill(pst.getWill());
		setMainHandAttack(pst.getMainHandAttack());
		setMainHandCritRate(pst.getMainHandCritRate());
		// TODO find off hand attack and crit rate values
		setOffHandAttack(pst.getMainHandAttack());
		setOffHandCritRate(pst.getMainHandCritRate());
		setWater(0);
		setWind(0);
		setEarth(0);
		setFire(0);
		// TODO find good values for attack range
		setAttackRange(1500);
		setAttackSpeed(Math.round(pst.getAttackSpeed() * 1000));
		// TODO find good values for fly time
		setFlyTime(60);
		setInitialized(true);
		log.debug("loading base game stats for player " + owner.getName() + " (id " + owner.getObjectId() + "): "
			+ this);
	}

	public PlayerGameStats(Player player, PlayerStatsData psd, int power, int health, int agility, int accuracy,
		int knowledge, int will, int mainHandAttack, int mainHandCritRate, int otherHandAttack, int otherHandCritRate,
		int attackSpeed, int attackRange)
	{
		super(player, power, health, agility, accuracy, knowledge, will, mainHandAttack, mainHandCritRate,
			otherHandAttack, otherHandCritRate, attackSpeed, attackRange);
		this.playerStatsData = psd;
	}

	public PlayerGameStats getBaseGameStats()
	{
		int level = this.getOwner().getLevel();
		final PlayerGameStats pgs = new PlayerGameStats(playerStatsData, (Player) getOwner());
		pgs.doEvolution(1, level);
		log.debug("Loading base game stats for player " + getOwner().getName() + "(id " + getOwner().getObjectId()
			+ ") for level " + level + ": " + pgs);
		return pgs;
	}

	/**
	 * @param itemIdArray
	 *            the itemIdArray to set
	 */
	public void setItemIdArrayLenght(int lenght)
	{
		this.itemIdArray = new int[lenght];
		this.itemCountArray = new int[lenght];
		this.itemIdArrayLength = lenght;
	}

	public void setItemIdArray(int itemId, int arrayRow)
	{
		this.itemIdArray[arrayRow] = itemId;
	}

	public void setItemCountArray(int count, int arrayRow)
	{
		this.itemCountArray[arrayRow] = count;
	}

	public int getItemIdArray(int arrayRow)
	{
		return itemIdArray[arrayRow];
	}

	public int getItemCountArray(int arrayRow)
	{
		return itemCountArray[arrayRow];
	}

	public int getArrayLenght()
	{
		return itemIdArrayLength;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @param itemId
	 *            the itemId to set
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
	 * @param itemNameId
	 *            the itemNameId to set
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

	public void setPlayerStatsData(PlayerStatsData playerStatsData)
	{
		this.playerStatsData = playerStatsData;
	}

	// TODO Find the good stats evolution rates according to level
	public void doEvolution(int fromLevel, int toLevel)
	{
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
	}
}
