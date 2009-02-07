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
package com.aionemu.loginserver.network.aion;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.utils.Rnd;

/**
 * @author -Nemesiss-
 */
public class SessionKey
{
	public int	playOkID1;
	public int	playOkID2;
	public int	loginOkID1;
	public int	loginOkID2;

	public SessionKey()
	{
		this.loginOkID1 = Rnd.nextInt();
		this.loginOkID2 = Rnd.nextInt();
		this.playOkID1 = Rnd.nextInt();
		this.playOkID2 = Rnd.nextInt();
	}

	public SessionKey(int loginOk1, int loginOk2, int playOk1, int playOk2)
	{
		this.loginOkID1 = loginOk1;
		this.loginOkID2 = loginOk2;
		this.playOkID1 = playOk1;
		this.playOkID2 = playOk2;
	}

	public boolean checkLogin(int loginOk1, int loginOk2)
	{
		return loginOkID1 == loginOk1 && loginOkID2 == loginOk2;
	}

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