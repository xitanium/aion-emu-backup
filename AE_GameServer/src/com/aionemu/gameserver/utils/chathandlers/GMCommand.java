package com.aionemu.gameserver.utils.chathandlers;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public abstract class GMCommand {
	
	private final String commandName;
	
	protected GMCommand(String commandName) {
		this.commandName = commandName;		
	}
	
	public int getSplitSize()
	{
		return -1;
	}
	
	public String getCommandName()
	{
		return commandName;
	}
	
	public abstract void executeCommand(Player admin, String[] params);
	
}