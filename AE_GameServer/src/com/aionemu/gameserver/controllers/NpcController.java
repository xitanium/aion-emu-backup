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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.NpcGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.StatsFunctions;
import com.aionemu.gameserver.world.World;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-
 * 
 */
public class NpcController extends CreatureController<Npc>
{	
	private static final Logger	log	= Logger.getLogger(PlayerController.class);
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onDie()
	 */
	@Override
	public void onDie()
	{
		super.onDie();
		RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
		DecayService.getInstance().scheduleDecayTask(this.getOwner());
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onRespawn()
	 */
	@Override
	public void onRespawn()
	{
		super.onRespawn();
		this.getOwner().getLifeStats().reset();
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public boolean onAttack(Creature creature)
	{
		super.onAttack(creature);
		
		Npc npc = getOwner();
		NpcLifeStats lifeStats = npc.getLifeStats();
		
		//TODO resolve synchronization issue
		if(!lifeStats.isAlive())
		{
			//TODO send action failed packet
			return false;
		}
		
		int newHp = lifeStats.reduceHp(StatsFunctions.calculateBaseDamageToTarget(creature, npc));
		int hpPercentage = Math.round(100 *  newHp / lifeStats.getMaxHp());
		
		log.info("npc {name:"+npc.getName()+",level:"+npc.getLevel()+"} attacked by {name:"+creature.getName()+",level:"+creature.getLevel()+"}, newHp:"+newHp);
		PacketSendUtility.broadcastPacket(npc, new SM_ATTACK_STATUS(npc.getObjectId(), hpPercentage));
		if(newHp == 0)
		{
			PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(this.getOwner().getObjectId(), 13 , creature.getObjectId()));
			this.doDrop();
			this.doReward(creature);
			this.onDie();
		} else {
			this.attackTarget(creature.getObjectId(), npc.getGameStats().getAttackCounter(), System.currentTimeMillis(), 2);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	public boolean attackTarget (int targetObjectId, int attackno, long time, int type) {
		Npc npc = getOwner();
		World world = npc.getActiveRegion().getWorld();
		Player player = world.findPlayer(targetObjectId);
		NpcGameStats gameStats = npc.getGameStats();
		attackno = gameStats.getAttackCounter();
		time = System.currentTimeMillis();
		int attackType = type; //TODO investigate attack types	
		
		//TODO fix last attack - cause mob is already dead
		int damage = StatsFunctions.calculateBaseDamageToTarget(npc, player);
		PacketSendUtility.broadcastPacket(player,
			new SM_ATTACK(npc.getObjectId(), targetObjectId,
				gameStats.getAttackCounter(), (int) time, attackType, damage), true);
		boolean attackSuccess = player.getController().onAttack(npc);
		log.info("npc {name:"+npc.getName()+",lvl:"+npc.getLevel()+"} attacks player {name:"+player.getName()+",lvl:"+player.getLevel()+"}, attackSuccess:"+attackSuccess);
		if(attackSuccess)
		{
			gameStats.increaseAttackCounter();
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		super.doDrop();
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));
	}
			 
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doReward()
	 */
	@Override
	public void doReward(Creature creature)
	{
		super.doReward(creature);
		
		if(creature instanceof Player)
		{
			Player player = (Player) creature;
			//TODO may be introduce increaseExpBy method in PlayerCommonData
			long currentExp = player.getCommonData().getExp();
			
			long xpReward = StatsFunctions.calculateSoloExperienceReward(player, getOwner());
			player.getCommonData().setExp(currentExp + xpReward);
		}
	}
}
