/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Class that that is responsible for loading/storing {@link com.aionemu.gameserver.model.gameobjects.player.Player}
 * object from MySQL 5.
 * 
 * @author SoulKeeper
 */
public class MySQL5PlayerDAO extends PlayerDAO
{

	/** Logger */
	private static final Logger					log					= Logger.getLogger(MySQL5PlayerDAO.class);

	/** Cache for {@link PlayerCommonData} objects */
	private CacheMap<Integer, PlayerCommonData>	playerCommonData	= CacheMapFactory.createCacheMap("PlayerCommon","player common");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNameUsed(final String name)
	{
		PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM players WHERE ? = players.name");
		try
		{
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch(SQLException e)
		{
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePlayer(final Player player)
	{
		DB.insertUpdate("UPDATE players SET name=?, exp=?, x=?, y=?, z=?, heading=?, world_id=?, player_class=?, last_online=?, admin=?, note=?, online=?, hp=?, mp=?, dp=? WHERE id=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] storing player "+player.getObjectId()+" "+player.getName());
				
				stmt.setString(1, player.getName().replace("*MJ* ",""));
				stmt.setLong(2, player.getCommonData().getExp());
				stmt.setFloat(3, player.getX());
				stmt.setFloat(4, player.getY());
				stmt.setFloat(5, player.getZ());
				stmt.setInt(6, player.getHeading());
				stmt.setInt(7, player.getWorldId());
				stmt.setString(8, player.getCommonData().getPlayerClass().toString());
				stmt.setTimestamp(9, player.getCommonData().getLastOnline());
				stmt.setInt(10, player.getCommonData().getAdmin());
				stmt.setString(11,player.getCommonData().getNote());
				stmt.setInt(12, player.getObjectId());
				stmt.setBoolean(13, player.isOnline());
				stmt.setInt(14, player.getLifeStats().getHp());
				stmt.setInt(15, player.getLifeStats().getMp());
				stmt.setInt(16, player.getLifeStats().getDp());
				stmt.execute();
			}
		});
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewPlayer(final PlayerCommonData pcd, final int accountId, final String accountName)
	{
		boolean success = DB.insertUpdate(
				"INSERT INTO players(id, `name`, exp, account_id, account_name, x, y, z, heading, world_id, gender, race, player_class , admin, online, hp, mp, dp) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new IUStH(){
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
					{
						log.debug("[DAO: MySQL5PlayerDAO] saving new player: "+pcd.getPlayerObjId()+" "+pcd.getName());
						
						preparedStatement.setInt(1, pcd.getPlayerObjId());
						preparedStatement.setString(2, pcd.getName().replace("*MJ* ", ""));
						preparedStatement.setLong(3, pcd.getExp());
						preparedStatement.setInt(4, accountId);
						preparedStatement.setString(5, accountName);
						preparedStatement.setFloat(6, pcd.getPosition().getX());
						preparedStatement.setFloat(7, pcd.getPosition().getY());
						preparedStatement.setFloat(8, pcd.getPosition().getZ());
						preparedStatement.setInt(9, pcd.getPosition().getHeading());
						preparedStatement.setInt(10, pcd.getPosition().getMapId());
						preparedStatement.setString(11, pcd.getGender().toString());
						preparedStatement.setString(12, pcd.getRace().toString());
						preparedStatement.setString(13, pcd.getPlayerClass().toString());
						preparedStatement.setInt(14, pcd.getAdmin());
						preparedStatement.setBoolean(15, pcd.isOnline());
						preparedStatement.setInt(16, (pcd.getPlayer()!=null)?pcd.getPlayer().getLifeStats().getHp():0);
						preparedStatement.setInt(17, (pcd.getPlayer()!=null)?pcd.getPlayer().getLifeStats().getMp():0);
						preparedStatement.setInt(18, (pcd.getPlayer()!=null)?pcd.getPlayer().getLifeStats().getDp():0);
						preparedStatement.execute();
					}
				});
		if(success)
		{
			playerCommonData.put(pcd.getPlayerObjId(), pcd);
		}
		return success;
	}

