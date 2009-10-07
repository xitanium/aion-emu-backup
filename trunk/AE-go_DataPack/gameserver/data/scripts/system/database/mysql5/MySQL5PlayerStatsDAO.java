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

import mysql5.MySQL5DAOUtils;

import org.apache.log4j.Logger;
import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerStatsDAO;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;

/**
 * 
 *  @author AEJTester
 */
public class MySQL5PlayerStatsDAO extends PlayerStatsDAO
{
	private static Logger log = Logger.getLogger(MySQL5PlayerStatsDAO.class); 
	
	public static final String INSERT_QUERY = "INSERT INTO `player_stats` ("
		+ "`player_id`,`current_hp`,`current_mp`,`current_dp`,`max_hp`,`max_mp`,`max_dp`,"
		+ "`attack_counter`,`power`,`health`,`agility`,`accuracy`,`knowledge`,`will`,"
		+ "`main_hand_attack`,`main_hand_crit_rate`,`off_hand_attack`,`off_hand_crit_rate`,"
		+ "`water`,`wind`,`earth`,`fire`,`fly_time`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String GAME_UPDATE_QUERY = "UPDATE `player_stats` SET "
	    + "`attack_counter`=?,`power`=?,`health`=?,`agility`=?,`accuracy`=?,"
	    + "`knowledge`=?,`will`=?,`main_hand_attack`=?,`main_hand_crit_rate`=?,"
		+ "`off_hand_attack`=?,`off_hand_crit_rate`=?,`water`=?,`wind`=?,`earth`=?,"
		+ "`fire`=?,`fly_time`=? WHERE `player_id`=?";
	
	public static final String LIFE_UPDATE_QUERY = "UPDATE `player_stats` SET "
	    + "`current_hp`=?,`current_mp`=?,`current_dp`=?,`max_hp`=?,`max_mp`=?,`max_dp`=? "
	    + "WHERE `player_id`=?";
	
	public static final String SELECT_QUERY = "SELECT * FROM `player_stats` WHERE `player_id` = ?";
	
	/** {@inheritDoc} */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
	
	/** {@inheritDoc} */
	@Override
	public PlayerGameStats loadGameStats(final int playerId) {
		final PlayerGameStats pgs = new PlayerGameStats ();
		
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					pgs.setAttackCounter(rset.getInt("attack_counter"));
					pgs.setPower(rset.getInt("power"));
					pgs.setHealth(rset.getInt("health"));
					pgs.setAgility(rset.getInt("agility"));
					pgs.setAccuracy(rset.getInt("accuracy"));
					pgs.setKnowledge(rset.getInt("knowledge"));
					pgs.setWill(rset.getInt("will"));
					pgs.setMainHandAttack(rset.getInt("main_hand_attack"));
					pgs.setMainHandCritRate(rset.getInt("main_hand_crit_rate"));
					pgs.setOffHandAttack(rset.getInt("off_hand_attack"));
					pgs.setOffHandCritRate(rset.getInt("off_hand_crit_rate"));
					pgs.setWater(rset.getInt("water"));
					pgs.setWind(rset.getInt("wind"));
					pgs.setEarth(rset.getInt("earth"));
					pgs.setFire(rset.getInt("fire"));
					pgs.setFlyTime(rset.getInt("fly_time"));
				}
			}
		});
		return pgs;
	}
	
	/** {@inheritDoc} */
	@Override
	public PlayerLifeStats loadLifeStats(final int playerId)
	{
		final PlayerLifeStats pls = new PlayerLifeStats ();
		
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					pls.setMaxHp(rset.getInt("max_hp"));
					pls.setMaxMp(rset.getInt("max_mp"));
					pls.setMaxDp(rset.getInt("max_dp"));
					pls.setCurrentHp(rset.getInt("current_hp"));
					pls.setCurrentDp(rset.getInt("current_mp"));
					pls.setCurrentMp(rset.getInt("current_dp"));
				}
			}
		});
		
		return null;
	}
	
	/** {@inheritDoc} */
	@Override
	public void storeNewStats(final int playerId, final PlayerLifeStats pls, final PlayerGameStats pgs) {
		DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, pls.getCurrentHp());
				stmt.setInt(3, pls.getCurrentMp());
				stmt.setInt(4, pls.getCurrentDp());
				stmt.setInt(5, pls.getMaxHp());
				stmt.setInt(6, pls.getMaxMp());
				stmt.setInt(7, pls.getMaxDp());
				stmt.setInt(8, pgs.getAttackCounter());
				stmt.setInt(9, pgs.getPower());
				stmt.setInt(10, pgs.getHealth());
				stmt.setInt(11, pgs.getAgility());
				stmt.setInt(12, pgs.getAccuracy());
				stmt.setInt(13, pgs.getKnowledge());
				stmt.setInt(14, pgs.getWill());
				stmt.setInt(15, pgs.getMainHandAttack());
				stmt.setInt(16, pgs.getMainHandCritRate());
				stmt.setInt(17, pgs.getOffHandAttack());
				stmt.setInt(18, pgs.getOffHandCritRate());
				stmt.setInt(19, pgs.getWater());
				stmt.setInt(20, pgs.getWind());
				stmt.setInt(21, pgs.getEarth());
				stmt.setInt(22, pgs.getFire());
				stmt.setInt(23, pgs.getFlyTime());
				stmt.execute();
			}
		});
	}
	
	/** {@inheritDoc} */
	@Override
	public void storeLifeStats(final int playerId, final PlayerLifeStats pls) {
		DB.insertUpdate(LIFE_UPDATE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, pls.getCurrentHp());
				stmt.setInt(2, pls.getCurrentMp());
				stmt.setInt(3, pls.getCurrentDp());
				stmt.setInt(4, pls.getMaxHp());
				stmt.setInt(5, pls.getMaxMp());
				stmt.setInt(6, pls.getMaxDp());
				stmt.setInt(7, playerId);
				stmt.execute();
			}
		});
	}
	
	/** {@inheritDoc} */
	@Override
	public void storeGameStats(final int playerId, final PlayerGameStats pgs) {
		DB.insertUpdate(GAME_UPDATE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, pgs.getAttackCounter());
				stmt.setInt(2, pgs.getPower());
				stmt.setInt(3, pgs.getHealth());
				stmt.setInt(4, pgs.getAgility());
				stmt.setInt(5, pgs.getAccuracy());
				stmt.setInt(6, pgs.getKnowledge());
				stmt.setInt(7, pgs.getWill());
				stmt.setInt(8, pgs.getMainHandAttack());
				stmt.setInt(9, pgs.getMainHandCritRate());
				stmt.setInt(10, pgs.getOffHandAttack());
				stmt.setInt(11, pgs.getOffHandCritRate());
				stmt.setInt(12, pgs.getWater());
				stmt.setInt(13, pgs.getWind());
				stmt.setInt(14, pgs.getEarth());
				stmt.setInt(15, pgs.getFire());
				stmt.setInt(16, pgs.getFlyTime());
				stmt.setInt(17, playerId);
				stmt.execute();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteStats(int playerId)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM `players_stats` WHERE `id`=?");
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
}
