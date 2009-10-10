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

import com.aionemu.gameserver.model.AdminLevel;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;

public class Modify extends AdminCommand
{
	/**
	 * @param commandName
	 */
	public Modify()
	{
		super("mod", AdminLevel.ANIM);
	}
	
	private void sendHelp(Player admin, String subcommand) {
		PacketSendUtility.sendMessage(admin, "//mod : Modifies a player variable according to parameters");
		if(subcommand.equals("money")) {
			PacketSendUtility.sendMessage(admin, "//mod money <new_kinah> : Changes target's kinah amount by <new_kinah> (can be a negative value to remove kinah from target)");
		}
		else {
			PacketSendUtility.sendMessage(admin, "Available subcommands : money");
		}
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params.length == 0) {
			sendHelp(admin, "");
		}
		else {
			String subcommand = params[0].trim();
			if(subcommand.equals("")) {
				sendHelp(admin, "");
			}
			else if(subcommand.equals("money")) 
			{
				if(params.length != 2) {
					sendHelp(admin, "money");
				}
				else {
					try {
						int newKinah = Integer.parseInt(params[1]);
						if(!(admin.getTarget() instanceof Player)) {
							PacketSendUtility.sendMessage(admin, "Error : you must target a player, nothing else");
						}
						else {
							Player target = (Player) admin.getTarget();
							Inventory targetKinah = new Inventory();
							targetKinah.getKinahFromDb(target.getObjectId());
							int actualKinah = targetKinah.getKinahCount();
							int totalKinah = actualKinah + newKinah;
							targetKinah.putKinahToDb(target.getObjectId(), totalKinah);
							PacketSendUtility.sendPacket(target, new SM_INVENTORY_UPDATE(0, 182400001, totalKinah));
						}
					}
					catch(NumberFormatException e) {
						PacketSendUtility.sendMessage(admin, "Error : you must specify a integer numeric value for <new_kinah>");
					}
					
				}
			}
			else 
			{
				sendHelp(admin, "");
			}
			
		}		
	}
}
