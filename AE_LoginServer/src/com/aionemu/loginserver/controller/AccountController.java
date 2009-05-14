/**
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

package com.aionemu.loginserver.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.utils.AccountUtils;

/**
 * This class is resposible for controlling all account account actions
 *
 * @author KID
 * @author SoulKeeper
 */
public class AccountController
{
    /**
     * Map with accounts that are active on LoginServer or joined GameServer
     * and are not authenticated yet.
     */
    private static final Map<Integer, AionConnection> accountsOnLS = new HashMap<Integer, AionConnection>();

    /**
     * Removes account from list of connections
     * @param account account
     */
    public static synchronized void removeAccountOnLS(Account account){
        accountsOnLS.remove(account.getId());
    }

    /**
     * This method is for answering GameServer question about account
     * authentication on GameServer side.
     * @param key
     * @return Account matched to giver SessionKey or null
     */
    public static synchronized Account checkAuth(SessionKey key)
    {
    	AionConnection con = accountsOnLS.get(key.accountId);
    	if(con != null)
    	{
    		if(con.getSessionKey().checkSessionKey(key))
    		{
    			//account is successful loged in on gs
    			//remove it from here
    			accountsOnLS.remove(key.accountId);
    			return con.getAccount();
    		}
    	}
    	return null;
    }

    /**
     * Tries to authentificate account.<br>
     * If success returns {@link AionAuthResponse#AUTHED} and sets
     * account object to connection.<br>
     *
     * If {@link com.aionemu.loginserver.configs.Config#ACCOUNT_AUTO_CREATION} is enabled - creates new account.<br>
     *
     * @param name  name of account
     * @param password password of account
     * @param connection connection for account
     * @return Response with error code
     */
    public static AionAuthResponse login(String name, String password, AionConnection connection) {
        Account account = getDAO().getAccount(name);

        // Try to create new account
        if (account == null && Config.ACCOUNT_AUTO_CREATION) {
            account = createAccount(name, password);
        }

        // If account not found and not created
        if (account == null) {
            return AionAuthResponse.INVALID_PASSWORD;
        }

        // check for paswords beeing equals
        if(!account.getPasswordHash().equals(AccountUtils.encodePassword(password))){
            return AionAuthResponse.INVALID_PASSWORD;
        }

        // If account expired
        if(account.isExpired()){
            return AionAuthResponse.TIME_EXPIRED;
        }

        // if account is banned
        if(account.isPenaltyActive()){
            return AionAuthResponse.BAN_IP;
        }

        // if account is restricted to some ip or mask
        if(account.getIpForce() != null){
            if(!NetworkUtils.checkIPMatching(account.getIpForce(), connection.getIP())){
                return AionAuthResponse.BAN_IP;
            }
        }

        // if ip is banned
        if(BannedIpController.isBanned(connection.getIP())){
            return AionAuthResponse.BAN_IP;
        }

        // Do not allow to login two times with same account
        // TODO: Should we kick old account?
        synchronized (AccountController.class)
        {
            if(GameServerTable.isAccountOnAnyGameServerAndKick(account))
            	return AionAuthResponse.ALREADY_LOGGED_IN;

            if(accountsOnLS.containsKey(account))
                return AionAuthResponse.ALREADY_LOGGED_IN;
            else
            {
                connection.setAccount(account);
                accountsOnLS.put(account.getId(), connection);
            }
        }

        // if everything was OK
        getDAO().updateLastActive(account.getId(), new Timestamp(System.currentTimeMillis()));
        getDAO().updateLastIp(account.getId(), connection.getIP());

        return AionAuthResponse.AUTHED;
    }

    /**
     * Creates new account and stores it in DB. Returns account object in case of success or null if failed
     *
     * @param name     account name
     * @param password account password
     * @return account object or null
     */
    public static Account createAccount(String name, String password) {
        String passwordHash = AccountUtils.encodePassword(password);
        Account account = new Account();
        account.setName(name);
        account.setPasswordHash(passwordHash);
        account.setAccessLevel((byte) 0);
        account.setLastActive(new Timestamp(System.currentTimeMillis()));
        if (getDAO().insertAccount(account)) {
            return account;
        } else {
            return null;
        }
    }


    /**
     * Returns {@link com.aionemu.loginserver.dao.AccountDAO}, just a shortcut
     *
     * @return {@link com.aionemu.loginserver.dao.AccountDAO}
     */
    private static AccountDAO getDAO() {
        return DAOManager.getDAO(AccountDAO.class);
    }
}
