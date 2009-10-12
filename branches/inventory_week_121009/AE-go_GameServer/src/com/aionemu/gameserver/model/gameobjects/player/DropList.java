/**
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;

/**
 *
 * @author Metos
 */
public class DropList {
	
	private static final DropList instance = new DropList();
	
	//not finish sorry, need optimize
	private int [][] fullDL;
	private int maxDL;
	
	public DropList () { //full load of droplist
		try {
			PreparedStatement ps = DB.prepareStatement("SELECT COUNT(*) AS nb FROM droplist");
			ResultSet prs = ps.executeQuery();
			prs.first();
			maxDL = prs.getInt("nb");
			
			fullDL = new int[maxDL][5];
			int nb = 0;
			
			ResultSet rs = ps.executeQuery("SELECT * FROM droplist");
			while (rs.next()) {
				fullDL[nb][0] = rs.getInt("mobId");
				fullDL[nb][1] = rs.getInt("itemId");
				fullDL[nb][2] = rs.getInt("min");
				fullDL[nb][3] = rs.getInt("max");
				fullDL[nb][4] = rs.getInt("chance");
				nb++;
			}
			DB.close(ps);
			//log.info("ListDrop Loaded ("+maxDL+")");
			System.out.println("ListDrop Loaded ("+maxDL+")"); //need use log system
		}
		catch (SQLException e) {
			Logger.getLogger(DropList.class).error("Error loading droplist", e);
		}
	}
	
	public int [][] getLootTable (int mobId) {
		int nb = 0;
		for (int i = 0; i < maxDL; i++) {
			if (fullDL[i][0] == mobId) {
				nb++;
			}
		}
		
		int [][] temp = new int[nb][4];
		nb = 0;
		for (int i = 0; i < maxDL; i++) {
			if (fullDL[i][0] == mobId) {
				temp[nb][0] = fullDL[i][1];
				temp[nb][1] = fullDL[i][2];
				temp[nb][2] = fullDL[i][3];
				temp[nb][3] = fullDL[i][4];
				nb++;
			}
		}
		return temp;
	}
	
	public static DropList getInstance()
	{
		return instance;
	}
}