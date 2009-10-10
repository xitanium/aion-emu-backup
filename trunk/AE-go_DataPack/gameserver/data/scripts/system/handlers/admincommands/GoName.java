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

package admincommands;

import com.aionemu.gameserver.model.AdminLevel;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Admin movetoplayer command.
 *
 * @author Tanelorn
 */
public class GoName extends AdminCommand
{
	@Inject
	private World	world;

	/**
	 * Constructor.
	 */
	public GoName()
	{
		super("goname", AdminLevel.ANIM);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //goname <player> : Teleports yourself to the specified player");
			return;
		}

		Player player = world.findPlayer(params[0]);
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		if (player == admin)
		{
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}

		admin.getController().teleportTo(player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading());

		PacketSendUtility.sendMessage(admin, "Teleported to player " + player.getName() + ".");
		PacketSendUtility.sendMessage(player, "Game Master " + admin.getName() + " appears at your location");
	}
}
