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
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_TITLES;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_INTRO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK50;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF0;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Xita extends AdminCommand
{
	/**
	 * @param commandName
	 */
	public Xita()
	{
		super("xita", AdminLevel.ADMIN);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params[0].equals("titles")) 
		{		
			playerTitlesSendPacket(admin);
		}
		else if(params[0].equals("video")) 
		{ 
			sendVideo(admin, Integer.parseInt(params[1]));
		}
		else if(params[0].equals("sys_1370000")) 
		{
			sendSys1370000(admin,Integer.parseInt(params[1],16), Integer.parseInt(params[2],16));
		}
		else if(params[0].equals("f0")) {
			PacketSendUtility.sendPacket(admin, new SM_UNKF0());
		}
		else if(params[0].equals("50")) {
			PacketSendUtility.sendPacket(admin, new SM_UNK50());
		}
		else 
		{
			PacketSendUtility.sendMessage(admin, "//xita titles");
			PacketSendUtility.sendMessage(admin, "//xita video #factionid #videoid");
		}
	}
	
	public void playerTitlesSendPacket(Player xita) {
		PacketSendUtility.sendPacket(xita, new SM_PLAYER_TITLES(1));
	}
	public void sendVideo(Player xita, int videoid) {
		PacketSendUtility.sendPacket(xita, new SM_PLAY_INTRO(videoid));
	}
	public void sendSys1370000(Player xita, int _1, int _2) {
		PacketSendUtility.sendPacket(xita, SM_SYSTEM_MESSAGE.UNKNOWN_1370000(_1, _2));
	}
}