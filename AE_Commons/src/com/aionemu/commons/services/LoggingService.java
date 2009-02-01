package com.aionemu.commons.services;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.log4j.xml.DOMConfigurator;

import com.aionemu.commons.log4j.JuliToLog4JHandler;
import com.aionemu.commons.log4j.exceptions.Log4jInitializationError;

/**
 * This class represents simple wrapper for loggers that initializes logging
 * system.
 * 
 * @author SoulKeeper
 */
public class LoggingService
{

	private static boolean	initialized;

	/**
	 * Actually initializes logging system.
	 * 
	 * @throws com.aionemu.commons.log4j.exceptions.Log4jInitializationError
	 *             if can't initialize logging
	 */
	public static void init() throws Log4jInitializationError
	{

		synchronized (LoggingService.class)
		{
			if (initialized)
			{
				return;
			}
			else
			{
				initialized = true;
			}
		}

		File f = new File("config/log4j.xml");

		if (!f.exists())
		{
			throw new Log4jInitializationError("Missing file " + f.getPath());
		}

		try
		{
			DOMConfigurator.configure(f.getPath());
		}
		catch (Exception e)
		{
			throw new Log4jInitializationError("Can't initialize logging", e);
		}

		// Initialize JULI to Log4J bridge
		Logger logger = LogManager.getLogManager().getLogger("");
		for (Handler h : logger.getHandlers())
		{
			logger.removeHandler(h);
		}
		logger.addHandler(new JuliToLog4JHandler());
	}
}
