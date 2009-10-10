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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Admin setlevel command
 *
 * @author IceReaper
 */
public class ResetStats extends AdminCommand
{

	public ResetStats()
	{
		super("resetstats", AdminLevel.ADMIN);
	}

	private void sendHelp (String message, Player admin) {
		PacketSendUtility.sendMessage(admin, message);
		PacketSendUtility.sendMessage(admin, "syntax //resetstats [player]");
		PacketSendUtility.sendMessage(admin, " - with player: name of the player (default to selected target, if not, to issuer)");
	}
	
	private void resetStatsOfPlayer (Player player, Player admin) {
		player.resetStats();
		PacketSendUtility.sendMessage(admin, "The stats of "+player.getName()+" have been reset");
		PacketSendUtility.sendMessage(player, admin.getName()+"reset your stats");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params == null)
		{
			sendHelp("Missing parameters...",admin);
			return;
		}

		Creature cre = admin.getTarget();
		if (cre==null) {
			if (params.length==1) {
				World world = admin.getActiveRegion().getWorld();
				Player player = world.findPlayer(params[0]);
				if (player == null) {
					PacketSendUtility.sendMessage(admin, "Player "+params[0]+" is not online");
				} else {
					resetStatsOfPlayer(player, admin);
				}
			} else {
				if (params.length==0) {
					admin.resetStats();
					PacketSendUtility.sendMessage(admin, "Your stats have been reset");
				} else {
					sendHelp("Too much parameters", admin);
				}
			}
		} else {
			if(cre instanceof Player)
			{
				Player player = (Player)cre;
				resetStatsOfPlayer(player, admin);
			} else {
				PacketSendUtility.sendMessage(admin, "Selected target "+cre.getName()+" is not a Player");
			}
		}
	}
}