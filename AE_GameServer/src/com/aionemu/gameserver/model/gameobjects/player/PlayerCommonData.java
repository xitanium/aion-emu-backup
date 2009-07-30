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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class is holding base information about player, that may be used even when player itself is not online.
 * 
 * @author Luno
 * 
 */
public class PlayerCommonData
{
	private final int		playerObjId;
	private Race			race;
	private String			name;
	private PlayerClass		playerClass;
	private boolean			admin;
	private Gender			gender;

	private WorldPosition	position;

	public PlayerCommonData(int objId)
	{
		this.playerObjId = objId;
	}

	public int getPlayerObjId()
	{
		return playerObjId;
	}

	public Race getRace()
	{
		return race;
	}

	public void setRace(Race race)
	{
		this.race = race;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}

	public Gender getGender()
	{
		return gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public WorldPosition getPosition()
	{
		return position;
	}

	/**
	 * This method should be called exactly once after creating object of this class
	 * @param position
	 */
	public void setPosition(WorldPosition position)
	{
		if(this.position != null)
		{
			throw new IllegalStateException("position already set");
		}
		this.position = position;
	}
}
