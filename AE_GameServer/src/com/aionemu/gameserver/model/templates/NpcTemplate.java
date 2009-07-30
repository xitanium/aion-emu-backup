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
package com.aionemu.gameserver.model.templates;

/**
 * @author Luno
 * 
 */
public class NpcTemplate
{
	private int		npcId;
	private int		nameId;
	private String	name;

	/**
	 * @param id
	 * @param nameId
	 * @param name
	 */
	public NpcTemplate(int id, int nameId, String name)
	{
		this.npcId = id;
		this.nameId = nameId;
		this.name = name;
	}

	public int getNpcId()
	{
		return npcId;
	}

	public int getNameId()
	{
		return nameId;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Npc Template id: " + npcId + " name: " + name;
	}
}
