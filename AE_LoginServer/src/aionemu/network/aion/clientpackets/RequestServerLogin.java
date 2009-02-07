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
package aionemu.network.aion.clientpackets;

import java.nio.ByteBuffer;

import aionemu.network.aion.AionClientPacket;
import aionemu.network.aion.AionConnection;
import aionemu.network.aion.SessionKey;
import aionemu.network.aion.serverpackets.PlayOk;

/**
 * @author -Nemesiss-
 */
public class RequestServerLogin extends AionClientPacket
{
	private final int	loginOk1;
	private final int	loginOk2;
	private final int	servId;

	public RequestServerLogin(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		loginOk1 = readD();
		loginOk2 = readD();
		servId = readD();
	}

	@Override
	protected void runImpl()
	{
		SessionKey key = getConnection().getSessionKey();
		if (key.checkLogin(loginOk1, loginOk2))
		{
			// TODO! if ok
			sendPacket(new PlayOk(key));
			// TODO! else dc
		}
		// TODO! dc
	}

	@Override
	public String getType()
	{
		return "0x02 RequestServerLogin";
	}
}
