/**
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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Random;
import java.util.UUID;

import com.aionemu.gameserver.configs.Rates;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.sun.xml.internal.fastinfoset.algorithm.HexadecimalEncodingAlgorithm;
/**
 * 
 * @author alexa026, Avol, ATracer
 * 
 */
public class CM_ATTACK extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					attackno;
	private int					time;
	private int					type;
	private int					at;

	public CM_ATTACK(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		attackno = readC();// empty
		time = readH();// empty
		type = readC();// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		Creature target = (Creature)player.getActiveRegion().getWorld().findAionObject(targetObjectId);
		PacketSendUtility.broadcastPacket((Player)player, new SM_ATTACK(player.getActiveRegion().getWorld(),player.getObjectId(),targetObjectId,attackno,time,type), true);
		log.info("player {name:"+player.getName()+",level:"+player.getLevel()+"} launch attack {id:"+targetObjectId+",no:"+attackno+",time:"+time+",type:"+type+"}");
		at = player.getatcount();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,30,player.getObjectId()), true);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,19,player.getObjectId()), true);
		sendPacket(new SM_ATTACK(player.getActiveRegion().getWorld(),targetObjectId,player.getObjectId(),at,time,type));
		sendPacket(new SM_ATTACK_STATUS(player,99));
	  	at = at + 1;
	    player.setatcount(at);
		
		sendPacket(new SM_ATTACK_STATUS(target,attackno));
		Creature looser = null;
		Creature winner = null;
		if (player.getHP()<=0) {
			looser = (Creature)player;
			winner = target;
		} else {
			if (target.getHP()<=0) {
				looser = target;
				winner = (Creature)player;
			}
		}
		if (looser!=null)
		{
			int totalKinah = 0;
			Random generator = new Random();
			if (winner instanceof Player) {
				Player pWinner = (Player)winner;
				int lvlDiff = looser.getLevel()-pWinner.getLevel();
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
				int exp = (int)Math.round(pWinner.getCommonData().getExpNeed()*0.07*rate*Rates.XP_RATE);
				int dp = (int)Math.round(40*rate);
				int randomKinah = generator.nextInt(50)+1;
				Inventory kina = new Inventory();
				kina.getKinahFromDb(pWinner.getObjectId());
				int kinah = kina.getKinahCount();
				kina.putKinahToDb(pWinner.getObjectId(), totalKinah);
				totalKinah = kinah + randomKinah;
				pWinner.getCommonData().setExp(player.getCommonData().getExp()+exp);
				pWinner.setDP(player.getDP()+dp);
				log.info("player {name:"+pWinner.getName()+",level:"+pWinner.getLevel()+"} win battle against level "+looser.getLevel()+", and gain :{xp:"+exp+",dp:"+dp+",kn:"+randomKinah+"}");
			} else {
				log.info("creature {name:"+winner.getName()+",id:"+winner.getObjectId()+",level:"+winner.getLevel()+"} won battle");
			}
			winner.setHP(winner.getMaxHP());
			winner.setMP(winner.getMaxMP());
			if (!(looser instanceof Player)) {
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(looser.getObjectId(),13,winner.getObjectId()), true);
				PacketSendUtility.broadcastPacket(player, new SM_LOOT_STATUS(looser.getObjectId(),0), true);
			}
			int randomUniqueId = UUID.randomUUID().hashCode();
			sendPacket(new SM_INVENTORY_UPDATE(randomUniqueId, 182400001, 2211143, totalKinah));
			looser.onDie();
		}
	}
}
