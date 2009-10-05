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
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_HP;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.StatsFunctions;
import com.aionemu.gameserver.world.World;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-
 *
 */
public class PlayerController extends CreatureController<Player>
{
	private static final Logger	log	= Logger.getLogger(PlayerController.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if(object instanceof Player)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO((Player) object, false));
		}
		else if(object instanceof Npc)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO((Npc) object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDie()
	{
		super.onDie();
		Player owner = getOwner();
		World world = owner.getActiveRegion().getWorld();
		PlayerCommonData pcd = getOwner().getCommonData();
		pcd.setExp((int)Math.round(pcd.getExpNeed()*0.03));
		PacketSendUtility.sendMessage(owner,"You died...");
		world.despawn(owner);
		world.setPosition(owner, owner.getWorldId(), owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
		owner.setProtectionActive(true);
		PacketSendUtility.sendPacket(owner, new SM_UNKF5(owner));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void attackTarget(int targetObjectId, int attackno, long time, int type)
	{
		Player player = getOwner();
		World world = player.getActiveRegion().getWorld();
		PlayerGameStats gameStats = player.getGameStats();
		attackno = gameStats.getAttackCounter();
		time = System.currentTimeMillis();
		int attackType = type; //TODO investigate attack types	
		
		Npc npc = (Npc) world.findAionObject(targetObjectId);
		//TODO fix last attack - cause mob is already dead
		int damage = StatsFunctions.calculateBaseDamageToTarget(player, npc);
		PacketSendUtility.broadcastPacket(player,
			new SM_ATTACK(player.getObjectId(), targetObjectId,
				gameStats.getAttackCounter(), (int) time, attackType, damage), true);
		boolean attackSuccess = npc.getController().onAttack(player);
		log.info("player {name:"+player.getName()+",lvl:"+player.getLevel()+"} attacks npc {name:"+npc.getName()+",lvl:"+npc.getLevel()+"}, attackSuccess:"+attackSuccess);
		if(attackSuccess)
		{
			gameStats.increaseAttackCounter();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public boolean onAttack(Creature creature)
	{
		super.onAttack(creature);
		Player player = getOwner();
		PlayerLifeStats lifeStats = player.getLifeStats();
		
		//TODO resolve synchronization issue
		if(!lifeStats.isAlive())
		{
			//TODO send action failed packet
			return false;
		}
		
		int newHp = lifeStats.reduceHp(StatsFunctions.calculateBaseDamageToTarget(creature, player));
		int hpPercentage = Math.round(100 *  newHp / lifeStats.getMaxHp());
		log.info("player {name:"+player.getName()+",level:"+player.getLevel()+"} attacked by {name:"+creature.getName()+",level:"+creature.getLevel()+"}, newHp:"+newHp+",hpPercent:"+hpPercentage);
		//PacketSendUtility.broadcastPacket(player, new SM_STATUPDATE_HP())
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(creature.getObjectId(), player.getObjectId(), 0, 0, 0, StatsFunctions.calculateBaseDamageToTarget(creature, player)));
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK_STATUS(player.getObjectId(), hpPercentage));
		PacketSendUtility.broadcastPacket(player, new SM_STATUPDATE_HP(newHp, lifeStats.getMaxHp()));
		if(newHp == 0)
		{
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(this.getOwner().getObjectId(), 13 , creature.getObjectId()));
			this.doDrop();
			this.doReward(creature);
			this.onDie();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		// TODO Auto-generated method stub
		super.doDrop();
	}
	
	@Override
	public void doReward(Creature creature) {
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
