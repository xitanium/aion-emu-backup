/**
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.PlayerExperienceTable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;
import com.aionemu.gameserver.network.aion.clientpackets.CM_CREATE_CHARACTER;

/**
 * In this packet Server is sending User Info?
 * 
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket
{
	/** Logger for this class. */
	private static final Logger	log	= Logger.getLogger(SM_STATS_INFO.class);
	/**
	 * Player that stats info will be send
	 */
	private Player	player;
	private PlayerExperienceTable exp;

	/**
	 * Constructs new <tt>SM_UI</tt> packet
	 * 
	 * @param player
	 */
	public SM_STATS_INFO(Player player)
	{
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		PlayerCommonData pcd = player.getCommonData();
		
		writeD(buf, player.getObjectId());

		writeD(buf, 123456789);// unk 52910559 32759DF

		writeH(buf, 110);// current power [confirmed]
		writeH(buf, 110);// current health [confirmed]
		writeH(buf, 100);// current accuracy [confirmed]
		writeH(buf, 100);// current agility [confirmed]
		writeH(buf, 90);// current knowledge [confirmed]
		writeH(buf, 90);// current will [confirmed]

		writeH(buf, 0);// water res [confirmed]
		writeH(buf, 0);// wind res [confirmed]
		writeH(buf, 0);// earth res [confirmed]
		writeH(buf, 1);// fire res [confirmed]

		writeD(buf, 0);// unk 0
		writeH(buf, 1);// level [confirmed] ***
		
		writeH(buf, 0); //
		writeD(buf, 0);//
		writeQ(buf, 650);// max xp [confirmed]
		writeQ(buf, 0); // recoverable xp [confirmed]
		writeQ(buf, 0); // current xp [confirmed]
		
		writeD(buf, 0); //
		writeD(buf, 284); // max hp [confirmed] {cur hp is as % in CI packet}
		writeD(buf, 284);// if set to 0, then client thinks you're dead! [confirmed] (why there is whole int ?)=>because it's the curHp ... Niato

		writeD(buf, 500);// max mp [confirmed]
		writeD(buf, 300);// cur mp [confirmed]

		writeH(buf, 4000);// max dp [confirmed]
		writeH(buf, 0);// cur dp [confirmed]

		writeD(buf, 70);// unk 60 MaxFlightTime

		writeD(buf, 60);// unk1 0 CurentflightTime

		writeH(buf, 0);// unk2

		writeH(buf, 29); // Main-Hand attack [confirmed]
		writeH(buf, 0); // Off-Hand attack [confirmed]

		writeH(buf, 46);// Physical defence [confirmed]

		writeH(buf, 0);//

		writeH(buf, 18); // Magical resistance [confirmed]

		writeH(buf, 0);// 
		writeH(buf, 16320);//
		writeH(buf, 1400);// 
		writeH(buf, 112);// evasion [confirmed]
		writeH(buf, 247);// parry [confirmed]
		writeH(buf, 74);// block [confirmed]

		writeH(buf, 52);// Main-Hand Crit Rate [confirmed]
		writeH(buf, 0);// 0ff-Hand Crit Rate [confirmed]

		writeH(buf, 198);// Main-Hand Accuracy [confirmed]
		writeH(buf, 0);// Off-Hand Accuracy [confirmed]

		writeH(buf, 2);// unk10
		writeH(buf, 12);// Magic Accuracy [confirmed]
		writeH(buf, 14); // unk
		writeH(buf, 0); // Magic Boost [confirmed]

		writeH(buf, 27);// unk12
		writeH(buf, 0);// unk12

		writeD(buf, 2);// unk13 2
		writeD(buf, 0);// unk14 0
		writeD(buf, 0);// unk15 0
		writeH(buf, 0);// unk12
		writeH(buf, pcd.getPlayerClass().getClassId());// class id 
		writeD(buf, 0);// unk17 0

		writeH(buf, 110);// base power [confirmed]
		writeH(buf, 110);// base health [confirmed]
		writeH(buf, 100);// base accuracy [confirmed]
		writeH(buf, 100);// base agility [confirmed]
		writeH(buf, 90);// base knowledge [confirmed]
		writeH(buf, 90); // base will [confirmed]

		writeH(buf, 0);// base water res [confirmed]
		writeH(buf, 0);// base wind res [confirmed]
		writeH(buf, 0);// base earth res [confirmed]
		writeH(buf, 0);// base fire res [confirmed]

		writeD(buf, 0);// unk23 0 ? ***

		writeD(buf, 284);// base hp [confirmed]
		writeD(buf, 170);// base mp [confirmed]

		writeD(buf, 4000);// unk26 maxDp ?
		writeD(buf, 45);// unk27 60 curent flight time?

		writeH(buf, 19);// base Main-Hand Attack [confirmed]
		writeH(buf, 0);// base Off-Hand Attack [confirmed]

		writeH(buf, 0); // ***
		writeH(buf, 44); // base Physical Def [confirmed]

		writeH(buf, 18); // base Magical res [confirmed]
		writeH(buf, 0); // *** ??

		writeD(buf, 1069547520);// unk31 1069547520 3FC00000 ** co to ?

		writeH(buf, 112); // base evasion [confirmed]
		writeH(buf, 247); // base parry [confirmed]
		writeH(buf, 74); // base block [confirmed]

		writeH(buf, 52); // base Main-Hand crit rate [confirmed]
		writeH(buf, 0); // base Off-Hand crit rate [confirmed]

		writeH(buf, 198); // base Main-Hand Accuracy [confirmed]
		writeH(buf, 0); // base Off-Hand Accuracy [confirmed]

		writeH(buf, 2);// unk35 co to ? ***

		writeH(buf, 12); // base Magic Acc [confirmed]
		writeH(buf, 14);// unk36 ***

		writeH(buf, 00); // base Magic Boost [confirmed]

		writeH(buf, 0); // ***
	}
}
