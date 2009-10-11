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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Online extends AdminCommand
{
	/**
	 * @param commandName
	 */
	public Online()
	{
		super("online", AdminLevel.ANIM);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params.length != 1) {
			PacketSendUtility.sendMessage(admin, "Usage : //online [count | list ]");
		}
		else {
			
			if(params[0].equals("count")) 
			{
				Iterator<Player> players = admin.getActiveRegion().getWorld().getPlayersIterator();
				int playersCount = 0;
				while(players.hasNext()) 
				{
					players.next();
					playersCount++;
				}
				PacketSendUtility.sendMessage(admin, "Currently online players : " + playersCount);
			}
			else if(params[0].equals("list")) 
			{
				Iterator<Player> players = admin.getActiveRegion().getWorld().getPlayersIterator();
				StringBuilder playersList = new StringBuilder();
				playersList.append("Currently online players : \n");
				while(players.hasNext()) 
				{
					playersList.append(players.next().getName());
					playersList.append(" | ");
				}
				playersList.append("--END LIST--");
				PacketSendUtility.sendMessage(admin, playersList.toString());
			}
			else 
			{
				PacketSendUtility.sendMessage(admin, "Usage : //online [count | list ]");
			}
		}
	}
}
