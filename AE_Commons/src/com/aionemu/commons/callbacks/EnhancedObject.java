/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.callbacks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Generic interface for all enhanced object.<br>
 * <font color="red">NEVER IMPLEMENT THIS CLASS MANUALLY!!!</font>
 * 
 * @author SoulKeeper
 */
public interface EnhancedObject
{

	/**
	 * Adds callback to this object.<br>
	 * 
	 * @param callback
	 *            instance of callback to add
	 * @see com.aionemu.commons.callbacks.CallbackHelper#addCallback(Callback, EnhancedObject)
	 */
	public void addCallback(Callback callback);

	/**
	 * Removes callback from this object
	 * 
	 * @param callback
	 *            instance of callback to remove
	 * @see com.aionemu.commons.callbacks.CallbackHelper#removeCallback(Callback, EnhancedObject)
	 */
	public void removeCallback(Callback callback);

	/**
	 * Returns all callbacks associated with this
	 * 
	 * @return unmodifiable list of callbacks
	 */
	public Map<Class<? extends Callback>, List<Callback>> getCallbacks();

	/**
	 * Returns lock that is used to ensure thread safety
	 * 
	 * @return lock that is used to ensure thread safety
	 */
	public ReentrantReadWriteLock getCallbackLock();
}
