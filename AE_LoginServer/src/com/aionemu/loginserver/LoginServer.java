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
package com.aionemu.loginserver;

import java.security.GeneralSecurityException;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.services.LoggingService;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.network.IOServer;
import com.aionemu.loginserver.utils.DeadLockDetector;
import com.aionemu.loginserver.utils.ThreadPoolManager;
import com.aionemu.loginserver.controller.BannedIpController;

/**
 * @author -Nemesiss-
 */
public class LoginServer
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(LoginServer.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LoggingService.init();
		/** initializing DB Factory */
		DatabaseFactory.init();
		Config.load();
		/** Start deadlock detector that will restart server if deadlock happened */
		new DeadLockDetector(60, DeadLockDetector.RESTART).start();
		ThreadPoolManager.getInstance();

		try
		{
			LoginController.load();
		}
		catch (GeneralSecurityException e)
		{
			log.fatal("Failed initializing LoginController. Reason: " + e.getMessage(), e);
			System.exit(1);
		}

		GameServerTable.load();
        BannedIpController.load();
		// TODO! flood protector
		// TODO! brute force protector

		IOServer.getInstance();
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

		long freeMem = (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime()
			.freeMemory()) / (1024 * 1024);
		long totalMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		log.info("LoginServer Started, used memory " + (totalMem - freeMem) + " MB");
	}
}
