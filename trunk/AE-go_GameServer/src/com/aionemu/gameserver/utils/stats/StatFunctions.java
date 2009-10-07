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
package com.aionemu.gameserver.utils.stats;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * @author ATracer
 *
 */
public class StatFunctions
{
	private static Logger log = Logger.getLogger(StatFunctions.class);

	/**
	 * 
	 * @param player
	 * @param target
	 * @return XP reward from target
	 */
	public static long calculateSoloExperienceReward(Player player, Creature target)
	{
		
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		
		//TODO take baseXP from target object (additional attribute in stats template is needed)
		int baseXP = targetLevel * 80;
		
		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);

		return (int) Math.floor(baseXP * xpPercentage / 100);
	}
	
	/**
	 * 
	 * @param player
	 * @param target
	 * @return Damage made to target (-hp value)
	 */
	public static int calculateBaseDamageToTarget(Player player, Creature target)
	{
		int pAttack = ClassStats.getPowerFor(player.getPlayerClass());
		int targetPDef = ((Npc) target).getTemplate().getStatsTemplate().getMaxHp();
		
		return pAttack - targetPDef / 10;
	}
	
	public static int calculateNpcBaseDamageToPlayer(Npc npc, Player player)
	{
		//TODO this is a dummy calcs
		return npc.getLevel() * 10;
	}
	
	public static PlayerLifeStats getBaseLifeStats (PlayerClass playerClass) {
		final PlayerLifeStats pls = new PlayerLifeStats();
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(playerClass,1);
		pls.setMaxHp(pst.getMaxHp());
		pls.setMaxMp(pst.getMaxMp());
		// TODO find good MaxDp value
		pls.setMaxDp(100);
		return pls;
	}
	
	public static PlayerGameStats getBaseGameStats (PlayerClass playerClass) {
		final PlayerGameStats pgs = new PlayerGameStats();
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(playerClass,1);
		pgs.setAttackCounter(0);
		pgs.setPower(pst.getPower());
		pgs.setHealth(pst.getHealth());
		pgs.setAgility(pst.getAgility());
		pgs.setAccuracy(pst.getAccuracy());
		pgs.setKnowledge(pst.getKnowledge());
		pgs.setWill(pst.getWill());
		pgs.setMainHandAttack(pst.getMainHandAttack());
		pgs.setMainHandCritRate(pst.getMainHandCritRate());
		// TODO find off hand attack and crit rate values
		pgs.setOffHandAttack(pst.getMainHandAttack());
		pgs.setOffHandCritRate(pst.getMainHandCritRate());
		pgs.setWater(0);
		pgs.setWind(0);
		pgs.setEarth(0);
		pgs.setFire(0);
		// TODO find good values for fly time
		pgs.setFlyTime(60);
		return pgs;
	}
}