	private Object	pcdLock	= new Object();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerCommonData loadPlayerCommonData(final int playerObjId, final World world)
	{

		PlayerCommonData cached = playerCommonData.get(playerObjId);
		if(cached != null)
		{
			log.debug("[DAO: MySQL5PlayerDAO] PlayerCommonData for id: "+playerObjId+" obtained from cache");
			return cached;
		}
		final PlayerCommonData cd = new PlayerCommonData(playerObjId);

		boolean success = DB.select("SELECT * FROM players WHERE id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerObjId);
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] loading from db "+playerObjId);
				
				resultSet.next();
				
				cd.setName(resultSet.getString("name").replace("*MJ* ", ""));
				cd.setExp(resultSet.getLong("exp"));
				cd.setRace(Race.valueOf(resultSet.getString("race")));
				cd.setGender(Gender.valueOf(resultSet.getString("gender")));
				cd.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
				cd.setAdmin(resultSet.getInt("admin"));
				cd.setLastOnline(resultSet.getTimestamp("last_online"));
				cd.setOnline(resultSet.getBoolean("online"));
				cd.setNote(resultSet.getString("note"));
				
				float x = resultSet.getFloat("x");
				float y = resultSet.getFloat("y");
				float z = resultSet.getFloat("z");
				byte heading = resultSet.getByte("heading");
				int worldId = resultSet.getInt("world_id");

				WorldPosition position = world.createPosition(worldId, x, y, z, heading);
				cd.setPosition(position);
				
				if (cd.getPlayer()!=null) {
					cd.getPlayer().getLifeStats().setDp(resultSet.getInt("dp"));
					cd.getPlayer().getLifeStats().setHp(resultSet.getInt("hp"));
					cd.getPlayer().getLifeStats().setMp(resultSet.getInt("mp"));
				}
			}
		});

		if(success)
		{
			synchronized(pcdLock)
			{
				cached = playerCommonData.get(playerObjId);
				if(cached != null)
					return cached;
				else
				{
					playerCommonData.put(playerObjId, cd);
					return cd;
				}
			}
		}
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlayer(int playerId)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM players WHERE id = ?");
		try
		{
			statement.setInt(1, playerId);
		}
		catch(SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		DB.executeUpdateAndClose(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getPlayerOidsOnAccount(final int accountId)
	{
		final List<Integer> result = new ArrayList<Integer>();
		boolean success = DB.select("SELECT id FROM players WHERE account_id = ?", new ParamReadStH(){
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					result.add(resultSet.getInt("id"));
				}
			}

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, accountId);
			}
		});

		return success ? result : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreationDeletionTime(final PlayerAccountData acData)
	{
		DB.select("SELECT creation_date, deletion_date FROM players WHERE id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, acData.getPlayerCommonData().getPlayerObjId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				rset.next();
				
				acData.setDeletionDate(rset.getTimestamp("deletion_date"));
				acData.setCreationDate(rset.getTimestamp("creation_date"));
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateDeletionTime(final int objectId, final Timestamp deletionDate)
	{
		DB.insertUpdate("UPDATE players set deletion_date = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, deletionDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCreationTime(final int objectId, final Timestamp creationDate)
	{
		DB.insertUpdate("UPDATE players set creation_date = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, creationDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}
	
	@Override
	public void storeLastOnlineTime(final int objectId, final Timestamp lastOnline)
	{
		DB.insertUpdate("UPDATE players set last_online = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, lastOnline);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}
	
	@Override
	public void onlinePlayer (final Player player, final boolean online) {
		DB.insertUpdate("UPDATE players SET online=? WHERE id=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] online status "+player.getObjectId()+" "+player.getName());
				stmt.setBoolean(1, online);
				stmt.setInt(2, player.getObjectId());
				stmt.execute();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);

		try
		{
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for(int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("id");
			}
			return ids;
		}
		catch(SQLException e)
		{
			log.error("Can't get list of id's from players table", e);
		}
		finally
		{
			DB.close(statement);
		}

		return new int[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
