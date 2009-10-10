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
package com.aionemu.gameserver;

import org.apache.log4j.Logger;

import java.util.Iterator;

import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This task is run, when server is shutting down. We should do here all data saving etc...
 * 
 * @author Luno
 * @modifier xitanium
 * 
 */
public class ShutdownHook implements Runnable
{
	private static final Logger	log	= Logger.getLogger(ShutdownHook.class);
	private World				world;
	private PlayerService		service;

	public ShutdownHook(World w, PlayerService service)
	{
		this.world = w;
		this.service = service;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		log.info("Starting AE GS shutdown sequence");
		
		for(int i = 0; i < 15; i++)
		{
			Iterator<Player> onlinePlayers = world.getPlayersIterator();
			if (!onlinePlayers.hasNext()) {
				break;
			}
			
			while(onlinePlayers.hasNext())
			{
				Player p = onlinePlayers.next();
				PacketSendUtility.sendMessage(p, "Server shutdown in " + (15 - i) + " seconds.");
				service.storePlayer(p);
			}
			
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				log.error("Can't sleep thread while running ShutdownHook", e);
			}
			
		}
		GameTimeManager.saveTime();
	}
}
