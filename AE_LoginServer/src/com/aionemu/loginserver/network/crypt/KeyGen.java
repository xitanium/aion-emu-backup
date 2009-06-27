/**
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
package com.aionemu.loginserver.network.crypt;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

/**
 * Key generator. It will generate keys for Blowfish, RSA etc.
 * 
 * @author -Nemesiss-
 * 
 */
public class KeyGen
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log				= Logger.getLogger(KeyGen.class);
	/**
	 * Key generator for blowfish
	 */
	private static KeyGenerator		blowfishKeyGen;

	/**
	 * Initialize Key Generator.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	static void init() throws NoSuchAlgorithmException
	{
		log.info("Initializing Key Generator...");
		blowfishKeyGen = KeyGenerator.getInstance("Blowfish");
	}

	/**
	 * Generate and return blowfish key
	 * 
	 * @return Random generated blowfish key
	 */
	public static SecretKey generateBlowfishKey()
	{
		return blowfishKeyGen.generateKey();
	}
}
