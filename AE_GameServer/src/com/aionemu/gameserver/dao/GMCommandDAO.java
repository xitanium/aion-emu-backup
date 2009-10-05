package com.aionemu.gameserver.dao;

import java.sql.Timestamp;
import com.aionemu.commons.database.dao.DAO;

public abstract class GMCommandDAO implements DAO {
	
	public abstract int getRequiredLevel(String command);
	public abstract boolean commandExists(String command);
	
	public final String getClassName() {
		return GMCommandDAO.class.getName();		
	}
	
}