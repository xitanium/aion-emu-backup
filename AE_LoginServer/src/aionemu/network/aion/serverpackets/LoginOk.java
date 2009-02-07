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
package aionemu.network.aion.serverpackets;

import aionemu.network.aion.AionServerPacket;
import aionemu.network.aion.SessionKey;

/**
 * @author -Nemesiss-
 */
public class LoginOk extends AionServerPacket
{
	private final int	loginOk1;
	private final int	loginOk2;

	public LoginOk(SessionKey key)
	{
		this.loginOk1 = key.loginOkID1;
		this.loginOk2 = key.loginOkID2;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x03);
		writeD(loginOk1);
		writeD(loginOk2);
		writeD(0x00);
		writeD(0x00);
		writeD(0x000003ea);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeB(new byte[16]);
	}

	@Override
	public String getType()
	{
		return "0x03 LoginOk";
	}
}
