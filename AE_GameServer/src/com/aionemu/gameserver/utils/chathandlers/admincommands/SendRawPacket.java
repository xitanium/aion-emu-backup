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
package com.aionemu.gameserver.utils.chathandlers.admincommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Luno
 *
 */
public class SendRawPacket extends AdminCommand
{

	/**
	 * @param commandName
	 */
	public SendRawPacket()
	{
		super("raw");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@SuppressWarnings("null")
	@Override
	public void executeCommand(Player admin, String... params)
	{
		try
		{
			Scanner sc = new Scanner(new File("data/packets/"+params[0]+".txt"));
			
			SM_CUSTOM_PACKET packet = null;
			
			int i = 0;
			while(sc.hasNextLine())
			{
				String row = sc.nextLine().substring(0,48).trim();
				for(String st: row.split(" "))
				{
					if(i == 0)
					{
						int id = Integer.decode("0x"+st);
						packet = new SM_CUSTOM_PACKET(id);
					}
					else if(i > 2)
					{
						packet.addElement(PacketElementType.C, "0x"+st);
					}
					i++;
				}
			}
			
			if(packet != null)
				PacketSendUtility.sendPacket(admin, packet);
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
