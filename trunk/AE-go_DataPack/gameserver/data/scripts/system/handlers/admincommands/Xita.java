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
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK76;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Xita extends AdminCommand
{
	@Inject
	private World	world;
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
	public void executeCommand(Player admin, String... params)
	{
		int methodId = Integer.parseInt(params[0]);
		switch(methodId) {
		
		case 1: playerTitlesSendPacket(admin);
		break;
		case 2: sendVideo(admin);
		break;
		default: return;
		
		}
	}
	
	public void playerTitlesSendPacket(Player xita) {
		PacketSendUtility.sendPacket(xita, new SM_PLAYER_TITLES(1));
	}
	public void sendVideo(Player xita) {
		PacketSendUtility.sendPacket(xita, new SM_PLAY_INTRO());
	}
}
