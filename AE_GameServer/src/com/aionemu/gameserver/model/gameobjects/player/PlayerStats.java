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
package com.aionemu.gameserver.model.gameobjects.player;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author xavier
 * 
 */
public class PlayerStats
{
	private static Logger				log				= Logger.getLogger(AionServerPacket.class);
	// static.
	private int					power;
	private int					health;
	private int					agility;
	private int					accuracy;
	private int					knowledge;
	private int					will;
	private int					mainHandAttack;
	private int					mainHandCritRate;
	private int					water		= 0;
	private int					wind		= 0;
	private int					earth		= 0;
	private int					fire		= 0;
	private int					flyTime		= 60;
	// needs calculations.
	private long				maxhpc;
	private int					maxhp;
	private int					mainHandAccuracy;
	private int					magicAccuracy;
	private long				evasionc;
	private long				blockc;
	private long				parryc;
	private int					evasion;
	private int					block;
	private int					parry;
	// unknown yet
	private int					maxdp		= 100;
	private int					maxmp		= 100;
	private int					magicBoost	= 0;
	private int					pdef		= 0;
	private int					mres		= 0;
	private Player				player;
	private PlayerStatsTemplate	playerStatsTemplate;

	public PlayerStats(Player player)
	{
		this.setPlayer(player);
	}

