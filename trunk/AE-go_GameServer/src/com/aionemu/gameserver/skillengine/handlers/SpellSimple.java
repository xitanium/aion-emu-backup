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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.skillengine.SkillHandler;

/**
 * @author xavier
 *
 */
public class SpellSimple extends SkillHandler
{

	/**
	 * @param skillId
	 */
	public SpellSimple(int skillId)
	{
		super(skillId);
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.SkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature, java.util.List)
	 */
	@Override
	public void useSkill(final Creature speller, List<Creature> targets)
	{
		final int spellerId = speller.getObjectId();
    	SkillTemplate st = this.getSkillTemplate();
    	CreatureLifeStats<?> cls = speller.getLifeStats();
    	final int reload = st.getLaunchTime();
    	final int cost = st.getCost();
    	final int spellId = getSkillId();
    	final int level = st.getLevel();
    	final int unk = 0;
    	if (cost>cls.getCurrentMp()) {
    		log.info("You cannot use "+st.getName()+" because it needs "+cost+"MP and you have only "+cls.getCurrentMp()+"MP");
    		return;
    	}
        log.info("You are using "+st.getName());
        Iterator<Creature> iter = targets.iterator();
        while (iter.hasNext()) {
        	final Creature target = iter.next();
        	final int targetId = target.getObjectId();
        	final int damages = StatFunctions.calculateMagicDamageToTarget(speller, target, st);
        	PacketSendUtility.broadcastPacket(speller, new SM_CASTSPELL(spellerId, spellId, level, unk, targetId, reload, st.getRechargeTime()));
        	if (speller instanceof Player) {
        		PacketSendUtility.sendPacket((Player)speller, new SM_CASTSPELL(spellerId,getSkillId(),st.getLevel(),0,targetId,reload,st.getRechargeTime()));
        	}
        	target.getController().onAttack(speller,damages);
        	ThreadPoolManager.getInstance().schedule(new Runnable()
        	{
        		public void run()
        		{
        			PacketSendUtility.broadcastPacket(speller, new SM_CASTSPELL_END(spellerId, spellId, level, unk, targetId, damages));
        			if (speller instanceof Player) {
        				PacketSendUtility.sendPacket((Player)speller,new SM_CASTSPELL_END(spellerId, spellId, level, unk,targetId, damages));
        			}
        			performAction(speller,target,damages,cost);
        		}
        	}, (reload-1)*1000);
        }
    }
    
    private void performAction(final Creature speller, final Creature target, final int damages, final int cost) {
    	CreatureLifeStats<?> tls = speller.getLifeStats();
    	CreatureLifeStats<?> als = target.getLifeStats();
    	als.reduceHp(damages);
    	tls.reduceMp(cost);
    }
}
