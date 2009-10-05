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

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.utils.chathandlers.GMCommand;
import com.google.inject.Inject;

/**
 * This task is run, when server is shutting down.
 * We should do here all data saving etc. 
 * 
 * @author Luno
 *
 */
public class ShutdownHook implements Runnable
{
	private static final Logger log = Logger.getLogger(ShutdownHook.class);
	private World world;
	private Player admin;
	
	/**
	 * @param instance
	 */
	public ShutdownHook(World instance)
	{
		// TODO Auto-generated constructor stub
		this.world = instance;
	}
	/**
	 * 
	 * @param instance
	 * @param sender
	 */
	public ShutdownHook(World instance, Player sender) 
	{
		this.world = instance;
		this.admin = sender;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		int i=0;
		log.info("Starting AE GS shutdown sequence");
		for (i=0;i<=10;i++) {
			Iterator<Player> iter = world.getPlayersIterator();
			while (iter.hasNext()) {
				Player p = iter.next();
				if (i<10) {
					PacketSendUtility.sendMessage(p, "Server shutdown in "+(10-i)+" seconds...");
				} else {
					p.getClientConnection().close(false);
				}
			}
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				if(this.admin != null) 
				{
					PacketSendUtility.sendMessage(this.admin, "InterruptedException thrown");
				}
			}		
		}
		GameTimeManager.saveTime();
	}
}
