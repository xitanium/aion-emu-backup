/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * @author SoulKeeper
 */
public class DatabaseFactory
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(DatabaseFactory.class);

	/**
	 * Database config link
	 */
	private DatabaseConfig		databaseConfig;

	/**
	 * Database datasource
	 */
	private DataSource			dataSource;

	/**
	 * Connection pool
	 */
	private GenericObjectPool	genericObjectPool;

	/**
	 * Database information
	 */
	private DatabaseInfo		databaseInfo;

	/**
	 * Constructor
	 * 
	 * @param config
	 *            database config
	 */
	public DatabaseFactory(DatabaseConfig config)
	{
		this(config, DatabaseFactory.class.getClassLoader());
	}

	/**
	 * Constructor, can be used if in some weird case driver uses another classloader than default one
	 * 
	 * @param config
	 *            database config
	 * @param classLoader
	 *            driver classloader
	 */
	public DatabaseFactory(DatabaseConfig config, ClassLoader classLoader)
	{
		this.databaseConfig = config;
		Driver driver;
		try
		{
			driver = (Driver) Class.forName(config.getDriver(), true, classLoader).newInstance();
		}
		catch (Exception e)
		{
			log.error("Can't inialize driver class", e);
			throw new RuntimeException(e);
		}

		init(driver);
	}

	/**
	 * Initializes database factory
	 * 
	 * @param driver
	 *            Driver that will be used
	 */
	protected void init(Driver driver)
	{
		Properties props = new Properties();
		props.setProperty("user", databaseConfig.getUser());
		props.setProperty("password", databaseConfig.getPassword());

		ConnectionFactory cf = new DriverConnectionFactory(driver, databaseConfig.getUrl(), props);

		genericObjectPool = new GenericObjectPool();
		genericObjectPool.setMaxActive(databaseConfig.getMaxConnections());
		genericObjectPool.setMinIdle(databaseConfig.getMinConnections());

		new PoolableConnectionFactory(cf, genericObjectPool, null, null, false, true);

		dataSource = new PoolingDataSource(genericObjectPool);

		Connection c = null;
		try
		{
			c = getConnection();
			DatabaseMetaData dmd = c.getMetaData();
			databaseInfo = new DatabaseInfo(dmd.getDatabaseProductName(), dmd.getDatabaseMajorVersion(), dmd
				.getDatabaseMinorVersion());
		}
		catch (SQLException e)
		{
			throw new RuntimeException("Can't initialize DatabaseFactory", e);
		}
		finally
		{
			if (c != null)
			{
				try
				{
					c.close();
				}
				catch (SQLException e)
				{
					log.error("Can't close SQL connection", e);
				}
			}
		}
	}

	/**
	 * Creates connection
	 * 
	 * @return Connection object
	 * @throws SQLException
	 *             if something went wrong
	 */
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	/**
	 * Destroys database factory object, connection pool and datasource.<br>
	 * Destroying destroyed database factory makes no effect
	 */
	public void destroy()
	{
		synchronized (this)
		{
			if (dataSource == null)
			{
				return;
			}

			log.info("Destroying datasource...");

			try
			{
				genericObjectPool.close();
				genericObjectPool.clear();
			}
			catch (Exception e)
			{
				log.error("Can't close generic object pool");
				throw new RuntimeException(e);
			}
			dataSource = null;
		}
	}

	/**
	 * Returns database information
	 * 
	 * @return database information
	 */
	public DatabaseInfo getDatabaseInfo()
	{
		return databaseInfo;
	}

	/**
	 * 
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable
	{
		if (dataSource != null)
		{
			log.warn("DatabseFactory waas garbage collected, but not manually destroyed. Forcing destruction");
			destroy();
		}
		super.finalize();
	}
}
