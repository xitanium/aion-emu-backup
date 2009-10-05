package com.aionemu.gameserver.utils.chathandlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class GMCommandChatHandler implements ChatHandler
{
	private static final Logger			log			= Logger.getLogger(AdminCommandChatHandler.class);

	private Map<String, GMCommand>	commands	= new HashMap<String, GMCommand>();

	GMCommandChatHandler()
	{

	}

	void registerGMCommand(GMCommand command)
	{
		if(command == null)
			throw new NullPointerException("Command instance cannot be null");

		String commandName = command.getCommandName();

		GMCommand old = commands.put(commandName, command);
		if(old != null)
		{
			log.warn("Overriding handler for command " + commandName + " from " + old.getClass().getName() + " to "
				+ command.getClass().getName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatHandlerResponse handleChatMessage(ChatType chatType, String message, Player sender)
	{
		if(!message.startsWith("//"))
		{
			return new ChatHandlerResponse(false, message);
		}
		else
		{
			//TODO: implement switch on gmlevel
			if(!sender.getCommonData().isAdmin())
			{
				PacketSendUtility.sendMessage(sender, "<You do not have permission to use GM commands>");
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}

			String[] commandAndParams = message.split(" ", 2);

			String command = commandAndParams[0].substring(2);
			GMCommand admc = commands.get(command);
			if(admc == null)
			{
				PacketSendUtility.sendMessage(sender, "<There is no such GM command: " + command + ">");
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}

			String[] params = new String[] {};
			if(commandAndParams.length > 1)
				params = commandAndParams[1].split(" ", admc.getSplitSize());

			admc.executeCommand(sender, params);
			return ChatHandlerResponse.BLOCKED_MESSAGE;
		}
	}

	/**
	 * Clear all registered handlers (before reload). 
	 */
	void clearHandlers()
	{
		this.commands.clear();
	}

	/**
	 * Returns count of available admin command handlers.
	 * @return count of available admin command handlers.
	 */
	public int getSize()
	{
		return this.commands.size();
	}
}