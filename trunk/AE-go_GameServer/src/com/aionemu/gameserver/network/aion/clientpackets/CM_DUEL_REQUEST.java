/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * @author alexa026
 * 
 */
public class CM_DUEL_REQUEST extends AionClientPacket
{
	/**
	* Target object id that client wants to start duel with
	*/
	private int	objectId;
	@Inject
	private World world;
	
	private static Logger		log			= Logger.getLogger(CM_DUEL_REQUEST.class);

	/**
	* Constructs new instance of <tt>CM_DUAL_REQUEST</tt> packet
	* @param opcode
	*/
	public CM_DUEL_REQUEST(int opcode)
	{
		super(opcode);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void readImpl()
	{
		objectId = readD();

	}

	@Override
	protected void runImpl()
	{
		// Get the request sender
		final Player activePlayer = getConnection().getActivePlayer();
		// Get the request recipient
		final Player targetPlayer = world.findPlayer(objectId);
		
		log.debug("Player " + activePlayer.getName() + " (objid=" + activePlayer.getObjectId() + ") requested duel with " + targetPlayer.getName() + " (objid=" + targetPlayer.getObjectId()+")");

		RequestResponseHandler activePlayerResponseHandler = new RequestResponseHandler(activePlayer) {
			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				requester.getController().startDuelWith(responder);
			}

			public void denyRequest(Player requester, Player responder)
			{
				requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(responder.getName()));
				PacketSendUtility.sendMessage(requester, "Player " + responder.getName() + " declined your Duel request.");
			}
		};
		
		RequestResponseHandler targetPlayerResponseHandler = new RequestResponseHandler(targetPlayer) {
			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				requester.getController().startDuelWith(responder);
			}

			public void denyRequest(Player requester, Player responder)
			{
				requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(responder.getName()));
				PacketSendUtility.sendMessage(requester, "Player " + responder.getName() + " declined your Duel request.");
			}
		};

		
		boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL,targetPlayerResponseHandler);
		if (!requested){
			// Can't trade with player.
			// TODO: Need to check why and send a error.
		}
		else {
			targetPlayer.getClientConnection().sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, activePlayer.getName()));
		}
		requested = activePlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL,activePlayerResponseHandler);
		if (!requested){
			// Can't trade with player.
			// TODO: Need to check why and send a error.
		}
		else {
			activePlayer.getClientConnection().sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, targetPlayer.getName()));
		}
	}
}
