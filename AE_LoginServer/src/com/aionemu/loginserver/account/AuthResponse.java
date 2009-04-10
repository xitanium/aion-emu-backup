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
package com.aionemu.loginserver.account;

/**
 * @author KID
 */
public enum AuthResponse
{
	/**
	 * that one is not being sent to client, it's only for internal use.
	 * Everything is OK
	 */
	AUTHED(0),
	/**
	 * System error.
	 */
	SYSTEM_ERROR(1),
	/**
	 * Invalid account name or password.
	 */
	INVALID_PASSWORD(2),
	/**
	 * Invalid account name or password.
	 */
	INVALID_PASSWORD2(3),
	/**
	 * Failed to load your account info.
	 */
	FAILED_ACCOUNT_INFO(4),
	/**
	 * Failed to load your social security number.
	 */
	FAILED_SOCIAL_NUMBER(5),
	/**
	 * No game server is registered to the authorization server.
	 */
	NO_GS_REGISTERED(6),
	/**
	 * You are already logged in.
	 */
	ALREADY_LOGGED_IN(7),
	/**
	 * The selected server is down and not accessible.
	 */
	SERVER_DOWN(8),
	ERROR_9(9), // The login information does not match the information you
	// provided.
	NO_SUCH_ACCOUNT(10), // No Login info available.
	KICK_GM_TOOLS(11), // You are kicked out by the GM or other administration
	// tools.
	ERROR_12(12), // You are under the age limit.
	ALREADY_LOGGED_IN2(13), // Attempted to log in when you are already logged
	// in.
	ALREADY_LOGGED_IN3(14), // You are already playing the game.
	SERVER_DOWN2(15), // The server is not available now.
	CONNECT_ONLY_GM(16), // Currently only GMs are allowed to connect to the
	// server.
	ERROR_17(17), // Please try to enter the game after changing your password
	TIME_EXPIRED(18), // You have used all your playing time allowed.
	TIME_EXPIRED2(19), // The is no time left of this account.
	SYSTEM_ERROR2(20), // System error.
	ALREADY_USED_IP(21), // The IP is already in use.
	BAN_IP(22), // You cannot access the game through this IP.
	ERROR_23(23), // Deleted the character.
	MESSAGE_24(24), // Created the character.
	MESSAGE_25(25), // Invalid character name.
	MESSAGE_26(26), // Invalid character info.
	// ... some shit
	USED_ALL_SERVER_RELAX_TIME(30), // Used up all relax server time.
	// .. some shit
	RESTRICTED_SERVER(33), // Restricted server.
	TIME_EXPIRED3(34), // Usage time has expired.
	NO_TWOBOX_ONE_PC(35), // Multiple client loading from one computer is not
	// allowed in an Internet Cafe.
	DORMANT_ACCOUNT(36), // Dormant account.
	UNKNOWN(37); // Unknown error

	private int	messageId;

	private AuthResponse(int msgId)
	{
		messageId = msgId;
	}

	public int getMessageId()
	{
		return messageId;
	}
}
