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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author xavier
 *
 */
public class List extends AdminCommand
{

	/**
	 * @param commandName
	 */
	public List()
	{
		super("list");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		Iterator<Player> iter = admin.getActiveRegion().getWorld().getPlayersIterator();
		int count = 0;
		PacketSendUtility.sendMessage(admin, "Generation de la liste des joueurs en ligne...");
		while (iter.hasNext()) {
			PacketSendUtility.sendMessage(admin, "Le joueur "+iter.next().getName()+" est en ligne");
			count++;
		}
		PacketSendUtility.sendMessage(admin, "Il y a "+count+" joueurs en ligne");
	}

}
