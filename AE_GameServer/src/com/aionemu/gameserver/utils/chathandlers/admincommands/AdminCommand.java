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
 */

package com.aionemu.gameserver.utils.chathandlers.admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This is a base class representing admin command. Admin command is sent by player by typing a //command parameters in
 * chat
 * 
 * @author Luno
 * 
 */
public abstract class AdminCommand
{
	private String	commandName;

	protected AdminCommand(String commandName)
	{
		this.commandName = commandName;
	}

	/**
	 * This value says, how many params the command has.<br>
	 * What does it mean: when admin types //admin param0 param1 param2, then
	 * {@link #executeCommand(Player, String[])} is called with an array of params.<br>
	 * With default value of getSplitSize (-1) it'll be an array containing (in this case) 3 params
	 * ("param0","param1","param2").<br>
	 * But if we want to have only 2 params ("param0" - 1st param , "param1 param2" - 2nd param) we must override
	 * getSplitSize() method in subclass, and make it returning 2.
	 * 
	 * @return
	 */
	public int getSplitSize()
	{
		return -1;
	}

	/**
	 * Returns the name of the command handled by this class.
	 * 
	 * @return command name
	 */
	public String getCommandName()
	{
		return commandName;
	}

	/**
	 * Execute admin command represented by this class, with a given list of parametrs.
	 * 
	 * @param admin
	 * @param params
	 */
	public abstract void executeCommand(Player admin, String... params);
}
