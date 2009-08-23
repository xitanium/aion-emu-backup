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

import com.aionemu.commons.database.dao.DAO;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.MethodFilter;

/**
 * @author SoulKeeper
 */
public class CacheProxyBuilder
{

	/**
	 * Method that build proxy.
	 * 
	 * @param superClass
	 *            Proxy superclass
	 * @param cacheManager
	 *            cachemanager that should be used
	 * @return DAO instance with caching abilities
	 */
	@SuppressWarnings( { "unchecked" })
	public static <T extends DAO> T buildProxy(Class<T> superClass, CacheManager cacheManager)
	{

		// TODO: check if we need to disabe caching in ProxyFactory
		ProxyFactory pf = new ProxyFactory();
		pf.setSuperclass(superClass);
		pf.setInterfaces(new Class[] { CacheProxy.class });
		pf.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method m)
			{
				return (m.getName().equals("save") || m.getName().equals("load"));
			}
		});

		pf.setHandler(new CachingMethodHandler(cacheManager));

		try
		{
			return (T) pf.createClass().newInstance();
		}
		catch (Exception e)
		{
			throw new CacheCreationException(superClass, e);
		}
	}
}
