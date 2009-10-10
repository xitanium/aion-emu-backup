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
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.AionObject;

public class Go extends AdminCommand
{
	@Inject
	private World	world;
/**
	 * @param commandName
	 */
	public Go()
	{
		super("go", AdminLevel.ANIM);
	}
	
	private void sendHelp(Player admin, String subcommand) {
		PacketSendUtility.sendMessage(admin, "//go : Sends you to a specific location");
		if(subcommand.equals("creature")) {
			PacketSendUtility.sendMessage(admin, "//go creature <creature_id> : Go to creature location");
		}
		else {
			PacketSendUtility.sendMessage(admin, "Available subcommands : creature, object, coords");
		}
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		world = admin.getActiveRegion().getWorld();
		if(params.length == 0) {
			sendHelp(admin, "");
		}
		else {
			
			String subcommand = params[1].trim();
			
			if(subcommand.equals("creature")) {
				
				if(params.length != 2) {
					sendHelp(admin, subcommand);
				}
				else {
					try {
						int targetCreatureId = Integer.parseInt(params[1].trim());
						AionObject targetCreatureObj = world.findAionObject(targetCreatureId);
						if(targetCreatureObj instanceof Creature)  {
							Creature target = (Creature) targetCreatureObj;
							admin.getController().teleportTo(target.getActiveRegion().getMapId(), target.getX(), target.getY(), target.getZ(), target.getHeading());
							PacketSendUtility.sendMessage(admin, "Successfully teleported to creature #" + target.getObjectId());
						}
						else {
							PacketSendUtility.sendMessage(admin, "Error : the specified <creature_id> doesn't point to a valid creature");
						}
					}
					catch(NumberFormatException e) {
						PacketSendUtility.sendMessage(admin, "Error : you must specify a integer numeric value for <creature_id>");
					}
				}
			}
			else {
				sendHelp(admin, "");
			}
		}
	}
}
