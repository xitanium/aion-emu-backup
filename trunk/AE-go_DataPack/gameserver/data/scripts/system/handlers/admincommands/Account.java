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
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.utils.chathandlers.AdminCommandChatHandler;
import com.aionemu.gameserver.utils.chathandlers.ChatHandlers;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.utils.AccountUtils;

public class Account extends AdminCommand
{
	/**
	 * @param commandName
	 */
	public Account()
	{
		super("account", AdminLevel.PLAYER);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params.length == 0) 
		{
			PacketSendUtility.sendMessage(admin, "Your account access level is: " + admin.getCommonData().getAdminLevel());
		}
		else 
		{
			if(params[0].equals("password"))
			{
				if(params.length != 4) 
				{
					PacketSendUtility.sendMessage(admin, "Usage: //account password <old_password> <new_password> <new_password>");
				}
				else 
				{
					String oldPassword = "";
					String newPassword = "";
					if(!params[2].equals(params[3]))
					{
						PacketSendUtility.sendMessage(admin, "The new password and its confirmation are different.");
					}
					else
					{
						newPassword = params[2];
						String oldPasswordHash = AccountUtils.encodePassword(oldPassword);
						String newPasswordHash = AccountUtils.encodePassword(newPassword);
						//TODO: compare with actual password
						//TODO: apply password change
						//TODO: say user he has to relogin into game
						//TODO: send countdown and close game (send SM_ packet for game exit)
					}
				}
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "Usage: //account {password}");
			}
		}
	}
}
