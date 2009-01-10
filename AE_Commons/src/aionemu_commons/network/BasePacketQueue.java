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
package aionemu_commons.network;

import java.util.Iterator;

/**
 * @author mkizub
 */
public final class BasePacketQueue
{

	BasePacket<?>	_first;
	BasePacket<?>	_last;

	private int		size;

	public synchronized boolean isEmpty()
	{
		return _first == null;
	}

	public synchronized void put(BasePacket<?> pkt)
	{
		pkt._queue = this;
		if (_first == null)
		{
			_first = _last = pkt;
		}
		else
		{
			pkt._prev = _last;
			_last._next = pkt;
			_last = pkt;
		}
		size++;
	}

	public synchronized BasePacket<?> get()
	{
		if (_first == null)
			return null;
		BasePacket<?> pkt = _first;
		_first = pkt._next;
		pkt._queue = this;
		if (_first == null)
			_last = null;
		else
			_first._prev = null;
		pkt._queue = null;
		pkt._prev = null;
		pkt._next = null;
		size--;
		return pkt;
	}

	public synchronized void remove(BasePacket<?> pkt)
	{
		if (pkt == null || pkt._queue == null)
			return;
		if (pkt._prev != null)
			pkt._prev._next = pkt._next;
		if (pkt._next != null)
			pkt._next._prev = pkt._prev;
		if (_first == pkt)
			_first = pkt._next;
		if (_last == pkt)
			_last = pkt._prev;
		pkt._queue = null;
		pkt._prev = null;
		pkt._next = null;
		size--;
	}

	public int size()
	{
		return size;
	}

	Iterator<BasePacket<?>> iterator()
	{
		return new Iter();
	}

	class Iter implements Iterator<BasePacket<?>>
	{
		private BasePacket<?>	_lastRet;
		private BasePacket<?>	_next;

		Iter()
		{
			_next = _first;
		}

		public void remove()
		{
			BasePacketQueue.this.remove(_lastRet);
		}

		public boolean hasNext()
		{
			return _next != null;
		}

		public BasePacket<?> next()
		{
			if (_next == null)
				return null;
			_lastRet = _next;
			_next = _next._next;
			return _lastRet;
		}
	}
}
