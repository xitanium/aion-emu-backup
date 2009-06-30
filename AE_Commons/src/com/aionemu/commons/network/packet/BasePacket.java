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
package com.aionemu.commons.network.packet;

/**
 * Basic superclass for packets. 
 * <p/>
 * Created on: 29.06.2009 17:59:25
 *
 * @author Aquanox
 */
public abstract class BasePacket
{
	/**
	 * Default packet string representation pattern.
	 *
	 * @see java.util.Formatter
	 * @see String#format(String, Object[])
	 */
	public static final String TYPE_PATTERN = "[%s] 0x%02X %s";

	/**
	 * Packet type field.
	 */
	private final PacketType packetType;

	/**
	 * Packet opcode field
	 */
	private final int opcode;

	/**
	 * Constructs a new packet with specified type and id.
	 *
	 * @param packetType    Type of packet
	 * @param opcode        Id of packet
	 */
	protected BasePacket(PacketType packetType, int opcode)
	{
		this.packetType = packetType;
		this.opcode = opcode;
	}

	/**
	 * Returns packet opcode.
	 *
	 * @return packet id
	 */
	public final int getOpcode()
	{
		return opcode;
	}

	/**
	 * Returns packet type.
	 * 
	 * @return type of this packet.
	 *
	 * @see com.aionemu.commons.network.packet.BasePacket.PacketType
	 */
	public final PacketType getPacketType()
	{
		return packetType;
	}

	/**
	 * Returns packet name. <p/>
	 * Actually packet name is a simple name of the underlying class.
	 * 
	 * @return packet name
	 *
	 * @see Class#getSimpleName()
	 */
	public String getPacketName()
	{
		return this.getClass().getSimpleName();
	}

	/**
	 * Returns string representation of this packet based on  packet type, opcode and name.
	 *
	 * @return packet type string
	 *
	 * @see #TYPE_PATTERN
	 * @see java.util.Formatter
	 * @see String#format(String, Object[])
	 */
	public String getType()
	{
		return String.format(TYPE_PATTERN, getPacketType().getAbbr(), getOpcode(), getPacketName());
	}

	/**
	 * Enumeration of packet types.
	 */
	public static enum PacketType
	{
		/** Server packet */
		SERVER("S"),
		
		/** Client packet */
		CLIENT("C");

		private final String abbr;

		private PacketType(String abbr)
		{
			this.abbr = abbr;
		}

		public String getAbbr()
		{
			return abbr;
		}
	}

	/**
	 * Returns string representation of this packet.
	 *
	 * @return string representation of this packet
	 *
	 * @see #getType()
	 */
	@Override
	public String toString()
	{
		return getType();
	}
}
