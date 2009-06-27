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

import javax.crypto.SecretKey;

/**
 * Static blowfish key.
 * 
 * @author -Nemesiss-
 * 
 */
public class StaticBlowfishKey implements SecretKey
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/**
	 * Static blowfish key for aion client/server packet crypt.
	 */
	private static final byte[]	staticBlowfishKey	= { (byte) 0x6b, (byte) 0x60, (byte) 0xcb, (byte) 0x5b,
		(byte) 0x82, (byte) 0xce, (byte) 0x90, (byte) 0xb1, (byte) 0xcc, (byte) 0x2b, (byte) 0x6c, (byte) 0x55,
		(byte) 0x6c, (byte) 0x6c, (byte) 0x6c, (byte) 0x6c };

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlgorithm()
	{
		return "Blowfish";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getEncoded()
	{
		return staticBlowfishKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormat()
	{
		return "RAW";
	}
}
