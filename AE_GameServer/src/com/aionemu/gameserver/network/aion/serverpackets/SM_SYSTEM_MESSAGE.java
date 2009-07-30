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

package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;

/**
 * System message packet.
 * 
 * @author -Nemesiss-
 * @author EvilSpirit
 */
public class SM_SYSTEM_MESSAGE extends AionServerPacket
{
	/**
	 * Player duration time, called by %DURATIONTIMENOSEC ingame command
	 */
	public static final int	DURATION_TIME				= 0x13D8EF;

	/**
	 * player's accumulated ingame time
	 */
	public static final int	ACCUMULATED_TIME			= 0x15363D;

	/**
	 * Sent when someone whispers a player which is offline at the moment
	 */
	public static final int	WHISPERED_PLAYER_OFFLINE	= 0x13D893;

	/**
	 * /loc ingame command response
	 */
	public static final int	LOC							= 0x038296;

	private final String[]	params;
	private final int		code;

	/**
	 * Constructs new <tt>SM_SYSTEM_MESSAGE </tt> packet
	 * 
	 * @param code
	 *            operation code, take it from SM_SYSTEM_MESSAGE public static values
	 * @param params
	 */
	public SM_SYSTEM_MESSAGE(int code, String... params)
	{
		super(Version.Chiness ? 0x2A : 0x1A);

		this.code = code;
		this.params = params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeOP(buf, getOpcode());
		writeH(buf, 0x13); // unk
		writeD(buf, 0x00); // unk
		writeD(buf, code); // msg id
		writeC(buf, params.length); // count

		for(String param : params)
		{
			writeS(buf, param);
		}
	}
}
