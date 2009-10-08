package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Random;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_CASTSPELL_END extends AionServerPacket
{
	private int attackerobjectid;
	private int	targetObjectId;
	private int	spellid;
	private int	level;
	private int	unk; //can cast?? 
	private int damage;
	
	public SM_CASTSPELL_END(int attackerobjectid ,int spellid,int level,int unk, int targetObjectId, int damage)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.spellid = spellid ;// empty
		this.level = level ;
		this.unk = unk ;
		this.damage = damage;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		writeD(buf, attackerobjectid);
		writeC(buf, 0x00);
		writeD(buf, targetObjectId);
		writeH(buf, spellid); 
		writeC(buf, level);
		writeD(buf, 0x0000000A);
		writeC(buf, 0xFE); //unk??
		writeC(buf, 0x01); //unk??
		writeD(buf, 0x00000200); //unk??

		writeH(buf, 0x0001);
		writeD(buf, targetObjectId); 
		writeD(buf, 0x00645800); // unk?? abnormal eff id ??
		writeD(buf, 0x00010010); // unk??
		
		writeD(buf, damage); // damage
		writeH(buf, 0x000A);
	}	
}
