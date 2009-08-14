/*
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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;

/**
 * @author Ben
 *
 */
public class CM_FRIEND_DEL extends AionClientPacket
{

	private String 				targetName;
	@Inject
	private PlayerService		playerService;
	private static Logger		log				= Logger.getLogger(CM_FRIEND_DEL.class);
	
	public CM_FRIEND_DEL(int opcode)
	{
		super(opcode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetName = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		
		Player activePlayer = getConnection().getActivePlayer();
		Friend target = activePlayer.getFriendList().getFriend(targetName);
		if (target == null)
		{
			log.warn(activePlayer.getName() + " tried to delete friend " + targetName + " who is not his friend");
			sendPacket(SM_SYSTEM_MESSAGE.BUDDYLIST_NOT_IN_LIST);
		}
		else
		{
			playerService.delFriends(activePlayer.getObjectId(), target.getOid());
			
			activePlayer.getClientConnection().sendPacket(new SM_FRIEND_LIST());
			sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_REMOVED));
			
			Player targetPlayer = activePlayer.getActiveRegion().getWorld().findPlayer(target.getOid());
			//If the friend is online
			if (targetPlayer != null)
			{
				//Notify him that he was deleted
				targetPlayer.getClientConnection()
					.sendPacket(new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.DELETED, activePlayer.getName()));
				targetPlayer.getClientConnection().sendPacket(new SM_FRIEND_LIST());
			}
			
		}
		

	}

}
