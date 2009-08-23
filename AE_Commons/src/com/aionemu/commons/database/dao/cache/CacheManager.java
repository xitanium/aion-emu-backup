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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.PersistentObject;
import com.aionemu.commons.utils.collections.cachemap.CacheMap;
import com.aionemu.commons.utils.collections.cachemap.CacheMapFactory;

/**
 * @author SoulKeeper
 */
public class CacheManager
{

	/**
	 * Logger
	 */
	private static final Logger																	log	= Logger
																										.getLogger(CacheManager.class);

	/**
	 * Cache storage
	 */
	private final Map<Class<? extends PersistentObject>, CacheMap<Object, PersistentObject>>	cache;

	/**
	 * Factory to create cache classes
	 */
	private final CacheMapFactory																cacheMapFactory;

	/**
	 * CacheManager constructor
	 * 
	 * @param cacheMapFactory
	 *            cacheMapFactory to create CaheInstances
	 */
	public CacheManager(CacheMapFactory cacheMapFactory)
	{
		this.cacheMapFactory = cacheMapFactory;
		cache = new ConcurrentHashMap<Class<? extends PersistentObject>, CacheMap<Object, PersistentObject>>();
	}

	/**
	 * Saves object in cache.
	 * 
	 * @param obj
	 *            object to store
	 * @throws DuplicatedCacheObjectException
	 *             if another object with same id is already cached
	 */
	public synchronized void save(PersistentObject<?> obj) throws DuplicatedCacheObjectException
	{
		CacheMap<Object, PersistentObject> directCache = cache.get(obj.getClass());

		if (directCache == null)
		{
			log.debug("Creating cache for class " + obj.getClass().getName());

			directCache = cacheMapFactory.createCacheMap(obj.getId().getClass().getName(), obj.getClass().getName());
			cache.put(obj.getClass(), directCache);
		}

		if (directCache.contains(obj.getId()))
		{
			PersistentObject anotherObject = directCache.get(obj);

			// reference check to make sure that objects are the same
			if (anotherObject != obj)
			{
				throw new DuplicatedCacheObjectException(obj.getId(), obj.getClass());
			}
		}
		else
		{
			directCache.put(obj.getId(), obj);
		}
	}

	/**
	 * Returns cached instance of class or null if not found
	 * 
	 * @param clazz
	 *            Class of cached object
	 * @param id
	 *            identifier of cached object
	 * @return chached instance or null
	 */
	@SuppressWarnings( { "unchecked" })
	public PersistentObject get(Class<? extends PersistentObject> clazz, Object id)
	{
		CacheMap directCache = cache.get(clazz);
		if (directCache == null)
		{
			log.debug("Can't find cache for class " + clazz.getName());
			return null;
		}

		PersistentObject res = (PersistentObject) directCache.get(id);
		if (res == null)
		{
			log.debug("Can't find cached object of class " + clazz.getName() + " with id " + id);
			return null;
		}

		return res;
	}
}
