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

package com.aionemu.commons.database.dao.cache;

import com.aionemu.commons.database.dao.DAO;

/**
 * Basic Interface for cacheable DAO<br>.
 * Cache implementation should implement this and DAO child that is going to be cached.
 *
 * @author SoulKeeper
 */
public interface Cache<T extends DAO> {

	/**
	 * Returns DAO instance that is used by cahce
	 * @return DAO instance
	 */
	public T getDAO();

	/**
	 * Sets DAO instance that cahe will use
	 * @param daoInstance DAO isntance
	 */
	public void setDAO(T daoInstance);
}
