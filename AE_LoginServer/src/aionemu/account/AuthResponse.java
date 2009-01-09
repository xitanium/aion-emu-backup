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
package aionemu.account;

/**
 * 
 * @author KID
 *
 */
public enum AuthResponse
{
	//TODO correct ids, when we know their vaules
	
	AUTHED				(0), // that one is not being sent to client, it's only for internal use 
	INVALID_PASSWORD	(1), 
	ACCOUNT_IN_USE_LS	(1), 
	ACCOUNT_IN_USE_GS	(1), 
	BAN					(1), 
	RESTRICTED_IP		(1), 
	PENALTY_ACTIVE		(1), 
	TIME_EXPIRED		(1);
	
	private int messageId;
	
	private AuthResponse(int msgId)
	{
		messageId = msgId;
	}
	public int getMessageId(){ return messageId; }
}
