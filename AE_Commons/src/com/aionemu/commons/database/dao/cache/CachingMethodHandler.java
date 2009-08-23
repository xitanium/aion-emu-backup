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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.PersistentObject;
import com.aionemu.commons.database.dao.DAO;
import com.aionemu.commons.database.dao.DAOUtils;

import javassist.util.proxy.MethodHandler;

/**
 * InvocationHandler that is used to build dynamic Proxies
 * 
 * @author SoulKeeper
 */
public class CachingMethodHandler implements MethodHandler
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CachingMethodHandler.class);

	/**
	 * CacheManager that is used to store caches
	 */
	private final CacheManager	cacheManager;

	/**
	 * Creates new instance
	 * 
	 * @param cacheManager
	 *            cacheManager that is used to cache objects
	 */
	public CachingMethodHandler(CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( { "unchecked" })
	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
	{
		if (thisMethod.getName().equals("save"))
		{
			PersistentObject po = (PersistentObject) args[0];
			((DAO) self).save(po);
			cacheManager.save(po);
			return null;
		}
		else if (thisMethod.getName().equals("load"))
		{
			Object primaryKey = args[0];
			Class<? extends PersistentObject> poClazz = DAOUtils.getCacheClass((Class<? extends DAO>) self.getClass()
				.getSuperclass());
			Object result = cacheManager.get(poClazz, primaryKey);
			if (result == null)
			{
				result = ((DAO) self).load(primaryKey);
				if (result != null)
				{
					cacheManager.save((PersistentObject<?>) result);
				}
				else
				{
					log.warn("Reuquest for unexisting object instance of class " + poClazz + " with id: " + primaryKey);
				}
			}
			return result;
		}

		throw new RuntimeException("This exception should be never thrown");
	}
}
