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

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.events.AttackEvent;
import com.aionemu.gameserver.ai.npcai.NpcAi;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.NpcGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 */
public class NpcController extends CreatureController<Npc>
{
	public NpcController (World world) {
		super(world);
	}

	private static Logger log = Logger.getLogger(NpcController.class);
	
	private Future<?> decayTask;

	public void attackTarget(int targetObjectId)
	{
		Npc npc = getOwner();
		NpcGameStats npcGameStats = npc.getGameStats();
		NpcLifeStats npcLS = npc.getLifeStats();
		long time = System.currentTimeMillis();
		int attackType = 1; //TODO investigate attack types	

		//TODO refactor to possibility npc-npc fight
		Player player = (Player) world.findAionObject(targetObjectId);

		//TODO fix last attack - cause mob is already dead
		if(npcLS.getCurrentHp() > 0) {
			int damages = StatFunctions.calculateBaseDamageToTarget(npc, player);
			
			PacketSendUtility.broadcastPacket(player,
				new SM_EMOTION(npc.getObjectId(), 19, player.getObjectId()), true);
	
			PacketSendUtility.broadcastPacket(player,
				new SM_ATTACK(npc.getObjectId(), player.getObjectId(),
					npcGameStats.getAttackCounter(), (int) time, attackType, damages), true);
			boolean attackSuccess = player.getController().onAttack(npc,damages);
			
			if(attackSuccess)
			{
				npcGameStats.increaseAttackCounter();
			}
			if(player.getLifeStats().isAlreadyDead())
			{
				player.getController().onDie();
				getOwner().getNpcAi().stopTask();
			}
		}
		else
		{
			this.onDie();
		}
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onDie()
	 */
	@Override
	public void onDie()
	{	
		NpcAi npcAi = this.getOwner().getNpcAi();
		npcAi.setAiState(AIState.DEAD);
		npcAi.stopTask();
		
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13 , getOwner().getObjectId()));
		this.doDrop();

		if(decayTask == null)
		{
			RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
			decayTask = DecayService.getInstance().scheduleDecayTask(this.getOwner());
		}	
		
		//deselect target at the end
		getOwner().setTarget(null);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onRespawn()
	 */
	@Override
	public void onRespawn()
	{
		this.decayTask = null;
		
		this.getOwner().getNpcAi().setAiState(AIState.IDLE);
		NpcStatsTemplate statsTemplate = getOwner().getTemplate().getStatsTemplate();
		this.getOwner().setLifeStats(new NpcLifeStats(getOwner(),statsTemplate));
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public boolean onAttack(Creature attacker, int damages)
	{
		Npc victim = getOwner();
		NpcLifeStats lifeStats = victim.getLifeStats();
		
		if(lifeStats.isAlreadyDead())
		{
			return false;
		}
		
		//TODO: Reduce hp corresponding to real damages done by player
		lifeStats.reduceHp(damages);
		
		if (lifeStats.isAlreadyDead()) {
			this.onDie();
			this.doReward(attacker);
		} else {
			NpcAi npcAi = victim.getNpcAi();
			npcAi.handleEvent(new AttackEvent(attacker));
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doReward()
	 */
	@Override
	public void doReward(Creature creature)
	{
		if(creature instanceof Player)
		{
			Player player = (Player) creature;
			//TODO may be introduce increaseExpBy method in PlayerCommonData
			long currentExp = player.getCommonData().getExp();

			long xpReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());
			player.getCommonData().setExp(currentExp + xpReward);
			
			PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.EXP(Long.toString(xpReward)));
		}
	}
}
