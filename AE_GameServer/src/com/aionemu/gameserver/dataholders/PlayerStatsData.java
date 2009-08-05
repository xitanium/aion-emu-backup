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
package com.aionemu.gameserver.dataholders;


import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.templates.StatsTemplate;
import com.aionemu.gameserver.model.gameobjects.player.Player;


import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created on: 31.07.2009 14:20:03
 *
 * @author Aquanox
 */
@XmlRootElement(name = "stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData
{
	@XmlElement(name = "stats_template", required = true)
	private List<StatsTemplate> templatesList = new ArrayList<StatsTemplate>();

	private final Map<Integer, StatsTemplate> templates = new HashMap<Integer, StatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (StatsTemplate pt : templatesList)
		{
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());

			templates.put(code, pt);
		}

		templatesList.clear();
		templatesList = null;
	}

	public StatsTemplate getTemplate(Player player)
	{
		return getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
	}

	public StatsTemplate getTemplate(PlayerClass playerClass, int level)
	{
		return templates.get(makeHash(playerClass, level));
	}

	private static int makeHash(PlayerClass playerClass, int level)
	{
		int result = 0x1f;
		result = 0x1f * result + playerClass.ordinal();
		result = 0x1f * result + level;
		return result;
	}

	public int size()
	{
		return templates.size();
	}
}
