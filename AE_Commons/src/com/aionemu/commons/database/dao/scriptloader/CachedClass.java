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

package com.aionemu.commons.database.dao.scriptloader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

import com.aionemu.commons.database.PersistentObject;

/**
 * Marker annotation for DAO's that should be cached.<br>
 * Please not that this annotation can be placed directly on class, on parent class or on interface.<br>
 * Also plase note that only the following methods are affected by cache:<br>
 * <ul>
 * <li>{@link com.aionemu.commons.database.dao.DAO#load(Object)}</li>
 * <li>{@link com.aionemu.commons.database.dao.DAO#save(com.aionemu.commons.database.PersistentObject)}</li>
 * </ul>
 * 
 * @author SoulKeeper
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CachedClass
{

	/**
	 * Class that works as DAO cache
	 * 
	 * @return class that works as DAO cache
	 */
	Class<? extends PersistentObject> value();
}
