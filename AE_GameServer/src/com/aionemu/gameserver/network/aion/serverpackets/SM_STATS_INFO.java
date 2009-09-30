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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerStats;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;
import com.aionemu.gameserver.network.aion.clientpackets.CM_ATTACK;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.model.PlayerClass;
import java.lang.Math;

import sun.util.logging.resources.logging;

/**
 * In this packet Server is sending User Info?
 * 
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket
{

	/**
	 * Player that stats info will be send
	 */
	private Player	player;

		//static.
	public int power;
	public int health;
	public int agility;
	public int accuracy;
	public int knowledge;
	public int will;
	public int main_hand_attack;
	public int main_hand_crit_rate;
	public int water = 0;
	public int wind = 0;
	public int earth = 0;
	public int fire = 0;
	public int fly_time = 60;
	// needs calculations.
	public long maxhpc;
	public int maxhp;
	public int main_hand_accuracy;
	public int magic_accuracy;
	public long evasionc;
	public long blockc;
	public long parryc;
	public int evasion;
	public int block;
	public int parry;
	//unknown yet
	public int maxdp = 100;
	public int maxmp = 100;
	public int magic_boost = 0;
	public int pdef = 0;
	public int mres = 0;
	
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
		PlayerStats stats = player.getStats();

	////////////////////////////////////////////////////////
//////Sexy calculations of player stats. 
//////TODO: Find out some missing packet structures.
//////TODO: Move calculations to some other class
//////TODO: Find out formulas for other stat calculations.
///////////////////////////////////////////////////

