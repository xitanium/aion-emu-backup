package com.aionemu.commons.services;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.xml.DOMConfigurator;

import com.aionemu.commons.log4j.JuliToLog4JHandler;
import com.aionemu.commons.log4j.ThrowableAsMessageAwareFactory;
import com.aionemu.commons.log4j.exceptions.Log4jInitializationError;

/**
 * This class represents simple wrapper for loggers that initializes logging system. <p/>
 * 
 * Default {@link org.apache.log4j.spi.LoggerFactory} can by configured by system property
 * {@value #LOGGER_FACTORY_CLASS_PROPERTY} <p/>
 * 
 * Default logger factory is {@link com.aionemu.commons.log4j.ThrowableAsMessageAwareFactory}
 * 
 * @author SoulKeeper
 */
public class LoggingService
{

	/**
	 * Property that represents {@link org.apache.log4j.spi.LoggerFactory} class
	 */
	public static final String	LOGGER_FACTORY_CLASS_PROPERTY	= "log4j.loggerfactory";

	/**
	 * Is Logging initialized or not?
	 */
	private static boolean		initialized;

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

		overrideDefaultLoggerFactory();

		// Initialize JULI to Log4J bridge
		Logger logger = LogManager.getLogManager().getLogger("");
		for (Handler h : logger.getHandlers())
		{
			logger.removeHandler(h);
		}
		logger.addHandler(new JuliToLog4JHandler());
	}

	/**
	 * This method uses some reflection to hack default log4j log facrory. <p/>
	 * 
	 * Log4j uses this Hierarchy for loggers that don't have exact name match and element categoryFactory for loggers
	 * with names that matches specified names in log4j.xml. <p/>
	 * 
	 * See log4j.xml for detailed description of Log4j behaviour.
	 */
	private static void overrideDefaultLoggerFactory()
	{
		// Hack here, we have to overwrite default logger factory
		Hierarchy lr = (Hierarchy) org.apache.log4j.LogManager.getLoggerRepository();
		try
		{
			Field field = lr.getClass().getDeclaredField("defaultFactory");
			field.setAccessible(true);
			String cn = System.getProperty(LOGGER_FACTORY_CLASS_PROPERTY, ThrowableAsMessageAwareFactory.class
				.getName());
			Class<?> c = Class.forName(cn);
			field.set(lr, c.newInstance());
			field.setAccessible(false);
		}
		catch (NoSuchFieldException e)
		{
			// never thrown
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// never thrown
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			throw new Log4jInitializationError("Can't found log4j logger factory class", e);
		}
		catch (InstantiationException e)
		{
			throw new Log4jInitializationError("Can't instantiate log4j logger factory", e);
		}
	}
}
