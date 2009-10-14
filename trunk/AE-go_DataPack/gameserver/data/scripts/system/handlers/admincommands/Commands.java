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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.AdminLevel;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.utils.chathandlers.AdminCommandChatHandler;

public class Commands extends AdminCommand
{
	/**
	 * @param commandName
	 * 
	 * Display a list of available commands for player's account level.
	 */
	public Commands()
	{
		super("commands", AdminLevel.PLAYER);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		Iterator<AdminCommand> commands = AdminCommandChatHandler.getAllCommands().values().iterator();
		List<AdminCommand> availableCommands = new ArrayList<AdminCommand>();
		while(commands.hasNext()) 
		{
			AdminCommand cmd = commands.next();
			if(admin.getCommonData().getAdminLevel() >= cmd.getRequiredGMLevel().valueOf())
			{
				availableCommands.add(cmd);
			}
		}
		PacketSendUtility.sendMessage(admin, "Available commands at your level :");
		if(availableCommands.size() == 0) 
		{
			PacketSendUtility.sendMessage(admin, "--no command available--");
		}
		else
		{
			for(AdminCommand aCmd : availableCommands)
			{
				PacketSendUtility.sendMessage(admin, aCmd.getCommandName());
			}
		}
	}
}
