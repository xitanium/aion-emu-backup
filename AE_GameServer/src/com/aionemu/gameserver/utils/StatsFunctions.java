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
package com.aionemu.gameserver.utils;

import java.util.Random;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.Rates;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.GameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * @author xavier
 *
 */
public class StatsFunctions
{
	private static final Logger	log	= Logger.getLogger(StatsFunctions.class);
	public static int calculateBaseDamageToTarget (Creature attacker, Creature target) {
		int damages = 0;
		Random generator = new Random();
		GameStats pgs = attacker.getGameStats();
		GameStats ngs = target.getGameStats();
		double rate = 1.0;
		int levelDiff = (attacker.getLevel()-target.getLevel());
		if (levelDiff>2) {
			switch (levelDiff) {
				case 3: rate = 0.90;
				case 4: rate = 0.80;
				case 5: rate = 0.70;
				case 6: rate = 0.60;
				case 7: rate = 0.50;
				case 8: rate = 0.40;
				case 9: rate = 0.30;
				case 10: rate = 0.20;
				default: rate = 0.10;
			}
		}
		damages = (int)Math.round((pgs.getPower()-ngs.getBlock()/10)*rate)+generator.nextInt(10);
		return damages; 
	}
	
	public static int calculateSoloExperienceReward (Player winner, Creature looser) {
		Random generator = new Random();
		int lvlDiff = looser.getLevel()-winner.getLevel();
		double rate = 0.00;
		if (lvlDiff<=-10) {
			rate = 0.01;
		} else {
			if (lvlDiff>=4) {
				rate = 1.20;
			} else {
				switch (lvlDiff) {
					case -9: rate = 0.10; break;
					case -8: rate = 0.20; break;
					case -7: rate = 0.30; break;
					case -6: rate = 0.40; break;
					case -5: rate = 0.50; break;
					case -4: if (generator.nextInt(2)==1) { rate = 0.60; } else { rate = 0.70; }; break;
					case -3: rate = 0.90; break;
					case -2:
					case -1:
					case 0: rate = 1.00; break;
					case 1: rate = 1.05; break;
					case 2: rate = 1.10; break;
					case 3: rate = 1.15; break;
				}
			}
		}
		return (int)Math.round(winner.getCommonData().getExpNeed()*0.07*rate*Rates.XP_RATE);
	}
	
	public static void doStatsEvolution (Creature creature, int oldLevel) {
		GameStats gs = creature.getGameStats();
		int newLevel = creature.getLevel();
		gs.setPower(gs.getPower() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setHealth(gs.getHealth() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setAgility(gs.getAgility() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setAccuracy(gs.getAccuracy() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setKnowledge(gs.getKnowledge() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setWill(gs.getWill() + (int) Math.round((newLevel - oldLevel) * 1.1688));
		gs.setMainHandAttack(gs.getMainHandAttack() + (int) Math.round((creature.getLevel() - 1) * 0.108));
		gs.setMainHandCritRate(gs.getMainHandCritRate() + (int) Math.round((creature.getLevel() - 1) * 0.108));
		gs.setOtherHandAttack(gs.getOtherHandAttack() + (int) Math.round((creature.getLevel() - 1) * 0.108));
		gs.setOtherHandCritRate(gs.getOtherHandCritRate() + (int) Math.round((creature.getLevel() - 1) * 0.108));
	}
	
	public static void computeStats (Npc npc) {
		NpcStatsTemplate nst = npc.getTemplate().getStatsTemplate();
		int maxHp = nst.getMaxHp();
		int maxMp = nst.getMaxMp();
		int power = 30 + (int) Math.round((npc.getLevel() - 1) * 1.1688);
		int health = 30 + (int) Math.round((npc.getLevel() - 1) * 1.1688);
		int agility = (int)Math.round((nst.getBlock() + 248.5 - 12.4 * npc.getLevel())/3.1);
		int accuracy = (int)Math.round((nst.getMainHandAccuracy() + 10 - 8 * npc.getLevel())/2);
		int knowledge = (int)Math.round((nst.getMagicAccuracy() + 10 - 8 * npc.getLevel())/2);
		int will = (int)Math.round((nst.getMagicAccuracy() + 10 - 8 * npc.getLevel())/2);
		int mha = nst.getMainHandAttack();
		int mhcr = nst.getMainHandCritRate();
		int oha = mha;
		int ohcr = mhcr;
		npc.setGameStats(new NpcGameStats(npc, power, health, agility, accuracy, knowledge, will, mha, mhcr, oha, ohcr));
		npc.setLifeStats(new NpcLifeStats(npc, maxHp, maxMp, maxHp, maxMp));
	}
	
	public static void computeStats (Player player) {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		int maxHp = pst.getMaxHp();
		int initialHp = pst.getMaxHp();
		int maxMp = pst.getMaxMp();
		int initialMp = pst.getMaxMp();
		int maxDp = 100;
		int initialDp = 0;
		int power = pst.getPower();
		int health = pst.getHealth();
		int agility = pst.getAgility();
		int accuracy = pst.getAccuracy();
		int knowledge = pst.getKnowledge();
		int will = pst.getWill();
		int mha = pst.getMainHandAttack();
		int mhcr = pst.getMainHandCritRate();
		int oha = mha;
		int ohcr = mhcr;
		log.info("player stats: {mhp:"+maxHp+",mmp:"+maxMp+",power:"+power+",health:"+health+",agility:"+agility+",accuracy:"+accuracy+",knowledge:"+knowledge+",will:"+will+",mha:"+mha+",mhcr:"+mhcr+"}");
		player.setGameStats(new PlayerGameStats(player, power, health, agility, accuracy, knowledge, will, mha, mhcr, oha, ohcr));
		player.setLifeStats(new PlayerLifeStats(player, initialHp, initialMp, initialDp, maxHp,  maxMp, maxDp));
	}
}
