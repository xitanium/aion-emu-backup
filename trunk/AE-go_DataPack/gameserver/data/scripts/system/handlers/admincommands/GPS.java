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
 *
 * @author Xitanium
 *
 */

package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class GPS extends AdminCommand
{
	@Inject
	private World	world;
/**
	 * @param commandName
	 */
	public GPS()
	{
		super("gps", 0);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String... params)
	{
		world = admin.getActiveRegion().getWorld();
		PacketSendUtility.sendMessage(admin, "Actual location GPS coordinates :");
		PacketSendUtility.sendMessage(admin, "x				= " + admin.getX());
		PacketSendUtility.sendMessage(admin, "y				= " + admin.getY());
		PacketSendUtility.sendMessage(admin, "z 				= " + admin.getZ());
		PacketSendUtility.sendMessage(admin, "heading		= " + admin.getHeading());
		PacketSendUtility.sendMessage(admin, "map 			= " + admin.getActiveRegion().getMapId());
	}
}
