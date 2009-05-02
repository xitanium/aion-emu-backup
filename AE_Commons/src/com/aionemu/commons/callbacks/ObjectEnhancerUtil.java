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

package com.aionemu.commons.callbacks;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class with helper methods for object enhancing
 * 
 * @author SoulKeeper
 */
public class ObjectEnhancerUtil
{

	private static final Logger	log						= Logger.getLogger(ObjectEnhancerUtil.class);

	/**
	 * Suffix for enhanced class
	 */
	public static final String	ENHANCED_CLASS_SUFFIX	= "_ENHANCED_";

	/**
	 * Returns true if can enhance class. Abstract, final classes and Interfaces can't be enhanced.<br>
	 * Classes without public constructors can't be enhanced
	 * 
	 * @param clazz
	 *            class to check
	 * @return true if can enhance class
	 */
	public static boolean isEnhantable(Class clazz)
	{

		if (clazz.isInterface())
		{
			return false;
		}

		if (clazz.isEnum())
		{
			return false;
		}

		int modifiers = clazz.getModifiers();

		// noinspection SimplifiableIfStatement
		if (Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers))
		{
			return false;
		}

		return clazz.getConstructors().length != 0;
	}

	/**
	 * Returns name that class will have after enhancing
	 * 
	 * @param clazz
	 *            class to get name from
	 * @return class name after enhancing
	 */
	public static String getEnhancedName(Class clazz)
	{
		return clazz.getName() + ENHANCED_CLASS_SUFFIX;
	}

	/**
	 * Returns list of enhancable methods in class
	 * 
	 * @param clazz
	 *            class to get enhancable methods
	 * @return lisf of enhancable methods
	 */
	public static List<Method> getEnhancableMethods(Class clazz)
	{
		Method[] methods = clazz.getMethods();
		List<Method> result = new ArrayList<Method>();

		for (Method m : methods)
		{
			int modifiers = m.getModifiers();

			if (!(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)))
			{
				continue;
			}

			if (m.isAnnotationPresent(Enhancable.class))
			{
				result.add(m);
			}
		}

		return result;
	}
}
