package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

public abstract class AdminCommandsDAO implements DAO {
	
	public final String getClassName() {
		return AdminCommandsDAO.class.getName();
	}
	
	public abstract boolean isExistingTeleport(String name);
	
	public abstract boolean saveTeleport(String name, int mapId, float x, float y, float z, byte h);
	
	public abstract boolean deleteTeleport(String name);
	
	public abstract String loadAllTeleportsList();
	
	public abstract float[] loadTeleport(String name);
	
}