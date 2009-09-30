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
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_ATTACK_STATUS extends AionServerPacket
{
	private int	attackno;
    private int remainHp;
    private int targetObjId; 
	
	public SM_ATTACK_STATUS(Creature target, int attackno)
	{
		this.targetObjId = target.getObjectId();
		this.attackno = attackno + 1;// empty
		this.remainHp = (target.getHP()<0)?0:target.getHP();
	}

	/**
	 * {@inheritDoc} ddchcc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		writeD(buf, targetObjId);
		writeD(buf, 0);
		writeC(buf, 5); // unknown?? type 5
		writeH(buf, remainHp); // unknown remain hp??
		writeC(buf, 0); // unknown
		writeC(buf, 0x94); // unknown
		
        
	}	
}
