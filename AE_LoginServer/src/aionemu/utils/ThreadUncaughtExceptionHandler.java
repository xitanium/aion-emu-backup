/**
 * This file is part of aion-emu.
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
package aionemu.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import aionemu_commons.network.nio.Dispatcher;

/**
 * @author -Nemesiss-
 */
public class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
	private static final Logger	_log	= Logger.getLogger(ThreadUncaughtExceptionHandler.class.getName());

	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		_log.log(Level.WARNING, "Critical Error - Thread: " + t.getName() + " terminated abnormaly: " + e, e);
		if (e instanceof OutOfMemoryError)
		{
			// TODO try get some memory or restart
		}
		if (t instanceof Dispatcher)
		{
			_log.info("Restarting Dispatcher...");
			// TODO!
		}
		// TODO! some threads should be "restarted" on error
	}
}
