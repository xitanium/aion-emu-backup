/*
 * This file is part of Onyxia Team.
 */
package com.aionemu.gameserver.configs;

import com.aionemu.commons.configuration.Property;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This config contains settings about cache.
 * @author Eristos
 *
 */
public class Rates
{
	/**
	 * Récupère les paramètres du fichier de configuration
	 *	
	 */
	@Property(key = "xp", defaultValue = "1")
	public static int			XP_RATE;

	@Property(key = "skill_point", defaultValue = "1")
	public static int			SKILL_POINT_RATE;

	@Property(key = "money", defaultValue = "1")
	public static int			MONEY_RATE;

	@Property(key = "party_xp", defaultValue = "1")
	public static int			PARTY_EXP_RATE;

	@Property(key = "drop", defaultValue = "1")
	public static int			DROP_RATE;}
