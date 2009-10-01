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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author xavier
 *
 */
public class RevokeMJ extends AdminCommand
{

	private World world;
	/**
	 * @param commandName
	 */
	@Inject
	public RevokeMJ(World world)
	{
		super("revokemj");
		this.world = world;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (params == null || params.length > 1) {
			PacketSendUtility.sendMessage(admin, "syntax //revokemj [player]");
			return;
		}
		if ((params.length==0)||(params[0].trim().length()==0)) {
			if (admin.getTarget()==null) {
				PacketSendUtility.sendMessage(admin, "You haven't selected a player, use \"//revokemj player\" instead");
				return;
			}
		}
		Player player = null;
		if ((admin.getTarget()!=null)&&(admin.getTarget() instanceof Player)) {
			player = (Player)admin.getTarget();
		} else {
			player = world.findPlayer(params[0].replace("*MJ* ", ""));
		}
		if (player != null) {
			world.despawn(player);
			player.getCommonData().setAdmin(false);
			player.setProtectionActive(true);
			PacketSendUtility.sendPacket(player, new SM_UNKF5(player));
			PacketSendUtility.sendMessage(admin, player.getName()+" is no more MJ ");
			PacketSendUtility.sendMessage(player, "[MJ] ("+admin.getName()+") revoke your MJ status");
		} else {
			PacketSendUtility.sendMessage(admin, "Cannot find player "+params[0]);
		}

	}

}
