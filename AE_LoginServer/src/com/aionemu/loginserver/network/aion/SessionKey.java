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
package com.aionemu.loginserver.network.aion;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.commons.utils.Rnd;

/**
 * @author -Nemesiss-
 */
public class SessionKey
{
	/**
	 * play1 ok key
	 */
	public int	playOkID1;
	/**
	 * play2 ok key
	 */
	public int	playOkID2;
	/**
	 * login1 ok key
	 */
	public int	loginOkID1;
	/**
	 * login2 ok key
	 */
	public int	loginOkID2;

	/**
	 * Create new SesionKey
	 */
	public SessionKey()
	{
		this.loginOkID1 = Rnd.nextInt();
		this.loginOkID2 = Rnd.nextInt();
		this.playOkID1 = Rnd.nextInt();
		this.playOkID2 = Rnd.nextInt();
	}

	/**
	 * Create new SesionKey with given values.
	 * @param loginOk1
	 * @param loginOk2
	 * @param playOk1
	 * @param playOk2
	 */
	public SessionKey(int loginOk1, int loginOk2, int playOk1, int playOk2)
	{
		this.loginOkID1 = loginOk1;
		this.loginOkID2 = loginOk2;
		this.playOkID1 = playOk1;
		this.playOkID2 = playOk2;
	}

	/**
	 * Check if given values are ok.
	 * @param loginOk1
	 * @param loginOk2
	 * @return true if loginOk1 and loginOk2 match this SessionKey
	 */
	public boolean checkLogin(int loginOk1, int loginOk2)
	{
		return loginOkID1 == loginOk1 && loginOkID2 == loginOk2;
	}

	/**
	 * Check if this SessionKey have the same values.
	 * @param key
	 * @return true if key match this SessionKey.
	 */
	public boolean checkSessionKey(SessionKey key)
	{
		if (Config.SHOW_LICENCE)
		{
			return (playOkID1 == key.playOkID1 && loginOkID1 == key.loginOkID1 && playOkID2 == key.playOkID2 && loginOkID2 == key.loginOkID2);
		}
		else
		{
			return (playOkID1 == key.playOkID1 && playOkID2 == key.playOkID2);
		}
	}
}