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

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
/**
 * This packet is response for CM_CREATE_CHARACTER
 * 
 * @author Nemesiss, AEJTester
 * 
 */
public class SM_CREATE_CHARACTER extends PlayerInfo
{
	/** If response is ok */
	public static final int	RESPONSE_OK				= 0x00;
	
	
	public static final int	FAILED_TO_CREATE_THE_CHARACTER = 1;
	/** Failed to create the character due to world db error */
	public static final int RESPONSE_DB_ERROR = 2;
	/** The number of characters exceeds the maximum allowed for the server */
	public static final int RESPONSE_SERVER_LIMIT_EXCEEDED = 4;
	/** Invalid character name */
	public static final int	RESPONSE_INVALID_NAME	= 5;
	/** The name includes forbidden words */
	public static final int RESPONSE_FORBIDDEN_CHAR_NAME= 9;
	/** A character with that name already exists */
	public static final int	RESPONSE_NAME_ALREADY_USED			= 10;
	/** The name is already reserved */
	public static final int RESPONSE_NAME_RESERVED = 11;
	/** You cannot create characters of other races in the same server */
	public static final int RESPONSE_OTHER_RACE = 12;

	

	/**
	 * response code
	 */
	private final int		responseCode;

	/**
	 * Newly created player.
	 */
	private final PlayerAccountData	player;

	/**
	 * Constructs new <tt>SM_CREATE_CHARACTER </tt> packet
	 * 
	 * @param accPlData
	 *            playerAccountData of player that was created
	 * @param responseCode
	 *            response code (invalid nickname, nickname is already taken, ok)
	 */

	public SM_CREATE_CHARACTER(PlayerAccountData accPlData, int responseCode)
	{
		this.player = accPlData;
		this.responseCode = responseCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, responseCode);

		if(responseCode == RESPONSE_OK)
		{
			Inventory inventory = new Inventory();
			int activePlayer = player.getPlayerCommonData().getPlayerObjId();
			if (player.getPlayerCommonData().getPlayerClass().getClassId() == 0) {// warrior
					inventory.putIsEquipedToDb(activePlayer, 100000094, ItemSlot.MAIN_HAND);//sword for training
					inventory.putIsEquipedToDb(activePlayer, 110500003, ItemSlot.ARMOR);//Chain armor for training
					inventory.putIsEquipedToDb(activePlayer, 113500001, ItemSlot.PANTS);//Chain Greaves for training
					inventory.putItemToDb(activePlayer, 160000001, 12); // mercenary Fruit Juice
					inventory.putItemToDb(activePlayer, 169300001, 20); // bandage
			}
			if (player.getPlayerCommonData().getPlayerClass().getClassId() == 3) { //SCOUT	
					inventory.putIsEquipedToDb(activePlayer, 100200112, ItemSlot.MAIN_HAND);//Dagger for training
					inventory.putIsEquipedToDb(activePlayer, 110300015, ItemSlot.ARMOR);//Leather Jerkin for training
					inventory.putIsEquipedToDb(activePlayer, 113300005, ItemSlot.PANTS);//Leather leggins for training
					inventory.putItemToDb(activePlayer, 160000001, 12); // mercenary Fruit Juice
					inventory.putItemToDb(activePlayer, 169300001, 20); // bandage
			}
			if (player.getPlayerCommonData().getPlayerClass().getClassId() == 6) {//MAGE
					inventory.putIsEquipedToDb(activePlayer, 100600034, ItemSlot.MAIN_HAND);//SpellBook for training
					inventory.putIsEquipedToDb(activePlayer, 110100009, ItemSlot.ARMOR);//Cloth Tunic for Training
					inventory.putIsEquipedToDb(activePlayer, 113100005, ItemSlot.PANTS);//Cloth Leggins for training
					inventory.putItemToDb(activePlayer, 160000001, 12); // mercenary Fruit Juice
					inventory.putItemToDb(activePlayer, 169300001, 20); // bandage
			}
			if (player.getPlayerCommonData().getPlayerClass().getClassId() == 9) {//PRIEST
					inventory.putIsEquipedToDb(activePlayer, 100100011, ItemSlot.MAIN_HAND);//Mace for training
					inventory.putIsEquipedToDb(activePlayer, 110300292, ItemSlot.ARMOR);//Leather armor for training
					inventory.putIsEquipedToDb(activePlayer, 113300278, ItemSlot.PANTS);//Leather leg armor for training
					inventory.putItemToDb(activePlayer, 160000001, 12); // mercenary Fruit Juice
					inventory.putItemToDb(activePlayer, 169300001, 20); // bandage
			}
			
			writePlayerInfo(buf, player); // if everything is fine, all the character's data should be sent
			writeD(buf, player.getDeletionTimeInSeconds());
			writeD(buf, 0x00);// unk
		}
		else
		{
			writeB(buf, new byte[448]); // if something is wrong, only return code should be sent in the packet
		}
	}
}
