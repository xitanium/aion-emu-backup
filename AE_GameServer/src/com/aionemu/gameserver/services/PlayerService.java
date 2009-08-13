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

package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.CacheConfig;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.PlayerAppearanceDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerMacrossesDAO;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.MacroList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.clientpackets.CM_ENTER_WORLD;
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUIT;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.KnownList;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

import java.sql.Timestamp;

/**
 * 
 * This class is designed to do all the work related with loading/storing players.<br>
 * Same with storing, {@link #storePlayer(com.aionemu.gameserver.model.gameobjects.player.Player)} stores all player
 * data like appearance, items, etc...
 * 
 * @author SoulKeeper
 */
public class PlayerService
{
	private static Logger				log				= Logger.getLogger(PlayerService.class);

	private CacheMap<Integer, Player>	playerCache		= CacheMapFactory.createSoftCacheMap("Player", "player");

	private IDFactory					aionObjectsIDFactory;
	private World						world;
	private PlayerInitialData           playerInitialData;

	// DAOs
	private PlayerDAO					playerDAO		= DAOManager.getDAO(PlayerDAO.class);
	private PlayerMacrossesDAO			macrosesDAO		= DAOManager.getDAO(PlayerMacrossesDAO.class);
	private PlayerAppearanceDAO			appereanceDAO	= DAOManager.getDAO(PlayerAppearanceDAO.class);
	private FriendListDAO				friendsDAO		= DAOManager.getDAO(FriendListDAO.class);


