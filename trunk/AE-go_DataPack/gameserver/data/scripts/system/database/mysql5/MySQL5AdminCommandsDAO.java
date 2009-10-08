package mysql5;

import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql5.MySQL5DAOUtils;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.dao.AdminCommandsDAO;

public class MySQL5AdminCommandsDAO extends AdminCommandsDAO {
	
	private static final Logger log = Logger.getLogger(MySQL5AdminCommandsDAO.class);
	
	public final float[] loadTeleport(String name) {
		PreparedStatement s = DB.prepareStatement("SELECT teleport_map, teleport_x, teleport_y, teleport_z, teleport_h FROM teleports WHERE teleport_name = ?");
		float[] result = new float[5];
		try {
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			result[0] = (float) rs.getInt("teleport_map");
			result[1] = rs.getFloat("teleport_x");
			result[2] = rs.getFloat("teleport_y");
			result[3] = rs.getFloat("teleport_z");
			result[4] = rs.getFloat("teleport_h");
		}
		catch(SQLException e) {
			result = null;
			log.error("Cannot get teleport coords", e);
		}
		finally {
			DB.close(s);
		}
		return result;
	}
	
	public final boolean saveTeleport(String name, int mapId, float x, float y, float z, byte h) {
		PreparedStatement s = DB.prepareStatement("INSERT INTO teleports(teleport_name, teleport_map, teleport_x, teleport_y, teleport_z, teleport_h) VALUES (?, ?, ?, ?, ?, ?)");
		try {
				s.setString(1, name);
				s.setInt(2, mapId);
				s.setFloat(3, x);
				s.setFloat(4, y);
				s.setFloat(5, z);
				s.setFloat(6, (float) h);
				s.execute();
				return true;
			}
			catch(SQLException e) {
				log.error("Cannot save teleport coords", e);
				return false;
			}
			finally {
				DB.close(s);
			}
	}
	
	public final boolean deleteTeleport(String name) {
		PreparedStatement s = DB.prepareStatement("REMOVE FROM teleports WHERE teleport_name = ?");
		try {
				s.setString(1, name);
				s.execute();
				return true;
			}
			catch(SQLException e) {
				log.error("Cannot delete teleport :" + name, e);
				return false;
			}
			finally {
				DB.close(s);
			}
	}
	
	public final boolean isExistingTeleport(String name) {
		PreparedStatement s = DB.prepareStatement("SELECT count(teleport_id) as tps FROM teleports WHERE teleport_name = ?");
		try {
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("tps") > 0;
		}
		catch(SQLException e)
		{
			log.error("Can't check if teleport name is in use, returning positive response", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
	
}