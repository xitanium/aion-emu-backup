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
package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BlockListDAO;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Handles activities related to social groups ingame such as the buddy list, legions, etc
 * @author Ben
 *
 */
public class SocialService
{
	private World 			world;
	private PlayerService	playerService;
	
	@Inject
	public SocialService(World world, PlayerService playerService)
	{
		this.world = world;
		this.playerService = playerService;
	}
	
	/**
	 * Blocks the given object ID for the given player.<br />
	 * <ul><li>Does not send packets</li></ul>
	 * 
	 * @param player
	 * @param blockedPlayer
	 * @param reason
	 * @return Success
	 */
	public boolean addBlockedUser(Player player, Player blockedPlayer, String reason)
	{
		if (DAOManager.getDAO(BlockListDAO.class).addBlockedUser(player.getObjectId(),	blockedPlayer.getObjectId(), reason))
		{
			player.getBlockList().add(new BlockedPlayer(blockedPlayer.getCommonData(),reason));
			
			player.getClientConnection()
				.sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.BLOCK_SUCCESSFUL, blockedPlayer.getName()));
			player.getClientConnection()
				.sendPacket(new SM_BLOCK_LIST());
			
			return true;
		}
		return false;
	}
	
	/**
	 * Unblocks the given object ID for the given player.<br />
	 * <ul><li>Does not send packets</li></ul>
	 * 
	 * @param player
	 * @param blockedUserId
	 * 			ID of player to unblock
	 * @return Success
	 */
	public boolean delBlockedUser(Player player, int blockedUserId)
	{
		if (DAOManager.getDAO(BlockListDAO.class).delBlockedUser(player.getObjectId(), blockedUserId))
		{
			player.getBlockList().remove(blockedUserId);
			player.getClientConnection()
				.sendPacket(new SM_BLOCK_RESPONSE(
								SM_BLOCK_RESPONSE.UNBLOCK_SUCCESSFUL,
								DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(blockedUserId, world).getName()
							));
			
			player.getClientConnection()
				.sendPacket(new SM_BLOCK_LIST());
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the reason for blocking a user
	 * @param player
	 * 			Player whos block list is to be edited
	 * @param target
	 * 			Whom to block
	 * @param reason
	 * 			Reason to set
	 * @return Success - May be false if the reason was the same and therefore not edited
	 */
	public boolean setBlockedReason(Player player, BlockedPlayer target, String reason)
	{
		
		if (!target.getReason().equals(reason))
		{
			if (DAOManager.getDAO(BlockListDAO.class).setReason(player.getObjectId(), target.getObjId(), reason))
			{
				target.setReason(reason);
				player.getClientConnection()
				.sendPacket(new SM_BLOCK_LIST());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds two players to eachothers friend lists, and updates the database<br />
	 * @param friend1
	 * @param friend2
	 * @return Successs
	 */
	public void makeFriends(Player friend1, Player friend2)
	{
		DAOManager.getDAO(FriendListDAO.class).addFriends(friend1, friend2);

		friend1.getFriendList().addFriend( new Friend(friend2.getCommonData()));
		friend2.getFriendList().addFriend( new Friend(friend1.getCommonData()));	
		
		friend1.getClientConnection()
			.sendPacket(new SM_FRIEND_LIST());
		friend2.getClientConnection()
			.sendPacket(new SM_FRIEND_LIST());
		
		friend1.getClientConnection()
			.sendPacket(new SM_FRIEND_RESPONSE(friend2.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
		friend2.getClientConnection()
			.sendPacket(new SM_FRIEND_RESPONSE(friend1.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
	}
	
	/**
	 * Deletes two players from eachothers friend lists, and updates the database
	 * <ul><li>Note: Does not send notification packets, and does not send new list packet</ul></li>
	 * @param exFriend1Id Object ID
	 * @param exFriend2Id Object ID
	 * @return Success
	 */
	public void delFriends(int exFriend1Id, int exFriend2Id)
	{
		DAOManager.getDAO(FriendListDAO.class).delFriends(exFriend1Id, exFriend2Id);
		
		Player friend1Player = playerService.getCachedPlayer(exFriend1Id);
		Player friend2Player = playerService.getCachedPlayer(exFriend2Id);
		
		if (friend1Player == null)
			friend1Player = world.findPlayer(exFriend1Id);
		if (friend2Player == null)
			friend2Player = world.findPlayer(exFriend2Id);
		
		if (friend1Player != null)
		{
		
			friend1Player.getFriendList().delFriend(exFriend2Id);
			
			if (friend1Player.isOnline())
			{
				friend1Player.getClientConnection()
					.sendPacket(new SM_FRIEND_LIST());
				friend1Player.getClientConnection()
					.sendPacket(new SM_FRIEND_RESPONSE(
									DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(exFriend2Id, world).getName(),
									SM_FRIEND_RESPONSE.TARGET_REMOVED));
			}
		}
		
		if (friend2Player != null)
		{
			friend2Player.getFriendList().delFriend(exFriend1Id);
			
			if (friend2Player.isOnline())
			{
				// TODO: Fixme: Possible NPE if friend1Player is null.
				friend2Player.getClientConnection()
				.sendPacket(new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.DELETED, friend1Player.getName()));
				friend2Player.getClientConnection().sendPacket(new SM_FRIEND_LIST());
			}
		}
	
		
	}
}
