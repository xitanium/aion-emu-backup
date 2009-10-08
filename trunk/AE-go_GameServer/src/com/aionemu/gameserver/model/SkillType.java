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
package com.aionemu.gameserver.model;

import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.skillengine.handlers.SpellSimple;
import com.aionemu.gameserver.skillengine.handlers.SpellWithObject;
import com.aionemu.gameserver.skillengine.handlers.HealSimple;
import com.aionemu.gameserver.skillengine.handlers.HealWithObject;


/**
 * @author xavier
 *
 */
public enum SkillType
{
	DEFAULT,
	SPELL_OBJECT,
	SPELL_SIMPLE,
	HEAL_OBJECT,
	HEAL_SIMPLE;
	
	public SkillHandler getHandler (int skillId) {
		final SkillHandler handler;
		switch (this) {
			case SPELL_OBJECT:
				handler = new SpellWithObject (skillId);
				break;
			case SPELL_SIMPLE:
				handler = new SpellSimple (skillId);
				break;
			case HEAL_OBJECT:
				handler = new HealWithObject (skillId);
				break;
			case HEAL_SIMPLE:
				handler = new HealSimple (skillId);
				break;
			default:
				throw new IllegalStateException("Cannot instantiate handler for skill type "+this);
		}
		return handler;
	}
}
