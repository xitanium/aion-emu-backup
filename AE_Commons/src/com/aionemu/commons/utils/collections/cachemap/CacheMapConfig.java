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

package com.aionemu.commons.utils.collections.cachemap;

import java.util.Properties;

import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.configuration.ConfigurableProcessor;

/**
 * Configurator for {@link com.aionemu.commons.utils.collections.cachemap.CacheMap}
 * 
 * @author SoulKeeper
 */
public class CacheMapConfig
{

	/**
	 * What type of chachemap shoudl be used
	 */
	@Property(key = "cachemap.type", defaultValue = "SOFT")
	public CacheMapType	type;

	/**
	 * Creates new CacheMap using given properties
	 * 
	 * @param props
	 *            properties to use
	 */
	public CacheMapConfig(Properties props)
	{
		ConfigurableProcessor.process(this, props);
	}

	/**
	 * Returns what type of cahcemap should be used
	 * 
	 * @return type of cachemap that should be used
	 */
	public CacheMapType getType()
	{
		return type;
	}

	/**
	 * Sets a cachemap that should be used
	 * 
	 * @param type
	 *            cachemap type
	 */
	public void setType(CacheMapType type)
	{
		this.type = type;
	}
}
