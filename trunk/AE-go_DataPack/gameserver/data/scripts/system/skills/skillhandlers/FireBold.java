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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.skillengine.SkillHandler;

import org.apache.log4j.Logger;

/**
 * @author ATracer
 */
public class FireBold extends SkillHandler
{
    private static final Logger log = Logger.getLogger(FireBold.class);
    
    public FireBold() {
        super(1351);
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.skillengine.SkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature, java.util.List)
     */
    @Override
    public void useSkill(Creature creature, List<Creature> targets)
    {
    	final int attackerId = creature.getObjectId();
    	SkillTemplate st = this.getSkillTemplate();
        log.info("You are using fire bold");
        if (creature instanceof Player) {
        	final Player player = (Player)creature;
        	Iterator<Creature> iter = targets.iterator();
        	while (iter.hasNext()) {
        		final Creature cur = iter.next();
        		final int spellId = getSkillId();
        		final int level = st.getLevel();
        		final int unk = 0;
        		final int targetId = cur.getObjectId();
        		final int damages = st.getDamages();
        		final int reload = st.getLaunchTime();
        		final int cost = st.getCost();
        		PacketSendUtility.sendPacket(player, new SM_CASTSPELL(attackerId,getSkillId(),st.getLevel(),0,st.getRechargeTime(),targetId));
        		ThreadPoolManager.getInstance().schedule(new Runnable()
        		{
        			public void run() 
        			{
        				PacketSendUtility.sendPacket(player,
                				new SM_CASTSPELL_END(attackerId, spellId, level, unk, damages, targetId));
        				performAction(player,cur,damages,cost);
        			}
        		}, (reload-1)*1000);
        	}
        }
    }
    
    private void performAction(final Player player, final Creature creature, final int damages, final int cost) {
    	CreatureLifeStats<?> cls = creature.getLifeStats();
    	PlayerLifeStats pls = player.getLifeStats();
    	cls.reduceHp(damages);
    	pls.reduceMp(cost);
    	creature.getController().onAttack(player);
    }
}