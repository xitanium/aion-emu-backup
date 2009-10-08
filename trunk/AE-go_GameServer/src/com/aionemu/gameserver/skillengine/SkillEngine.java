/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.skillengine;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.skillengine.loader.SkillHandlerLoader;
import com.google.inject.Injector;

/**
 * @author ATracer
 *
 */
public class SkillEngine
{
	private static final Logger log = Logger.getLogger(SkillEngine.class);
	
	public static final File CHAT_DESCRIPTOR_FILE = new File("./data/scripts/system/skills.xml");
	
	public static final SkillEngine skillEngine = new SkillEngine();
	
	private static final Map<Integer, SkillHandler> skillHandlers = new HashMap<Integer, SkillHandler>();

	/**
	 * should not be instantiated directly
	 */
	private SkillEngine()
	{	
	}
	
	public void registerSkill(SkillHandler skillHandler)
	{
		int skillId = skillHandler.getSkillId();
		SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st==null) {
			throw new GameServerError("There is no template for skill #"+skillId);
		}
		skillHandler.setSkillTemplate(st);
		log.info("[registerSkill] Registering skill #"+skillId+" with handler "+st.getName()+" of type "+st.getType());
		skillHandlers.put(skillId, skillHandler);
	}
	
	public int getHandlersCount()
	{
		return skillHandlers.size();
	}
	
	public SkillHandler getSkillHandlerFor(int skillId)
	{
		SkillHandler skillHandler =  skillHandlers.get(skillId);
		if(skillHandler == null)
		{
			log.info("There is no skill handler for skillId: " + skillId);
			return null;
		}
		return skillHandler;
	}
	public void registerAllSkills(Injector injector)
	{
		ScriptManager sm = new ScriptManager();

		Iterator<Map.Entry<Integer, SkillTemplate>> iter = DataManager.SKILL_DATA.getIterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, SkillTemplate> entry = iter.next();
			int skillId = entry.getKey();
			SkillType type = entry.getValue().getType();
			if (type!=SkillType.DEFAULT) {
				log.info("[registerAllSkills] Registering handler type "+type+" for skill #"+skillId);
				registerSkill(entry.getValue().getType().getHandler(entry.getKey()));
			}
		}
		
		sm.setGlobalClassListener(new SkillHandlerLoader(injector, this));

		try
		{
			sm.load(CHAT_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize skill handlers.", e);
		}
	}
	
	public static SkillEngine getInstance()
	{
		return skillEngine;
	}
}
