/**
 * This file is part of aion-emu.
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
package aionemu.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author -Nemesiss-
 */
public class Config
{
	protected static final Logger	log				= Logger.getLogger(Config.class.getName());

	/** Login Server port */
	public static int				LOGIN_PORT;
	/** Login Server bind ip */
	public static String			LOGIN_BIND_ADDRESS;
	/** Number of trys of login before ban */
	public static int				LOGIN_TRY_BEFORE_BAN;
	/** Ban time */
	public static int				WRONG_LOGIN_BAN_TIME;
	/** Show NC Licence */
	public static boolean			SHOW_LICENCE	= true;
	/** Number of Threads that will handle io read (>= 0) */
	public static int				NIO_READ_THREADS;
	/** Number of Threads that will handle io write (>= 0) */
	public static int				NIO_WRITE_THREADS;

	// Database settings
	/** DB Host */
	public static String			DATABASE_HOST;
	/** DB Port */
	public static int				DATABASE_PORT;
	/** DB URL */
	public static String			DATABASE_DRIVER;
	/** DB Login Alias */
	public static String			DATABASE_LOGIN_USER;
	/** DB Login Pass */
	public static String			DATABASE_LOGIN_PASS;
	/** DB Alias */
	public static String			DATABASE_NAME;
	/** DB Max Connections */
	public static int				DATABASE_MAX_CON;

	
	public static boolean			ACCOUNT_AUTO_CREATION = true;
	public static void load()
	{
		log.info("Loading loginserver.properties");
		try
		{
			Properties settings = new Properties();
			InputStream is = new FileInputStream(new File("./config/loginserver.properties"));
			settings.load(is);
			is.close();

			LOGIN_PORT = Integer.parseInt(settings.getProperty("LoginPort", "2106"));
			LOGIN_BIND_ADDRESS = settings.getProperty("LoginserverHostname", "*");
			LOGIN_TRY_BEFORE_BAN = Integer.parseInt(settings.getProperty("LoginTryBeforeBan", "10"));
			WRONG_LOGIN_BAN_TIME = Integer.parseInt(settings.getProperty("WrongLoginBanTime", "15"));
			NIO_READ_THREADS = Integer.parseInt(settings.getProperty("NioReadThreads", "0"));
			NIO_WRITE_THREADS = Integer.parseInt(settings.getProperty("NioWriteThreads", "0"));

			settings.clear();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error while loading loginserver.properties " + e, e);
			throw new Error("loginserver.cfg not loaded!");
		}

		try
		{
			Properties settings = new Properties();
			log.info("Loading database.properties");
			InputStream is = new FileInputStream(new File("./config/database.properties"));
			settings.load(is);

			DATABASE_HOST = settings.getProperty("DatabaseHost", "127.0.0.1");
			DATABASE_PORT = Integer.parseInt(settings.getProperty("DatabasePort", "3306"));
			DATABASE_DRIVER = settings.getProperty("DatabaseDriver", "com.mysql.jdbc.Driver");
			DATABASE_NAME = settings.getProperty("DatabaseName", "db_name");
			DATABASE_LOGIN_USER = settings.getProperty("DatabaseUser", "root");
			DATABASE_LOGIN_PASS = settings.getProperty("DatabasePass", "pass");
			DATABASE_MAX_CON = Integer.parseInt(settings.getProperty("DatabaseMaxConnections", "10"));

			settings.clear();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error while loading database.properties " + e, e);
			throw new Error("database.properties not loaded!");
		}
	}
}
