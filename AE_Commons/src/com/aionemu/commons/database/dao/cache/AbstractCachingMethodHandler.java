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

import javassist.util.proxy.MethodHandler;

/**
 * Basic class for cahing method handler.<br>
 * All subclasses that will be used must have same constructor, be non-abstract, and public
 * 
 * @author SoulKeeper
 */
public abstract class AbstractCachingMethodHandler implements MethodHandler
{

	/**
	 * CacheManager that is used to store caches
	 */
	protected final CacheManager	cacheManager;

	/**
	 * Creates new instance
	 * 
	 * @param cacheManager
	 *            cacheManager that is used to cache objects
	 */
	public AbstractCachingMethodHandler(CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable;
}
