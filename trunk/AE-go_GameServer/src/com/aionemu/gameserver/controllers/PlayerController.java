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

import java.util.Vector;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK72;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 * 
 */
public class PlayerController extends CreatureController<Player>
{
	public PlayerController(World world)
	{
		super(world);
	}

	// TEMP till player AI introduced
	private Creature	lastAttacker;

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
	 * 
	 * Shoul only be triggered from one place (life stats)
	 */
	@Override
	public void onDie()
	{
		// TODO probably introduce variable - last attack creature in player AI
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13,
			lastAttacker.getObjectId()), true);
		Player player = this.getOwner();
		PacketSendUtility.sendPacket(player, new SM_DIE());
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
	}

	public void attackTarget(int targetObjectId)
	{
		Player attacker = getOwner();
		PlayerGameStats ags = attacker.getGameStats();
		int time = Math.round(System.currentTimeMillis() / 1000f);
		int attackType = 0; // TODO investigate attack types

		Creature target = (Creature) world.findAionObject(targetObjectId);
		int damages = StatFunctions.calculateBaseDamageToTarget(attacker, target);
		PacketSendUtility.broadcastPacket(attacker, new SM_ATTACK(attacker.getObjectId(), targetObjectId, ags
			.getAttackCounter(), time, attackType, damages), true);

		boolean attackSuccess = target.getController().onAttack(attacker, damages);
		if(attackSuccess)
		{
			ags.increaseAttackCounter();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public boolean onAttack(Creature attacker, int damages)
	{
		lastAttacker = attacker;

		Player victim = getOwner();
		PlayerLifeStats vls = victim.getLifeStats();

		// TODO resolve synchronization issue
		if(vls.isAlreadyDead())
		{
			return false;
		}

		vls.reduceHp(damages);

		if(vls.isAlreadyDead())
		{
			if(attacker instanceof Npc)
			{ // PvE
				PacketSendUtility.broadcastPacket(victim, new SM_EMOTION(victim.getObjectId(), 13, attacker
					.getObjectId()), true);
				this.onDie();
			}
			else
			{ // PvP
				this.duelEndWith((Player) attacker);
			}
		}
		return true;
	}

	public void useSkill(int skillId)
	{
		SkillHandler skillHandler = SkillEngine.getSkillHandlerFor(skillId);

		if(skillHandler != null)
		{
			if(this.getOwner().getTarget() != null)
			{
				Vector<Creature> list = new Vector<Creature>();
				list.add(this.getOwner().getTarget());
				skillHandler.useSkill(this.getOwner(), list);
			}
			else
			{
				skillHandler.useSkill(this.getOwner(), null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		// TODO Auto-generated method stub
	}

	public void startDuelWith(Player player)
	{
		// PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.DUEL_STARTED_WITH(player.getName()));
		log.debug("[PvP] Player " + this.getOwner().getName() + " start duel with " + player.getName());
	}

	public void onDuelWith(Player provocker)
	{
		log.debug("[PvP] Player " + provocker.getName() + " provocked in duel " + this.getOwner().getName());
	}

	public void duelEndWith(Player attacker)
	{
		// TODO Fin du duel
		log.debug("[PvP] Player " + attacker.getName() + " won versus " + this.getOwner().getName());
	}

	/**
	 * Teleport player to new location
	 * 
	 * @param worldId
	 * @param x
	 * @param y
	 * @param z
	 */
	public void teleportTo(int worldId, float x, float y, float z, byte heading)
	{
		Player p = getOwner();
		world.despawn(p);
		world.setPosition(p, worldId, x, y, z, heading);
		p.setProtectionActive(true);
		PacketSendUtility.sendPacket(p, new SM_UNKF5(p));
	}

	/**
	 * Do player revival
	 */
	public void onRevive()
	{
		Player p = this.getOwner();
		PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.REVIVE);
		PacketSendUtility.sendPacket(p, new SM_UNK72());
		PacketSendUtility.sendPacket(p, new SM_STATS_INFO(p));
		PacketSendUtility.sendPacket(p, new SM_PLAYER_INFO(p, true));

		this.teleportTo(p.getWorldId(), p.getX(), p.getY(), p.getZ(), p.getHeading());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.controllers.CreatureController#doReward(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public void doReward(Creature creature)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onRespawn()
	 */
	@Override
	public void onRespawn()
	{
		// TODO Auto-generated method stub

	}
}
