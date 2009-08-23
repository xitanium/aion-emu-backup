/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.utils.collections.cachemap;

/**
 * @author Luno
 * 
 */
public class CacheMapFactory
{
	/**
	 * Cache map config instance
	 */
	private final CacheMapConfig	config;

	/**
	 * Instantiation of CacheMapFactory
	 * 
	 * @param config
	 *            CacheMapConfig instance
	 */
	public CacheMapFactory(CacheMapConfig config)
	{
		this.config = config;
	}

	/**
	 * Returns new instance of either {@link WeakCacheMap} or {@link SoftCacheMap} depending on
	 * {@link CacheMapConfig#type} setting.
	 * 
	 * @param <K>
	 *            - Type of keys
	 * @param <V>
	 *            - Type of values
	 * 
	 * @param cacheName
	 *            - The name for this cache map
	 * @param valueName
	 *            - Mnemonic name for values stored in the cache
	 * @return instance of Soft or Weah CacheMap
	 */
	public <K, V> CacheMap<K, V> createCacheMap(String cacheName, String valueName)
	{
		switch (config.getType())
		{
			case SOFT:
				return createSoftCacheMap(cacheName, valueName);
			case WEAK:
				return createWeakCacheMap(cacheName, valueName);
			default:
				throw new UnsupportedOperationException("Unknwon cache type");
		}
	}

	/**
	 * Creates and returns an instance of {@link SoftCacheMap}
	 * 
	 * @param <K>
	 *            - Type of keys
	 * @param <V>
	 *            - Type of values
	 * 
	 * @param cacheName
	 *            - The name for this cache map
	 * @param valueName
	 *            - Mnemonic name for values stored in the cache
	 * @return new instance of {@link com.aionemu.commons.utils.collections.cachemap.SoftCacheMap}
	 */
	public static <K, V> CacheMap<K, V> createSoftCacheMap(String cacheName, String valueName)
	{
		return new SoftCacheMap<K, V>(cacheName, valueName);
	}

	/**
	 * Creates and returns an instance of {@link WeakCacheMap}
	 * 
	 * @param <K>
	 *            - Type of keys
	 * @param <V>
	 *            - Type of values
	 * 
	 * @param cacheName
	 *            - The name for this cache map
	 * @param valueName
	 *            - Mnemonic name for values stored in the cache
	 * @return new instance of {@link WeakCacheMap}
	 */
	public static <K, V> CacheMap<K, V> createWeakCacheMap(String cacheName, String valueName)
	{
		return new WeakCacheMap<K, V>(cacheName, valueName);
	}
}
