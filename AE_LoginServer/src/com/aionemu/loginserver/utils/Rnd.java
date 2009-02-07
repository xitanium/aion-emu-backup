/**
 * This file is part of aion-emu.
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
package com.aionemu.loginserver.utils;

/**
 * $
 * 
 * @author Balancer
 */
public class Rnd
{
	private static MTRandom	_rnd	= new MTRandom();

	public static final float get() // get random number from 0 to 1
	{
		return _rnd.nextFloat();
	}

	/**
	 * Gets a random number from 0(inclusive) to n(exclusive)
	 * 
	 * @param n
	 *            The superior limit (exclusive)
	 * @return A number from 0 to n-1
	 */
	public static final int get(int n)
	{
		return (int) Math.floor(_rnd.nextDouble() * n);
	}

	public static final int get(int min, int max) // get random number from
	// min to max (not max-1 !)
	{
		return min + (int) Math.floor(_rnd.nextDouble() * (max - min + 1));
	}

	public static final int nextInt(int n)
	{
		return (int) Math.floor(_rnd.nextDouble() * n);
	}

	public static final int nextInt()
	{
		return _rnd.nextInt();
	}

	public static final double nextDouble()
	{
		return _rnd.nextDouble();
	}

	public static final double nextGaussian()
	{
		return _rnd.nextGaussian();
	}

	public static final boolean nextBoolean()
	{
		return _rnd.nextBoolean();
	}
}
