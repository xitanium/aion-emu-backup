/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerStatsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_DP;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class PlayerLifeStats extends CreatureLifeStats<Player>
{

	private int currentDp;
	private int maxDp;
	
	public PlayerLifeStats() {
		this(null,0,0,0,0,0,0);
		this.setInitialized(false);
	}
	
	public PlayerLifeStats(int maxHp, int maxMp, int maxDp)
	{
		this(null, maxHp, maxMp, maxDp, maxHp, maxMp, maxDp);
	}
	
	public PlayerLifeStats(Player owner, int currentHp, int currentMp, int currentDp, int maxHp, int maxMp, int maxDp)
	{
		super(owner, currentHp, currentMp, maxHp, maxMp);
		this.currentDp = currentDp;
		this.maxDp = maxDp;
	}	
	
	@Override
	public void sendHpPacketUpdate () {
		super.sendHpPacketUpdate();
		DAOManager.getDAO(PlayerStatsDAO.class).storeLifeStats(this.getOwner().getObjectId(), this);
	}
	
	@Override
	public void sendMpPacketUpdate () {
		super.sendMpPacketUpdate();
		DAOManager.getDAO(PlayerStatsDAO.class).storeLifeStats(this.getOwner().getObjectId(), this);
	}
	
	public int increaseCurrentDp (int value) {
		synchronized(this)
		{
			int newDp = this.currentDp + value;
			if(newDp > maxDp)
			{
				newDp = maxDp;
			}
			this.currentDp = newDp;		
		}
		
		sendDpPacketUpdate();
		
		return currentDp;
	}
	
	public void setCurrentDp (int currentDp) {
		this.currentDp = currentDp;
	}
	
	private void sendDpPacketUpdate () {
		if(getOwner() == null)
		{
			return;
		}
		PacketSendUtility.sendPacket((Player)getOwner(), new SM_STATUPDATE_DP(currentDp));
		DAOManager.getDAO(PlayerStatsDAO.class).storeLifeStats(this.getOwner().getObjectId(), this);
	}

	public void setMaxDp(int maxDp)
	{
		this.maxDp = maxDp;
	}
	
	public int getMaxDp() {
		return maxDp;
	}
	
	public int getCurrentDp() {
		return currentDp;
	}
	
	public void doEvolution (int fromLevel, int toLevel) {
		setMaxHp((int) Math.round(this.getMaxHp() * 1.07 * (toLevel - fromLevel)));
		setMaxMp((int) Math.round(this.getMaxMp() * 1.07 * (toLevel - fromLevel)));
		setMaxDp((int) Math.round(this.getMaxDp() * 1.07 * (toLevel - fromLevel)));
		DAOManager.getDAO(PlayerStatsDAO.class).storeLifeStats(this.getOwner().getObjectId(), this);
	}
}
