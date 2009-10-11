package com.aionemu.gameserver.model.gameevents;

import javax.print.attribute.standard.Finishings;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameevents.threading.DuelThread;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.clientpackets.CM_DUEL_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL_STARTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;



public class Duel {
	
	private static Logger	log = Logger.getLogger(Duel.class);
	private Player requester;
	private Player responder;
	private World currentWorld;
	private Player duelWinner;
	private Player duelLooser;
	
	public Duel(Player _requester, Player _responder) {
		
		requester = _requester;
		responder = _responder;
		currentWorld = _requester.getActiveRegion().getWorld();
		log.info("Player " + requester.getName() + " requested duel with " + responder.getName());
	}
	
	public  void start() {
		sendDuelChallenge();
	}
	
	public void setDuelResults(Player winner, Player looser) {
		duelWinner = winner;
		duelLooser = looser;
	}
	
	// method :: sendDuelChallenge
	public void sendDuelChallenge() {
		//PacketSendUtility.sendPacket(responder, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, requester.getName()));
		//PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.DUEL_ASKED_BY(requester.getName()));
		RequestResponseHandler requesterResponseHandler = new RequestResponseHandler(requester) {
			@Override
			public void acceptRequest(Player _requester, Player _responder)
			{
				startDuel();
				//_requester.getController().startDuelWith(_responder);
				//_responder.getController().startDuelWith(_requester);
			}

			public void denyRequest(Player _requester, Player _responder)
			{
				requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(responder.getName()));
				//PacketSendUtility.sendMessage(requester, "Player " + responder.getName() + " declined duel of "+requester.getName());
				log.debug("player " + _responder.getName() + " rejected duel request from " + _requester.getName());
			}
		};
		
		RequestResponseHandler responderResponseHandler = new RequestResponseHandler(responder) {
			@Override
			public void acceptRequest(Player _requester, Player _responder)
			{
				requester.getController().startDuelWith(responder);
			}

			public void denyRequest(Player _requester, Player _responder)
			{
				_requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(_responder.getName()));
				PacketSendUtility.sendMessage(_requester, "Player " + _responder.getName() + " declined your duel request");
				log.debug("player " + _responder.getName() + " rejected duel request from " + _requester.getName());
			}
		};

		boolean requested = responder.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL,requesterResponseHandler);
		if (!requested){
			// Can't trade with player.
			// TODO: Need to check why and send a error.
			log.debug("requested == false on Duel.java:72");
		}
		else {
			responder.getClientConnection().sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, requester.getName()));
			responder.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_ASKED_BY(requester.getName()));
		}
		requested = requester.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL,responderResponseHandler);
		if (!requested){
			// Can't trade with player.
			// TODO: Need to check why and send a error.
			log.debug("requested == false on Duel.java:82");
		}
		else {
			requester.getClientConnection().sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, responder.getName()));
			requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_ASKED_TO(responder.getName()));
		}
	}
	
	// method :: startDuel
	public void startDuel() 
	{
		log.info("Duel started");
		responder.getClientConnection().sendPacket(new SM_DUEL_STARTED(requester.getObjectId()));
		requester.getClientConnection().sendPacket(new SM_DUEL_STARTED(responder.getObjectId()));
		DuelThread dTh = new DuelThread(requester, responder);
		dTh.start();
		try
		{
			dTh.join();
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Cannot join DuelThread", e);
		}
		
		int requesterHP = requester.getLifeStats().getCurrentHp();
		int responderHP = requester.getLifeStats().getCurrentHp();
		
		if(requesterHP == 0) {
			setDuelResults(responder, requester);
		}
		else {
			setDuelResults(requester, responder);
		}
		finishDuel();
	}
	
	// method :: finishDuel(Player winner, Player looser)
	private void finishDuel() {
		if(duelLooser == null || duelWinner == null) {
			//TODO: investigate why winner and looser are set to null.
		}
		else {
			PacketSendUtility.sendMessage(duelWinner, "You won the duel against " + duelLooser.getName());
			PacketSendUtility.sendMessage(duelLooser, "You lost the duel against " + duelWinner.getName());
			// TODO: find a kind of SM_DUEL_END packet and send it now
		}
	}
	
}