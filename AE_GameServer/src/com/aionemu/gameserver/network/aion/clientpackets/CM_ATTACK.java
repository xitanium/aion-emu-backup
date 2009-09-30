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
	private long                exp;
	private long                maxexp;
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
		int playerobjid = player.getObjectId();
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player.getActiveRegion().getWorld(),playerobjid,targetObjectId,attackno,time,type), true);

		at = player.getatcount();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,30,playerobjid), true);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,19,playerobjid), true);
		sendPacket(new SM_ATTACK(player.getActiveRegion().getWorld(),targetObjectId,playerobjid,at,time,type));
		sendPacket(new SM_ATTACK_STATUS(player,99));
	  	at = at + 1;
	    player.setatcount(at);
		
		sendPacket(new SM_ATTACK_STATUS((Creature)player.getActiveRegion().getWorld().findAionObject(targetObjectId),attackno));
		if (((Creature)player.getActiveRegion().getWorld().findAionObject(targetObjectId)).getHP()<=0)
		{
			World world = player.getActiveRegion().getWorld();
			Creature npc = (Creature) world.findAionObject(targetObjectId);
			Random generator = new Random();
			exp = (int)Math.round(player.getCommonData().getExpNeed()*0.07)+(npc.getLevel()-player.getLevel())*10;
			if (exp<=0) {
				exp = generator.nextInt(10);
			}
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,13,playerobjid), true);
			PacketSendUtility.broadcastPacket(player, new SM_LOOT_STATUS(targetObjectId,0), true);
			player.getCommonData().setExp(player.getCommonData().getExp()+exp);
			
			int randomKinah = generator.nextInt(50)+1;
			
			int randomUniqueId = UUID.randomUUID().hashCode();
			//int randomUniqueId = generator.nextInt(99999999)+generator.nextInt(99999999)+99999999+99999999; // To prevent replacement of other item.

			Inventory kina = new Inventory();
			kina.getKinahFromDb(playerobjid);
			int kinah = kina.getKinahCount();
			int totalKinah = kinah + randomKinah;
			kina.putKinahToDb(playerobjid, totalKinah);
			
			sendPacket(new SM_INVENTORY_UPDATE(randomUniqueId, 182400001, 2211143, totalKinah));
			
			//schedule decay task
			//TODO move to npc fully
			npc.onDie();

		}
	}
}