	/**
	 * @return the power
	 */
	public int getPower()
	{
		return power;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(int power)
	{
		this.power = power;
	}

	/**
	 * @return the health
	 */
	public int getHealth()
	{
		return health;
	}

	/**
	 * @param health
	 *            the health to set
	 */
	public void setHealth(int health)
	{
		this.health = health;
	}

	/**
	 * @return the agility
	 */
	public int getAgility()
	{
		return agility;
	}

	/**
	 * @param agility
	 *            the agility to set
	 */
	public void setAgility(int agility)
	{
		this.agility = agility;
	}

	/**
	 * @return the accuracy
	 */
	public int getAccuracy()
	{
		return accuracy;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(int accuracy)
	{
		this.accuracy = accuracy;
	}

	/**
	 * @return the knowledge
	 */
	public int getKnowledge()
	{
		return knowledge;
	}

	/**
	 * @param knowledge
	 *            the knowledge to set
	 */
	public void setKnowledge(int knowledge)
	{
		this.knowledge = knowledge;
	}

	/**
	 * @return the will
	 */
	public int getWill()
	{
		return will;
	}

	/**
	 * @param will
	 *            the will to set
	 */
	public void setWill(int will)
	{
		this.will = will;
	}

	/**
	 * @return the mainHandAttack
	 */
	public int getMainHandAttack()
	{
		return mainHandAttack;
	}

	/**
	 * @param mainHandAttack
	 *            the mainHandAttack to set
	 */
	public void setMainHandAttack(int mainHandAttack)
	{
		this.mainHandAttack = mainHandAttack;
	}

	/**
	 * @return the mainHandCritRate
	 */
	public int getMainHandCritRate()
	{
		return mainHandCritRate;
	}

	/**
	 * @param mainHandCritRate
	 *            the mainHandCritRate to set
	 */
	public void setMainHandCritRate(int mainHandCritRate)
	{
		this.mainHandCritRate = mainHandCritRate;
	}

	/**
	 * @return the water
	 */
	public int getWater()
	{
		return water;
	}

	/**
	 * @param water
	 *            the water to set
	 */
	public void setWater(int water)
	{
		this.water = water;
	}

	/**
	 * @return the wind
	 */
	public int getWind()
	{
		return wind;
	}

	/**
	 * @param wind
	 *            the wind to set
	 */
	public void setWind(int wind)
	{
		this.wind = wind;
	}

	/**
	 * @return the earth
	 */
	public int getEarth()
	{
		return earth;
	}

	/**
	 * @param earth
	 *            the earth to set
	 */
	public void setEarth(int earth)
	{
		this.earth = earth;
	}

	/**
	 * @return the fire
	 */
	public int getFire()
	{
		return fire;
	}

	/**
	 * @param fire
	 *            the fire to set
	 */
	public void setFire(int fire)
	{
		this.fire = fire;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
		this.playerStatsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		this.recomputeStats();
	}

	/**
	 * @param flyTime
	 *            the flyTime to set
	 */
	public void setFlyTime(int flyTime)
	{
		this.flyTime = flyTime;
	}

	/**
	 * @return the flyTime
	 */
	public int getFlyTime()
	{
		return flyTime;
	}

	/**
	 * @return the maxhpc
	 */
	public long getMaxhpc()
	{
		return maxhpc;
	}

	/**
	 * @return the maxhp
	 */
	public int getMaxHP()
	{
		return maxhp;
	}

	/**
	 * @return the mainHandAccuracy
	 */
	public int getMainHandAccuracy()
	{
		return mainHandAccuracy;
	}

	/**
	 * @return the magicAccuracy
	 */
	public int getMagicAccuracy()
	{
		return magicAccuracy;
	}

	/**
	 * @return the evasionc
	 */
	public long getEvasionc()
	{
		return evasionc;
	}

	/**
	 * @return the blockc
	 */
	public long getBlockc()
	{
		return blockc;
	}

	/**
	 * @return the parryc
	 */
	public long getParryc()
	{
		return parryc;
	}

	/**
	 * @return the evasion
	 */
	public int getEvasion()
	{
		return evasion;
	}

	/**
	 * @return the block
	 */
	public int getBlock()
	{
		return block;
	}

	/**
	 * @return the parry
	 */
	public int getParry()
	{
		return parry;
	}

	/**
	 * @return the maxdp
	 */
	public int getMaxDP()
	{
		return maxdp;
	}

	/**
	 * @return the maxmp
	 */
	public int getMaxMP()
	{
		return maxmp;
	}

	/**
	 * @return the magicBoost
	 */
	public int getMagicBoost()
	{
		return magicBoost;
	}

	/**
	 * @return the pdef
	 */
	public int getPdef()
	{
		return pdef;
	}

	/**
	 * @return the mres
	 */
	public int getMres()
	{
		return mres;
	}

	public void recomputeStats()
	{
		maxhpc = Math.round(1.1688 * (player.getLevel() - 1) * (player.getLevel() - 1) + 45.149
			* (player.getLevel() - 1) + 284);
		Long lObj = new Long(maxhpc);
		maxhp = lObj.intValue();
		if (this.playerStatsTemplate!=null) {
			power = playerStatsTemplate.getPower() + (int) Math.round((player.getLevel() - 1) * 1.1688);
			health = playerStatsTemplate.getHealth() + (int) Math.round((player.getLevel() - 1) * 1.1688);
			agility = playerStatsTemplate.getAgility() + (int) Math.round((player.getLevel() - 1) * 1.1688);// change
			accuracy = playerStatsTemplate.getAccuracy() + (int) Math.round((player.getLevel() - 1) * 1.1688); // change
			knowledge = playerStatsTemplate.getKnowledge() + (int) Math.round((player.getLevel() - 1) * 1.1688); // change
			will = playerStatsTemplate.getWill() + (int) Math.round((player.getLevel() - 1) * 1.1688); // change
			mainHandAttack = playerStatsTemplate.getMainHandAttack() + (int) Math.round((player.getLevel() - 1) * 0.108); // change
			mainHandCritRate = playerStatsTemplate.getMainHandCritRate() + (int) Math.round((player.getLevel() - 1) * 0.108); // change
		} else {
			log.info("playerStatsTemplate null for player "+player.getName()+":{class:"+player.getClass()+",level:"+player.getLevel());
			power = health = agility = accuracy = knowledge = will = 100+(int) Math.round((player.getLevel() - 1) * 1.1688);
			mainHandAttack = 18+(int) Math.round((player.getLevel() - 1) * 0.108);
			mainHandCritRate = 6+(int) Math.round((player.getLevel() - 1) * 0.108);
		}
		evasionc = Math.round(agility * 3.1 - 248.5 + 12.4 * player.getLevel());
		Long lObj2 = new Long(evasionc);
		evasion = lObj2.intValue();
		parryc = Math.round(agility * 3.1 - 248.5 + 12.4 * player.getLevel());
		Long lObj3 = new Long(parryc);
		parry = lObj3.intValue();
		blockc = Math.round(agility * 3.1 - 248.5 + 12.4 * player.getLevel());
		Long lObj4 = new Long(blockc);
		block = lObj4.intValue();
		mainHandAccuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
		magicAccuracy = (will * 2) - 10 + 8 * player.getLevel();
	}

}
