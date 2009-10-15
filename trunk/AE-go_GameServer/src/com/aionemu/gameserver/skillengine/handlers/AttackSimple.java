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
package com.aionemu.gameserver.skillengine.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author xavier
 *
 */
public class AttackSimple extends SkillHandler
{
	/**
	 * @param skillId
	 */
	public AttackSimple(int skillId)
	{
		super(skillId);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.SkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature, java.util.List)
	 */
	@Override
	public void useSkill(final Creature creature, List<Creature> targets)
	{
		// TODO Auto-generated method stub
		final int spellerId = creature.getObjectId();
		SkillTemplate st = this.getSkillTemplate();
		CreatureLifeStats<?> cls = creature.getLifeStats();
    	final int reload = st.getLaunchTime();
    	final int spellId = getSkillId();
    	final int level = st.getLevel();
    	// If spell cost > current play MP
    	final int unk = 0;
    	log.info("You are using "+st.getName());
    	if(targets != null)
    	{
	        Iterator<Creature> iter = targets.iterator();
	        while (iter.hasNext()) {
	        	final Creature target = iter.next();
	        	final int targetId = target.getObjectId();
	        	final int damages = st.getDamages();
	        	PacketSendUtility.broadcastPacket(creature, new SM_CASTSPELL(targetId, spellId, level, unk, targetId, st.getRechargeTime()));
	        	if (creature instanceof Player) {
	        		PacketSendUtility.sendPacket((Player)creature, new SM_CASTSPELL(spellerId,getSkillId(),st.getLevel(),unk,targetId,st.getRechargeTime()));
	        	}
	        	target.getController().onAttack(creature,damages);
	        	ThreadPoolManager.getInstance().schedule(new Runnable()
	        	{
	        		public void run()
	        		{
	        			PacketSendUtility.broadcastPacket(creature, new SM_CASTSPELL_END(spellerId, spellId, level, unk, targetId, damages));
	        			if (creature instanceof Player) {
	        				PacketSendUtility.sendPacket((Player)creature,new SM_CASTSPELL_END(spellerId, spellId, level, unk,targetId, damages));
	        			}
	        			performAction(creature,target,damages);
	        		}
	        	}, (reload-1)*1000);
	        }
    	}
	}
	
	private void performAction(final Creature speller, final Creature target, final int damages) {
    	CreatureLifeStats<?> als = target.getLifeStats();
    	als.reduceHp(damages);
    }
}
