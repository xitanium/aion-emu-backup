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
package skillhandlers;

import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.model.SkillElement;
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

import org.apache.log4j.Logger;

/**
 * @author xavier
 */
public class SpellSkillHandler extends SkillHandler
{
    private static final Logger log = Logger.getLogger(SpellSkillHandler.class);
    
    public SpellSkillHandler(int spellId) {
        super(spellId);
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.skillengine.SkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature, java.util.List)
     */
    @Override
    public void useSkill(final Creature creature, List<Creature> targets)
    {
    	final int attackerId = creature.getObjectId();
    	SkillTemplate st = this.getSkillTemplate();
    	CreatureLifeStats<?> cls = creature.getLifeStats();
    	final SkillElement element = st.getElement();
    	final int reload = st.getLaunchTime();
    	final int cost = st.getCost();
    	final int spellId = getSkillId();
    	final int level = st.getLevel();
    	// TODO substract MagicalDefense to damages 
    	final int damages = st.getDamages();
    	final int unk = 0;
    	if (cost>cls.getCurrentMp()) {
    		log.info("You cannot use "+st.getName()+" because it needs "+cost+"MP and you have only "+cls.getCurrentMp()+"");
    		return;
    	}
        log.info("You are using "+st.getName());
        Iterator<Creature> iter = targets.iterator();
        while (iter.hasNext()) {
        	final Creature cur = iter.next();
        	final int targetId = cur.getObjectId();
        	if (creature instanceof Player) {
        		PacketSendUtility.sendPacket((Player)creature, new SM_CASTSPELL(attackerId,getSkillId(),st.getLevel(),0,st.getRechargeTime(),targetId));
        	}
        	cur.getController().onAttack(creature);
        	ThreadPoolManager.getInstance().schedule(new Runnable()
        	{
        		public void run() 
        		{
        			if (creature instanceof Player) {
        				PacketSendUtility.sendPacket((Player)creature,new SM_CASTSPELL_END(attackerId, spellId, level, unk, damages, targetId));
        			}
        			performAction(creature,cur,damages,cost);
        		}
        	}, (reload-1)*1000);
        }
    }
    
    private void performAction(final Creature attacker, final Creature target, final int damages, final int cost) {
    	CreatureLifeStats<?> tls = target.getLifeStats();
    	CreatureLifeStats<?> als = attacker.getLifeStats();
    	tls.reduceHp(damages);
    	als.reduceMp(cost);
    }
}
