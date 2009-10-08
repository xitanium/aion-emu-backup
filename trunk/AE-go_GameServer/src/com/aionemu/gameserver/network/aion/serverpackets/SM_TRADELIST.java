/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.model.templates.TradeListTemplate;
import com.google.inject.Inject;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_TRADELIST extends AionServerPacket
{
	@Inject
	private TradeListData tradeListData;
	@Inject
	private World world;
	private int	targetObjectId;
	private TradeListTemplate tlist;
		
	public SM_TRADELIST(int targetObjectId)
	{
		this.targetObjectId = targetObjectId;
		
		Npc npc = (Npc) world.findAionObject(targetObjectId);
		tlist = tradeListData.getTradeListTemplate(npc.getNpcId());
	}

	/**
	* {@inheritDoc}
	*/
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		

		if ((tlist != null)&&(tlist.getNpcId()!=0)&&(tlist.getCount()!=0))
		{
			writeD(buf, targetObjectId);
			writeD(buf, 0xc801); //unknown for 1.5.x
			writeC(buf, 0); // unknown
			writeH(buf, tlist.getCount()); // unknown
			
			if(tlist.getlistId0() != 0)
			{
				writeD(buf, tlist.getlistId0()); // unknown
			}
			if(tlist.getlistId1() != 0)
			{
				writeD(buf, tlist.getlistId1()); // unknown
			}
			if(tlist.getlistId2() != 0)
			{
				writeD(buf, tlist.getlistId2()); // unknown
			}
			if(tlist.getlistId3() != 0)
			{
				writeD(buf, tlist.getlistId3()); // unknown
			}
			if(tlist.getlistId4() != 0)
			{
				writeD(buf, tlist.getlistId4()); // unknown
			}
			if(tlist.getlistId5() != 0)
			{
				writeD(buf, tlist.getlistId5()); // unknown
			}
			if(tlist.getlistId6() != 0)
			{
				writeD(buf, tlist.getlistId6()); // unknown
			}
		}
	}
}
