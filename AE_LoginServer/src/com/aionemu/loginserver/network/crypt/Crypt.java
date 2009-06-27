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

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import com.aionemu.loginserver.LoginController;

/**
 * Crypt for encrypting/decrypting aion server/client packets.
 * 
 * @author -Nemesiss-
 * 
 */
public class Crypt
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log				= Logger.getLogger(Crypt.class);
	/**
	 * Static Encryption for 1st server packet.
	 */
	private static Cipher	staticEncrypt;
	/**
	 * Blowfish for decrypting
	 */
	private final Cipher	decrypt;
	/**
	 * Blowfish for encrypting
	 */
	private final Cipher	encrypt;
	/**
	 * Indicate if first packet was send ie if static or normal encrypt should be used.
	 */
	private boolean			staticEncryption	= true;

	/**
	 * Constructor that accept public blowfish key that will be used for packet encryption/decryption.
	 * 
	 * @param blowfishKey
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	public Crypt(Key blowfishKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		/**
		 * Initialize blowfish for decryption.
		 */
		decrypt = Cipher.getInstance("Blowfish");
		decrypt.init(Cipher.DECRYPT_MODE, blowfishKey);

		/**
		 * Initialize blowfish for encryption - with static key.
		 */
		encrypt = Cipher.getInstance("Blowfish");
		encrypt.init(Cipher.ENCRYPT_MODE, blowfishKey);
	}

	//TODO!
	public boolean decrypt()
	{
		return true;
	}

	//TODO!
	public boolean encrypt()
	{
		if (staticEncryption)
		{

			staticEncryption = false;
		}
		return true;
	}

	/**
	 * Initialize Crypt, key generator etc.
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public static void init() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		if (staticEncrypt == null)
		{
			log.info("Initializing Crypt...");
			staticEncrypt = Cipher.getInstance("Blowfish");
			staticEncrypt.init(Cipher.ENCRYPT_MODE, new StaticBlowfishKey());
			KeyGen.init();
		}
		else
		{
			throw new IllegalStateException("Crypt can only be initialized a single time.");
		}
	}
}