//		//warrior
//		if (pcd.getPlayerClass().getClassId() == 0) {
//			maxhpc = Math.round ( 1.1688 * (player.getLevel() - 1) * (player.getLevel() - 1) + 45.149 * (player.getLevel() -1) + 284 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//
//
//
//		//gladiator
//		if (pcd.getPlayerClass().getClassId() == 1) {
//			maxhpc = Math.round ( 1.3393 * (player.getLevel() - 1) * (player.getLevel() - 1) + 48.246 * (player.getLevel() -1) + 342 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 115+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 115+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//templar
//			if (pcd.getPlayerClass().getClassId() == 2) {
//			maxhpc = Math.round ( 1.3288 * (player.getLevel() - 1) * (player.getLevel() - 1) + 51.878 * (player.getLevel() -1) + 281 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 115+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 105+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//scout
//		if (pcd.getPlayerClass().getClassId() == 3) {
//			maxhpc = Math.round ( 1.0297 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.823 * (player.getLevel() -1) + 219 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=18+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=3+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//assasin
//		if (pcd.getPlayerClass().getClassId() == 4) {
//			maxhpc = Math.round ( 1.0488 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.38 * (player.getLevel() -1) + 222 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=3+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//ranger
//		if (pcd.getPlayerClass().getClassId() == 5) {
//			maxhpc = Math.round ( 0.5 * (player.getLevel() - 1) * (player.getLevel() - 1) + 38.5 * (player.getLevel() -1) + 133 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 120+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=18+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=3+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//mage
//		if (pcd.getPlayerClass().getClassId() == 6) {
//			maxhpc = Math.round ( 0.7554 * (player.getLevel() - 1) * (player.getLevel() - 1) + 29.457 * (player.getLevel() -1) + 132 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 95+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 95+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 115+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 115+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=16+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=1+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//sorcerer
//		if (pcd.getPlayerClass().getClassId() == 7) {
//			maxhpc = Math.round ( 0.6352 * (player.getLevel() - 1) * (player.getLevel() - 1) + 24.852 * (player.getLevel() -1) + 112 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 120+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=16+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//spirit master
//		if (pcd.getPlayerClass().getClassId() == 8) {
//			maxhpc = Math.round ( 1 * (player.getLevel() - 1) * (player.getLevel() - 1) + 20.6 * (player.getLevel() -1) + 157 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 115+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 115+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=16+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//priest
//		if (pcd.getPlayerClass().getClassId() == 9) {
//			maxhpc = Math.round ( 1.0303 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.824 * (player.getLevel() -1) + 201 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 95+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 95+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 100+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 100+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=17+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//cleric
//		if (pcd.getPlayerClass().getClassId() == 10) {
//			maxhpc = Math.round ( 0.9277 * (player.getLevel() - 1) * (player.getLevel() - 1) + 35.988 * (player.getLevel() -1) +229 );
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 105+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 105+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*0.108); // change
//			main_hand_crit_rate=2+(int)Math.round((player.getLevel()-1)*0.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
//		//chanter
//		if (pcd.getPlayerClass().getClassId() == 11) {
//			maxhpc = Math.round ( 0.9277 * (player.getLevel() - 1) * (player.getLevel() - 1) + 35.988 * (player.getLevel() -1) + 229 ) - 3*player.getLevel();// not retail like, needs some fixes.
//			Long lObj = new Long(maxhpc);
//			maxhp = lObj.intValue();
//
//			power = 110+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			health = 105+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			agility = 90+(int)Math.round((player.getLevel()-1)*1.1688);// change
//			accuracy = 90+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			knowledge = 105+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			will = 110+(int)Math.round((player.getLevel()-1)*1.1688); // change
//			main_hand_attack=19+(int)Math.round((player.getLevel()-1)*1.108); // change
//			main_hand_crit_rate=1+(int)Math.round((player.getLevel()-1)*1.108); // change
//
//			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj2 = new Long(evasionc);
//			evasion = lObj2.intValue();
//			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj3 = new Long(parryc);
//			parry = lObj3.intValue();
//			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
//			Long lObj4 = new Long(blockc);
//			block = lObj4.intValue();
//			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
//			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
//		}
		//////////////////////////////////////////////
		///// THE END of calcs////////////////////////
		//////////////////////////////////////////////


		writeD(buf, player.getObjectId());
		writeD(buf, GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

		writeH(buf, stats.getPower());// [current power]
		writeH(buf, stats.getHealth());// [current health]
		writeH(buf, stats.getAccuracy());// [current accuracy]
		writeH(buf, stats.getAgility());// [current agility]
		writeH(buf, stats.getKnowledge());// [current knowledge]
		writeH(buf, stats.getWill());// [current will]

		writeH(buf, stats.getWater());// [current water]
		writeH(buf, stats.getWind());// [current wind]
		writeH(buf, stats.getEarth());// [current earth]
		writeH(buf, stats.getFire());// [current fire]

		writeD(buf, 0);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 0); // [unk]
		writeD(buf, player.getHP());// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, 0); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, stats.getMaxHP()); // [max hp]
		writeD(buf, player.getHP());// [unk]

		writeD(buf, stats.getMaxMP());// [max mana]
		writeD(buf, player.getMP());// [current mana]

		writeH(buf, stats.getMaxDP());// [max dp]
		writeH(buf, player.getDP());// [current dp]

		writeD(buf, 0);// [unk]

		writeD(buf, stats.getFlyTime());// [current fly time]

		writeH(buf, 0);// [unk]

		writeH(buf, stats.getMainHandAttack()); // [current main hand attack]
		writeH(buf, stats.getMainHandAttack()); // [off hand attack]

		writeH(buf, stats.getPdef());// [current pdef]

		writeH(buf, 0);// [unk]

		writeH(buf, stats.getMres()); // [current mres]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]attack range - do calculation
		writeH(buf, 1500);// attack speed - do calculation may be
		writeH(buf, stats.getEvasion());// [current evasion]
		writeH(buf, stats.getParry() );// [current parry]
		writeH(buf, stats.getBlock());// [current block]

		writeH(buf, stats.getMainHandCritRate());// [current main hand crit rate]
		writeH(buf, stats.getMainHandCritRate());// [current off hand crit rate]

		writeH(buf, stats.getMainHandAccuracy());// [current main_hand_accuracy]
		writeH(buf, stats.getMainHandAccuracy());// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, stats.getMagicAccuracy());// [current magic accuracy]
		writeH(buf, 0); // [unk]
		writeH(buf, stats.getMagicBoost()); // [current magic boost]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeD(buf, 2);// [unk]
		writeD(buf, 0);// [unk]
		writeD(buf, 0);// [unk]
		writeD(buf, pcd.getPlayerClass().getClassId());// [Player Class id]
		writeD(buf, 0);// [unk]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeH(buf, stats.getPower());// [base power]
		writeH(buf, stats.getHealth());// [base health]

		writeH(buf, stats.getAccuracy());// [base accuracy]
		writeH(buf, stats.getAgility());// [base agility]

		writeH(buf, stats.getKnowledge());// [base knowledge]
		writeH(buf, stats.getWill());// [base water res]

		writeH(buf, stats.getWater());// [base water res]
		writeH(buf, stats.getWind());// [base water res]
		
		writeH(buf, stats.getEarth());// [base earth resist]
		writeH(buf, stats.getFire());// [base water res]

		writeD(buf, 0);// [unk]

		writeD(buf, stats.getMaxHP());// [base hp]

		writeD(buf, stats.getMaxMP());// [base mana]

		writeD(buf, 0);// [unk]
		writeD(buf, 60);// [unk]

		writeH(buf, stats.getMainHandAttack());// [base main hand attack]
		writeH(buf, stats.getMainHandAttack());// [base off hand attack]

		writeH(buf, 0); // [unk] 
		writeH(buf, stats.getPdef()); // [base pdef]

		writeH(buf, stats.getMres()); // [base magic res]

		writeH(buf, 0); // [unk]

		writeD(buf, 1086324736);// [unk]

		writeH(buf, stats.getEvasion()); // [base evasion]

		writeH(buf, stats.getParry()); // [base parry]
 
		writeH(buf, stats.getBlock()); // [base block]

		writeH(buf, stats.getMainHandCritRate()); // [base main hand crit rate]
		writeH(buf, stats.getMainHandCritRate()); // [base off hand crit rate]

		writeH(buf, stats.getMainHandAccuracy()); // [base main hand accuracy]
		writeH(buf, stats.getMainHandAccuracy()); // [base off hand accuracy]

		writeH(buf, 0); // [unk]

		writeH(buf, stats.getMagicAccuracy());// [base magic accuracy]

		writeH(buf, 0); // [unk]
		writeH(buf, stats.getMagicBoost());// [base magic boost]

		writeH(buf, 0); // [unk]

	}
}
