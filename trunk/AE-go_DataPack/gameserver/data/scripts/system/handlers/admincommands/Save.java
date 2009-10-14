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

import java.util.Iterator;

import com.aionemu.gameserver.model.AdminLevel;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Save extends AdminCommand
{
	/**
	 * @param commandName
	 */
	private PlayerService service;
	
	public Save()
	{
		super("save", AdminLevel.PLAYER);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params.length == 0)
		{
			service.storePlayer(admin);
			PacketSendUtility.sendMessage(admin, "Character saved");
		}
		else 
		{
			if(admin.getCommonData().getAdminLevel() >= 2) 
			{
				PacketSendUtility.sendMessage(admin, "Please wait, saving characters ...");
				Iterator<Player> players = admin.getActiveRegion().getWorld().getPlayersIterator();
				while(players.hasNext()) 
				{
					service = new PlayerService(null,null,null,null,null);
					service.storePlayer(players.next());
					try 
					{
						Thread.sleep(10);
					}
					catch(InterruptedException e) 
					{
					}
				}
				PacketSendUtility.sendMessage(admin, "Characters successfully saved.");
			}
		}
	}
}
