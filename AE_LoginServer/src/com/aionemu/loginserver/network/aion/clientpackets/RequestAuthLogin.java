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
package com.aionemu.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;

import com.aionemu.loginserver.account.AccountController;
import com.aionemu.loginserver.account.AuthResponse;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionConnection.State;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.LoginFail;
import com.aionemu.loginserver.network.aion.serverpackets.LoginOk;

/**
 * @author -Nemesiss-, KID
 */
public class RequestAuthLogin extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(RequestAuthLogin.class);

	private byte[]				data;

	private String				user;
	private String				password;
	private int					ncotp;

	public RequestAuthLogin(ByteBuffer buf, AionConnection client)
	{
		super(buf, client);
		readD();
		if (getRemainingBytes() >= 128)
		{
			data = readB(135);
		}
	}

	protected void runImpl()
	{
		if (data == null)
			return;

		byte[] decrypted;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, getConnection().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(data, 0, 128);
		}
		catch (GeneralSecurityException e)
		{
			log.warn("Error while decripting data on user auth." + e, e);
			sendPacket(new LoginFail(AuthResponse.INVALID_PASSWORD));
			return;
		}
		user = new String(decrypted, 64, 32).trim().toLowerCase();
		password = new String(decrypted, 96, 32).trim().toLowerCase();

		ncotp = decrypted[0x7c];
		ncotp |= decrypted[0x7d] << 8;
		ncotp |= decrypted[0x7e] << 16;
		ncotp |= decrypted[0x7f] << 24;

		log.info("AuthLogin: " + user + " pass: " + password + " ncotp: " + ncotp);

		AionConnection client = getConnection();
		AuthResponse response = AuthResponse.AUTHED;//AccountController.tryAuth(user, password, client.getIP());
		switch (response)
		{
			case AUTHED:
				client.setAccount(user);
				client.setState(State.AUTHED_LOGIN);
				client.setSessionKey(new SessionKey());
				if (Config.SHOW_LICENCE)
					client.sendPacket(new LoginOk(client.getSessionKey()));
				// else
				// sendPacket(new ServerList());
				break;

			default:
				sendPacket(new LoginFail(response));
				break;
		}
	}

	public String getType()
	{
		return "0x0B RequestAuthLogin";
	}
}