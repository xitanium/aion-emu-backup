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
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.model.SkillType;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.skillengine.loader.SkillHandlerLoader;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author ATracer
 *
 */
public class SkillEngine
{
	private static final Logger log = Logger.getLogger(SkillEngine.class);
	
	public static final File CHAT_DESCRIPTOR_FILE = new File("./data/scripts/system/skills.xml");
	
	@Inject
	private SkillData skillData;
	
	private static final Map<Integer, SkillHandler> skillHandlers = new HashMap<Integer, SkillHandler>();
	
	public void registerSkill(SkillHandler skillHandler)
	{
		int skillId = skillHandler.getSkillId();
		skillHandler.setSkillTemplate(skillData.getSkillTemplate(skillId));
		skillHandlers.put(skillId, skillHandler);
	}
	
	public int getHandlersCount()
	{
		return skillHandlers.size();
	}
	
	public static SkillHandler getSkillHandlerFor(int skillId)
	{
		SkillHandler skillHandler =  skillHandlers.get(skillId);
		if(skillHandler == null)
		{
			log.info("There is no skill handler for skillId: " + skillId);
			return null;
		}
		return skillHandler;
	}
	
	public void registerAllSkills(SkillData skillData, Injector injector)
	{
		ScriptManager sm = new ScriptManager();

		this.skillData = skillData;
		
		for (Map.Entry<Integer, SkillTemplate> skill : skillData) {
			SkillTemplate template = skill.getValue();
			SkillType type = template.getType();
			int skillId = skill.getKey();
			try {
				final SkillHandler handler = type.getHandler(skillId);
				handler.setSkillTemplate(template);
				skillHandlers.put(skillId, handler);
				log.debug("Loaded generic skill#"+skillId+" ("+template.getName()+") handler "+handler.getClass().getName());
			} catch (AssertionError e) {
				log.debug("Generic handler for skill#"+skillId+" not found: "+e.getMessage());
			}
		}
		
		log.debug("Now loading of specific skill handlers...");
		
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
}
