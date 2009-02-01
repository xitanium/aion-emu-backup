/*
 * This file is part of aion-emu.
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

package com.aionemu.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is designed to simplify routine job with properties
 * 
 * @author SoulKeeper
 */
public class PropertiesUtils
{

	/**
	 * Loads properties by given file
	 * 
	 * @param file
	 *            filename
	 * @return loaded properties
	 * @throws java.io.IOException
	 *             if can't load file
	 */
	public static Properties load(String file) throws IOException
	{
		return load(new File(file));
	}

	/**
	 * Loads properties by given file
	 * 
	 * @param file
	 *            filename
	 * @return loaded properties
	 * @throws java.io.IOException
	 *             if can't load file
	 */
	public static Properties load(File file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		Properties p = new Properties();
		p.load(fis);
		fis.close();
		return p;
	}
}
