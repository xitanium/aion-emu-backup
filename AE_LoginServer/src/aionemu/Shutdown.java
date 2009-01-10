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
package aionemu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import aionemu.account.BanIpList;
import aionemu.network.IOServer;
import aionemu.utils.ThreadPoolManager;
import aionemu_commons.database.DatabaseFactory;
import aionemu_commons.network.nio.NioServer;

/**
 * @author -Nemesiss-
 */
public class Shutdown extends Thread
{
	private static final Logger			log			= Logger.getLogger(Shutdown.class.getName());
	private static Shutdown				instance	= new Shutdown();
	private static BufferedOutputStream	shutdownLog;

	public Shutdown()
	{
		File log = new File("./log/shutdown.log");
		try
		{
			log.createNewFile();
			shutdownLog = new BufferedOutputStream(new FileOutputStream(log, true));
		}
		catch (Exception e)
		{
		};
	}

	/**
	 * get the shutdown-hook instance the shutdown-hook instance is created by
	 * the first call of this function, but it has to be registrered externaly.
	 * 
	 * @return instance of Shutdown, to be used as shutdown hook
	 */
	public static Shutdown getInstance()
	{
		return instance;
	}

	/**
	 * this function is called, when a new thread starts if this thread is the
	 * thread of getInstance, then this is the shutdown hook and we save all
	 * data and disconnect all clients. after this thread ends, the server will
	 * completely exit if this is not the thread of getInstance, then this is a
	 * countdown thread. we start the countdown, and when we finished it, and it
	 * was not aborted, we tell the shutdown-hook why we call exit, and then
	 * call exit when the exit status of the server is 1, startServer.sh /
	 * startServer.bat will restart the server.
	 */
	@Override public void run()
	{
		SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM H:mm:ss");
		log("[" + dateFmt.format(new Date(System.currentTimeMillis())) + "]");

		/* saaving ban ip list */
		try
		{
			logln("saving ban ips...");
			BanIpList.store();
			log(" ban ips saved.");
		}
		catch (Throwable t)
		{
		}

		/* Disconnecting all the clients */
		try
		{
			logln("Interrupting NioServer:");
			IOServer.getInstance().shutdown();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		/* Shuting down DB connections */
		try
		{
			logln("Closing SQL connections...");
			DatabaseFactory.getInstance().shutdown();
			log(" SQL connections closed.");
		}
		catch (Throwable t)
		{
		}

		/* Shuting down threadpools */
		try
		{
			logln("Shuting down ThreadPoolManager...");
			ThreadPoolManager.getInstance().shutdown();
		}
		catch (Throwable t)
		{
		}
		log("\n\n");
	}

	public static void logln(String msg)
	{
		log("\n" + msg, null);
	}

	public static void log(String msg)
	{
		log(msg, null);
	}

	public static void log(String msg, Throwable t)
	{
		try
		{
			if (t != null)
			{
				shutdownLog.write(("\n" + msg).getBytes());
				t.printStackTrace(new PrintWriter(shutdownLog));
				shutdownLog.write("\n".getBytes());
			}
			else
				shutdownLog.write(msg.getBytes());
			shutdownLog.flush();
		}
		catch (Exception e)
		{
		}
		if (t != null)
		{
			System.out.print("\n" + msg);
			t.printStackTrace();
		}
		else
			System.out.print(msg);

	}
}