	@Inject
	public PlayerService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world, PlayerInitialData playerInitialData)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
		this.playerInitialData = playerInitialData;
	}

	/**
	 * Checks if name is already taken or not
	 * 
	 * @param name
	 *            character name
	 * @return true if is free, false in other case
	 */
	public boolean isFreeName(String name)
	{
		return !playerDAO.isNameUsed(name);
	}

	/**
	 * Checks if a name is valid. It should contain only english letters
	 * 
	 * @param name
	 *            character name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidName(String name)
	{
		return Config.CHAR_NAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Stores newly created player
	 * 
	 * @param player
	 *            player to store
	 * @return true if character was successful saved.
	 */
	public boolean storeNewPlayer(Player player, String accountName, int accountId)
	{
		return playerDAO.saveNewPlayer(player.getCommonData(), accountId, accountName) && appereanceDAO.store(player);
	}

	/**
	 * Stores player data into db
	 * 
	 * @param player
	 */
	public void storePlayer(Player player)
	{
		playerDAO.storePlayer(player);
	}

	/**
	 * Returns the player with given objId (if such player exists)
	 * 
	 * @param playerObjId
	 * @return
	 */
	public Player getPlayer(int playerObjId)
	{
		Player player = playerCache.get(playerObjId);
		if(player != null)
			return player;

		PlayerCommonData pcd = playerDAO.loadPlayerCommonData(playerObjId, world);
		PlayerAppearance appereance = appereanceDAO.load(playerObjId);
		MacroList macroses = macrosesDAO.restoreMacrosses(playerObjId);

		player = new Player(new PlayerController(), pcd, appereance);
		player.setMacroList(macroses);
		player.setKnownlist(new KnownList(player));
		player.setFriendList(friendsDAO.load(player, world));
		
		if(CacheConfig.CACHE_PLAYERS)
			playerCache.put(playerObjId, player);	

		return player;
	}

	/**
	 * This method is used for creating new players
	 * 
	 * @param playerCommonData
	 * @param playerAppearance
	 * @return
	 */
	public Player newPlayer(PlayerCommonData playerCommonData, PlayerAppearance playerAppearance)
	{
		// TODO values should go from template
		LocationData ld = playerInitialData.getSpawnLocation(playerCommonData.getRace());

		WorldPosition position = world.createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(), ld.getHeading());

		playerCommonData.setPosition(position);

		// TODO: starting skills
		// TODO: starting items;

		Player player = new Player(new PlayerController(), playerCommonData, playerAppearance);

		return player;
	}

	/**
	 * This method is called just after player logged in to the game.<br>
	 * <br>
	 * <b><font color='red'>NOTICE: </font> This method called only from {@link CM_ENTER_WORLD} and must not be called from anywhere else.</b>
	 * 
	 * @param player
	 */
	public void playerLoggedIn(Player player)
	{
		player.getCommonData().setOnline(true);

		player.onLoggedIn();
	}

	/**
	 * This method is called when player leaves the game, which includes just two cases: either player goes back to char
	 * selection screen or it's leaving the game [closing client].<br><br>
	 * 
	 * <b><font color='red'>NOTICE: </font> This method is called only from {@link AionConnection} and {@link CM_QUIT} and must not be called from anywhere else</b>
	 * @param player
	 */
	public void playerLoggedOut(Player player)
	{
		player.onLoggedOut();
		
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));
			
		player.getController().delete();
		player.setClientConnection(null);
		
		storePlayer(player);
	}

	/**
	 * Cancel Player deletion process if its possible.
	 * 
	 * @param accData
	 *            PlayerAccountData
	 * 
	 * @return True if deletion was successful canceled.
	 */
	public boolean cancelPlayerDeletion(PlayerAccountData accData)
	{
		if(accData.getDeletionDate() == null)
			return true;

		if(accData.getDeletionDate().getTime() > System.currentTimeMillis())
		{
			accData.setDeletionDate(null);
			storeDeletionTime(accData);
			return true;
		}
		return false;
	}

	/**
	 * Starts player deletion process if its possible. If deletion is possible character should be deleted after 5
	 * minutes.
	 * 
	 * @param accData
	 *            PlayerAccountData
	 */
	public void deletePlayer(PlayerAccountData accData)
	{
		if(accData.getDeletionDate() != null)
			return;

		accData.setDeletionDate(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));
		storeDeletionTime(accData);
	}

	/**
	 * Completely removes player from database
	 * 
	 * @param playerId
	 *            id of player to delete from db
	 */
	void deletePlayerFromDB(int playerId)
	{
		playerDAO.deletePlayer(playerId);
	}

	/**
	 * Updates deletion time in database
	 * 
	 * @param accData
	 *            PlayerAccountData
	 */
	private void storeDeletionTime(PlayerAccountData accData)
	{
		playerDAO.updateDeletionTime(accData.getPlayerCommonData().getPlayerObjId(), accData.getDeletionDate());
	}

	/**
	 * 
	 * @param objectId
	 * @param creationDate
	 */
	public void storeCreationTime(int objectId, Timestamp creationDate)
	{
		playerDAO.storeCreationTime(objectId, creationDate);
	}

	/**
	 * Add macro for player
	 * 
	 * @param player
	 *            Player
	 * @param macroOrder
	 *            Macro order
	 * @param macroXML
	 *            Macro XML
	 */
	public void addMacro(Player player, int macroOrder, String macroXML)
	{
		if(player.getMacroList().addMacro(macroOrder, macroXML))
		{
			macrosesDAO.addMacro(player.getObjectId(), macroOrder, macroXML);
		}
	}

	/**
	 * Remove macro with specified index from specified player
	 * 
	 * @param player
	 *            Player
	 * @param macroOrder
	 *            Macro order index
	 */
	public void removeMacro(Player player, int macroOrder)
	{
		if(player.getMacroList().removeMacro(macroOrder))
		{
			macrosesDAO.deleteMacro(player.getObjectId(), macroOrder);
		}
	}
	
	/**
	 * Adds two players to eachothers friend lists, and updates the database<br />
	 * <ul><li>Does not send notification packets, and does not send new list packet</li></ul>
	 * @param friend1
	 * @param friend2
	 * @return Successs
	 */
	public void addFriends(Player friend1, Player friend2)
	{
		friendsDAO.addFriends(friend1, friend2);

		friend1.getFriendList().addFriend( new Friend(friend2.getCommonData()));
		friend2.getFriendList().addFriend( new Friend(friend1.getCommonData()));	
	}
	
	/**
	 * Deletes two players from eachothers friend lists, and updates the database
	 * <ul><li>Note: Does not send notification packets, and does not send new list packet</ul></li>
	 * @param exFriend1 Object ID
	 * @param exFriend2 Object ID
	 * @return Success
	 */
	public void delFriends(int exFriend1Id, int exFriend2Id)
	{
		friendsDAO.delFriends(exFriend1Id, exFriend2Id);
		
		Player friend1Player = playerCache.get(exFriend1Id);
		Player friend2Player = playerCache.get(exFriend2Id);
		
		if (friend1Player == null)
			friend1Player = world.findPlayer(exFriend1Id);
		if (friend2Player == null)
			friend2Player = world.findPlayer(exFriend2Id);
		
		if (friend1Player != null)
		{
		
			friend1Player.getFriendList().delFriend(exFriend2Id);
		}
		
		if (friend2Player != null)
		{
			friend1Player.getFriendList().delFriend(exFriend1Id);
		}
		
	}
	
}
