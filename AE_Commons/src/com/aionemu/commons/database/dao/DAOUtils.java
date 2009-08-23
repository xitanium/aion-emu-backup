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

package com.aionemu.commons.database.dao;

import com.aionemu.commons.database.DatabaseInfo;
import com.aionemu.commons.database.PersistentObject;
import com.aionemu.commons.database.dao.scriptloader.CachedClass;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.commons.utils.ClassUtils;

/**
 * DAO utility class
 * 
 * @author SoulKeeper
 */
public class DAOUtils
{

	/**
	 * Checks if DAO supports current DB
	 * 
	 * @param clazz
	 *            DAO class
	 * @param databaseInfo
	 *            Information about DB
	 * @return true if supports, false in other case
	 * @throws IllegalArgumentException
	 *             if DAO class is not marked with {@link com.aionemu.commons.database.dao.scriptloader.DAOInfo}
	 *             annotation
	 */
	public static boolean isDBSupportedByDAO(Class<? extends DAO> clazz, DatabaseInfo databaseInfo)
	{
		if (!clazz.isAnnotationPresent(DAOInfo.class))
		{
			throw new IllegalArgumentException("Class " + clazz.getName()
				+ " should be marked with @DAOInfo annotation");
		}

		DAOInfo info = clazz.getAnnotation(DAOInfo.class);
		SupportedDB[] databases = info.supportedDBs();

		for (SupportedDB db : databases)
		{
			if (!db.name().equals(databaseInfo.getProductName()))
			{
				continue;
			}

			if (!db.majorVersionComparator().compare(db.majorVersion(), databaseInfo.getMajorVersion()))
			{
				continue;
			}

			if (!db.minorVersionComparator().compare(db.minorVersion(), databaseInfo.getMinorVersion()))
			{
				continue;
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns base class for dao class.
	 * 
	 * @param clazz
	 *            class to check
	 * @return base class for dao
	 * @throws IllegalArgumentException
	 *             if argument is not marked with {@link DAOInfo} annotation
	 */
	public static Class<? extends DAO> getBaseClass(Class<? extends DAO> clazz)
	{
		if (!clazz.isAnnotationPresent(DAOInfo.class))
		{
			throw new IllegalArgumentException("Class " + clazz.getName()
				+ " should be marked with @DAOInfo annotation");
		}

		return clazz.getAnnotation(DAOInfo.class).baseClass();
	}

	/**
	 * Returns cache class for DAO class
	 * 
	 * @param clazz
	 *            class to check
	 * @return cache class
	 * @throws IllegalArgumentException
	 *             if argument is not marked with {@link com.aionemu.commons.database.dao.scriptloader.CachedClass}
	 *             annotation
	 */
	public static Class<? extends PersistentObject> getCacheClass(Class<? extends DAO> clazz)
	{
		if (!isCacheable(clazz))
		{
			throw new IllegalArgumentException("Class or it's sublcass/interface " + clazz.getName()
				+ " should be marked with @CachedClass annotation");
		}

		return ClassUtils.getAnnotationFromSubclassOrInterface(clazz, CachedClass.class).value();
	}

	/**
	 * Reutrns true if {@link com.aionemu.commons.database.dao.scriptloader.CachedClass} annotation is present
	 * 
	 * @param clazz
	 *            class to check
	 * @return true of dao is cacheable, false otherwise
	 */
	public static boolean isCacheable(Class<? extends DAO> clazz)
	{
		return ClassUtils.getAnnotationFromSubclassOrInterface(clazz, CachedClass.class) != null;
	}
}
