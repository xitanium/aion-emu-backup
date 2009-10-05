package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dao.GMCommandDAO;

public class MySQL5GMCommandDAO extends GMCommandDAO {
	
	public int getRequiredLevel(final String command) {
		PreparedStatement s = DB.prepareStatement("SELECT required_level as lvl FROM GMCommand WHERE command = ?");
		int result = 10;
		try {
			s.setString(1, command);
			ResultSet rs = s.executeQuery();
			rs.next();
			result = rs.getInt("lvl");
		}
		catch(SQLException e)
		{
		}
		finally
		{
			DB.close(s);
		}
		return result;
	}
	
	public boolean commandExists(final String command) {
		PreparedStatement s = DB.prepareStatement("SELECT COUNT(required_level) FROM GMCommand WHERE command = ?");
		boolean result = false;
		try {
			s.setString(1, command);
			ResultSet rs = s.executeQuery();
			rs.next();
			int dbResult = rs.getInt(0);
			if(dbResult == 1) {
				result = true;
			}
		}
		catch(SQLException e)
		{
			result = false;
		}
		finally
		{
			DB.close(s);
		}
		return result;
	}
	
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
	
}