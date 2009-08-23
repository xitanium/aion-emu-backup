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

/**
 * InvocationHandler that is used to build dynamic Proxies
 * 
 * @author SoulKeeper
 */
public class DefaultCachingMethodHandler extends AbstractCachingMethodHandler
{
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(DefaultCachingMethodHandler.class);

	/**
	 * Creates new instance
	 * 
	 * @param cacheManager
	 *            cacheManager that is used to cache objects
	 */
	public DefaultCachingMethodHandler(CacheManager cacheManager)
	{
		super(cacheManager);
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
			proceed.invoke(self, args);
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
				result = proceed.invoke(self, args);
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
		else if (thisMethod.getName().equals("remove"))
		{
			proceed.invoke(self, args);
			Class<? extends PersistentObject> poClazz = DAOUtils.getCacheClass((Class<? extends DAO>) self.getClass()
				.getSuperclass());
			cacheManager.remove(poClazz, args[0]);
			return null;
		}

		throw new RuntimeException("This exception should be never thrown");
	}
}
