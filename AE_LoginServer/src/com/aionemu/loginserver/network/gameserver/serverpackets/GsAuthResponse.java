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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * @author -Nemesiss-
 */
public class GsAuthResponse extends GsServerPacket
{
	public static final int	RESPONSE_OK				= 0;
	public static final int	RESPONSE_WRONG_HEXID	= 1;

	private final int		response;

	public GsAuthResponse(int response)
	{
		this.response = response;
	}

	@Override
	protected void writeImpl(GsConnection con, ByteBuffer buf)
	{
		writeC(buf, 0x00);
		writeC(buf, response);
	}

	@Override
	public String getType()
	{
		return "0x00 GsAuthResponse";
	}
}
