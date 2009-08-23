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

import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.commons.database.dao.DAOUtils;

/**
 * Generic class for managing caches
 *
 * @author SoulKeeper
 */
public class CacheManager {

	/**
	 * Cache holder
	 */
	private final Map<Class<? extends DAO>, Cache> cache = new HashMap<Class<? extends DAO>, Cache>();

	/**
	 * Returns cache instance of DAO class
	 * @param daoClass dao class
	 * @return cahce instance
	 */
	public Cache getCache(Class<? extends DAO> daoClass) {
		Class<? extends DAO> baseClass = DAOUtils.getBaseClass(daoClass);
		Cache c = cache.get(baseClass);
		if (c == null) {
			try {
				Class<? extends Cache> cacheClass = DAOUtils.getCacheClass(daoClass);
				c = cacheClass.newInstance();
				cache.put(baseClass, c);
			} catch (Exception e) {
				throw new CacheCreationException(daoClass, e);
			}
		}

		return c;
	}

	/**
	 * Wraps Cache isntance with DAO class. Cahce should also implement DAO
	 * @param daoInstance DAO intance
	 * @param <T> DAO type
	 * @return Chache class that implements daoInstance and wraps it
	 */
	@SuppressWarnings({"unchecked"})
	public <T extends DAO> T wrap(T daoInstance) {
		DAO result;
		Cache c = null;
		try {
			c = getCache(daoInstance.getClass());
			c.setDAO(daoInstance);
			result = (DAO) c;
		}
		catch (Exception e) {
			throw new CacheWrappingException(daoInstance.getClass().getName(), c != null ? c.getClass().getName() : "none", e);
		}

		return (T) result;
	}

	/**
	 * Remowes DAO instance from Cache instance
	 * @param daoClass Cache of that class should be cleared from DAO
	 */
	@SuppressWarnings({"unchecked"})
	public void removeWrapping(Class<? extends DAO> daoClass) {
		Cache c = getCache(DAOUtils.getBaseClass(daoClass));
		c.setDAO(null);
	}
}