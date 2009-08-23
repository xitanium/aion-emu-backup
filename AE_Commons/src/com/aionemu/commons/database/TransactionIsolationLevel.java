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

/**
 * Enum to use with {@link com.aionemu.commons.database.TransactionManager} instead of int's
 * 
 * @author SoulKeeper
 */
public enum TransactionIsolationLevel
{

	/**
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 */
	READ_COMMITED(Connection.TRANSACTION_READ_COMMITTED),

	/**
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 */
	READ_UNCOMMITED(Connection.TRANSACTION_READ_UNCOMMITTED),

	/**
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 */
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

	/**
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	/**
	 * Transaction integer level
	 */
	private int	i;

	/**
	 * Creates new transaction with given level
	 * 
	 * @param i
	 *            transaction level
	 */
	private TransactionIsolationLevel(int i)
	{
		this.i = i;
	}

	/**
	 * Return integer level that represents the transaction
	 * 
	 * @return integer transaction representation
	 */
	public int toInt()
	{
		return i;
	}
}
