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
import java.sql.SQLException;

/**
 * Thread-based transaction manager.<br>
 * Transactions are associated with thread, should be used with care to avoid unclosed transactions
 * 
 * @author SoulKeeper
 */
public class TransactionManager
{

	/**
	 * Transaction holder
	 */
	private final ThreadLocal<Connection>	transactions	= new ThreadLocal<Connection>();

	/**
	 * Database factory that provides connections to transaction manager
	 */
	private final DatabaseFactory			databaseFactory;

	/**
	 * Isolation level that will be set to transaction after creation
	 */
	private final TransactionIsolationLevel	defaultIsolationLevel;

	/**
	 * Creates TransactionManager
	 * 
	 * @param databaseFactory
	 *            factory that will provide connections
	 */
	public TransactionManager(DatabaseFactory databaseFactory)
	{
		this(databaseFactory, TransactionIsolationLevel.READ_COMMITED);
	}

	/**
	 * Creates TransactionManager that will use
	 * {@link com.aionemu.commons.database.TransactionIsolationLevel#READ_COMMITED} as default transaction level.
	 * 
	 * @param databaseFactory
	 *            factory that will provide transactions
	 * @param level
	 *            Isolation level
	 */
	public TransactionManager(DatabaseFactory databaseFactory, TransactionIsolationLevel level)
	{
		this.databaseFactory = databaseFactory;
		this.defaultIsolationLevel = level;
	}

	/**
	 * Changes transaction isolation level.
	 * 
	 * @see Connection#setTransactionIsolation(int)
	 * @param level
	 *            new isolation level
	 * @throws SQLException
	 *             if something went wrong
	 */
	public void setTransactionIsolationLevel(TransactionIsolationLevel level) throws SQLException
	{
		Connection c = transactions.get();
		if (c == null)
		{
			throw new SQLException("Transaction not found");
		}

		c.setTransactionIsolation(level.toInt());
	}

	/**
	 * Starts new transaction
	 * 
	 * @return transaction
	 * @throws SQLException
	 *             if transaction is already active
	 */
	public Connection beginTransaction() throws SQLException
	{
		Connection c = transactions.get();
		if (c != null)
		{
			throw new SQLException("Transaction already started");
		}

		c = databaseFactory.getConnection();
		c.setAutoCommit(false);
		c.setTransactionIsolation(defaultIsolationLevel.toInt());
		transactions.set(c);
		return c;
	}

	/**
	 * Returns current transaction if in progress
	 * 
	 * @return current transaction if is in progress
	 * @throws SQLException
	 *             if there is no active transaction
	 */
	public Connection joinTransaction() throws SQLException
	{
		Connection c = transactions.get();
		if (c == null)
		{
			throw new SQLException("Transaction not found");
		}

		return c;
	}

	/**
	 * Commits and closes the transaction
	 * 
	 * @throws SQLException
	 *             if something went wrong or there is no active transaction
	 */
	public void commitAndCloseTransaction() throws SQLException
	{
		Connection c = transactions.get();
		if (c == null)
		{
			throw new SQLException("Transaction not found");
		}

		c.commit();
		c.close();
		transactions.remove();
	}
}
