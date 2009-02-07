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
public class PlayOk extends AionServerPacket
{
	private final int	playOk1;
	private final int	playOk2;

	public PlayOk(SessionKey key)
	{
		this.playOk1 = key.playOkID1;
		this.playOk2 = key.playOkID2;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x07);
		writeD(playOk1);
		writeD(playOk2);
	}

	@Override
	public String getType()
	{
		return "0x07 PlayOk";
	}
}
