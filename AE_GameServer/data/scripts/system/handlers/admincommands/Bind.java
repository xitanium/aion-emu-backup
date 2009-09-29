/*
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

import java.util.Iterator;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.inject.Inject;

/**
 * @author xavier
 *
 */
public class Bind extends AdminCommand
{

	private World world;
	/**
	 * @param commandName
	 */
	@Inject
	public Bind(World world)
	{
		super("bind");
		this.world = world;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(admin, "syntax //bind [all|player]");
			return;
		}
		WorldPosition worldPosition = admin.getPosition();
		float x = worldPosition.getX();
		float y = worldPosition.getY();
		float z = worldPosition.getZ();
		int worldId = worldPosition.getMapId();
		if (params[0].equalsIgnoreCase("all")) {
			Iterator<Player> iter = admin.getActiveRegion().getWorld().getPlayersIterator();
			while (iter.hasNext()) {
				Player player = iter.next();
				if (player.getObjectId()!=admin.getObjectId()) {
					PacketSendUtility.sendMessage(player, "[MJ] ("+admin.getName()+") will bind you !");
					world.despawn(player);
					world.setPosition(player, worldId, x, y, z, admin.getHeading());
					player.setProtectionActive(true);
					PacketSendUtility.sendPacket(player, new SM_UNKF5(player));
				}
			}
			PacketSendUtility.sendMessage(admin, "Binded all active players");
		} else {
			Player player = world.findPlayer(params[0]);
			if (player != null) {
				world.despawn(player);
				world.setPosition(player, worldId, x, y, z, admin.getHeading());
				player.setProtectionActive(true);
				PacketSendUtility.sendPacket(player, new SM_UNKF5(player));
				PacketSendUtility.sendMessage(admin, "Binded "+player.getName()+" here ");
				PacketSendUtility.sendMessage(player, "[MJ] ("+admin.getName()+") bind you");
			} else {
				PacketSendUtility.sendMessage(admin, "Cannot bind "+params[0]);
			}
		}

	}

}
