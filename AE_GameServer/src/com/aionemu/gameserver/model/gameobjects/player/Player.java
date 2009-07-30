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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is representing Player object, it contains all needed data.
 * 
 * 
 * @author -Nemesiss-
 * @author SoulKeeper
 * 
 */
public class Player extends Creature
{
	private PlayerAppearance	playerAppearance;
	private PlayerCommonData	playerCommonData;
	private MacroList			macroList;

	/** When player enters game its char is in kind of "protection" state, when is blinking etc */
	private boolean				protectionActive;

	/**
	 * Connection of this Player.
	 */
	private AionConnection		clientConnection;

	/**
	 * @param objId
	 */
	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance)
	{
		super(plCommonData.getPlayerObjId(), controller, plCommonData.getPosition());
		
		this.playerCommonData = plCommonData;
		this.playerAppearance = appereance;
		
		controller.setOwner(this);
	}

	public PlayerCommonData getCommonData()
	{
		return playerCommonData;
	}

	@Override
	public String getName()
	{
		return playerCommonData.getName();
	}

	public PlayerAppearance getPlayerAppearance()
	{
		return playerAppearance;
	}

	/**
	 * Set connection of this player.
	 * 
	 * @param clientConnection
	 */
	public void setClientConnection(AionConnection clientConnection)
	{
		this.clientConnection = clientConnection;
	}

	/**
	 * Get connection of this player.
	 * 
	 * @return AionConnection of this player.
	 * 
	 */
	public AionConnection getClientConnection()
	{
		return this.clientConnection;
	}

	public boolean isProtectionActive()
	{
		return protectionActive;
	}

	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an
	 * action char stops blinking.
	 * 
	 * @param protectionActive
	 */
	public void setProtectionActive(boolean protectionActive)
	{
		this.protectionActive = protectionActive;
		if(!protectionActive)
			PacketSendUtility.sendPacket(this, new SM_PLAYER_STATE(this));
	}

	public MacroList getMacroList()
	{
		return macroList;
	}

	public void setMacroList(MacroList macroList)
	{
		this.macroList = macroList;
	}

	/**
	 * Return PlayerController of this Player Object.
	 * 
	 * @return PlayerController.
	 */
	@Override
	public PlayerController getController()
	{
		return (PlayerController) super.getController();
	}
}
