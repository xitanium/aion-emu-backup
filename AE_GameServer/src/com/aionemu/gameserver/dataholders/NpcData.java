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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.NpcTemplate;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 * 
 * @author Luno
 * 
 */
public class NpcData extends DataLoader
{
	/** A map containing all npc templates */
	private Map<Integer, NpcTemplate>	npcData	= new HashMap<Integer, NpcTemplate>();

	/**
	 * Constructor, which is supposed to be called just once, from {@link DataManager} class. Calling this constructor
	 * implies immediately loading npc data from npcdata.txt file.
	 */
	NpcData()
	{
		super("npcdata.txt");
		loadData();
		log.info("Loaded " + npcData.size() + " npc templates");

	}

	/**
	 * Returns an {@link NpcTemplate} object with given id.
	 * 
	 * @param id
	 *            id of NPC
	 * @return NpcTemplate object containing data about NPC with that id.
	 */
	public NpcTemplate getNpcTemplate(int id)
	{
		return npcData.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(String params)
	{
		String[] spld = params.split(" ", 3);
		if(spld.length < 3)
			return;

		int id = Integer.parseInt(spld[0]);
		int nameId = Integer.parseInt(spld[1]);
		String name = spld[2];

		NpcTemplate template = new NpcTemplate(id, nameId, name);
		npcData.put(id, template);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSaveFile()
	{
		return "new_npcdata.txt";
	}
}
