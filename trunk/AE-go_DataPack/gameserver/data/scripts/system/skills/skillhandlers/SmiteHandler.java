package skillhandlers;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class SmiteHandler extends SkillHandler {
	
private static final Logger log = Logger.getLogger(FireBold.class);
    
    public SmiteHandler() {
        super(975);
    }
    
    public void useSkill(Creature creature, List<Creature> targets)
    {
    	SkillTemplate st = this.getSkillTemplate();
        log.info("You are using Smite");
        if (creature instanceof Player) {
        	final Player player = (Player)creature;
        	final int attackerId = player.getObjectId();
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
        		creature.getController().onAttack(player, st);
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
    }
	
}