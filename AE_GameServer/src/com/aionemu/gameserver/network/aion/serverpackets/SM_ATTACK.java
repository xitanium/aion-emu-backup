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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerStats;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.World;

import java.util.Random;

/**
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_ATTACK extends AionServerPacket
{
	private World world;
	private int attackerobjectid;
	private int	targetObjectId;
	private int	attackno;
	private int	time;
	private int	type;
	
	public SM_ATTACK(World world, int attackerobjectid ,int targetObjectId,int attackno,int time,int type)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.attackno = attackno + 1;// empty
		this.time = time ;// empty
		this.type = type;// empty
		this.world = world;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		Creature attacker = (Creature)world.findAionObject(attackerobjectid);
		Creature target = (Creature)world.findAionObject(targetObjectId);
		Random generator = new Random();
		int damages = (int)Math.round((attacker.getPower()-target.getBlock()/10)+(attacker.getLevel()-target.getLevel())*10)+generator.nextInt(10);
		if (damages<0) {
			damages = generator.nextInt(10);
		}
		attacker.setHP(attacker.getHP()-damages);
		target.setHP(target.getHP()-damages);
		//attacker
		writeD(buf, attackerobjectid);
		writeC(buf, attackno); // unknown
		writeH(buf, time); // unknown
		writeC(buf, type); // unknown
		//defender
		writeD(buf, targetObjectId);
		writeC(buf, attackno + 1);
		writeH(buf, 84); // unknown
		writeC(buf, 0); // unknown
/*		//demage
		writeC(buf, 2);
		
		writeD(buf, 19); // damage
		writeH(buf, 10); // unknown
		
		writeD(buf, 17); // damage
		writeH(buf, 10); // unknown
	*/
		writeC(buf, 1);
		writeD(buf, damages); // damage
		writeH(buf, 10); // unknown
		
		writeC(buf, 0);
	}	
}
