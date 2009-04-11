package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.scripting.metadata.OnClassUnload;
import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.model.Account;

/**
 * MySQL5 Account DAO implementation
 * @author SoulKeeper
 */
public class MySQL5AccountDAO extends AccountDAO {

    /**
     * {@inheritDoc}
     */
    private static final Logger log = Logger.getLogger(MySQL5AccountDAO.class);

    /**
     * Register dao on class load
     */
    @OnClassLoad
    public static void onLoad(){
        try {
            DAOManager.registerDAO(MySQL5AccountDAO.class);
        } catch (IllegalAccessException e) {
            log.error("Can't register DAO", e);
        } catch (InstantiationException e) {
            log.error("Can't register DAO", e);
        }
    }

    @OnClassUnload
    public static void onUnload(){
        DAOManager.unregisterDAO(MySQL5AccountDAO.class);
    }

    @Override
    public Account getAccount(String name) {
        Account account = null;
        PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `name` = ?");
        try{
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                account.setId(rs.getInt("id"));
                account.setName(name);
                account.setPasswordHash(rs.getString("password"));
                account.setLastActive(rs.getTimestamp("last_active"));
                account.setExpirationTime(rs.getTimestamp("expiration_time"));
                account.setAccessLevel(rs.getByte("access_level"));
                account.setLastServer(rs.getByte("last_server"));
                account.setLastIp(rs.getString("last_ip"));
                account.setIpForce(rs.getString("ip_force"));
            }
        } catch(Exception e){
            log.error("Can't select account with name: " + name, e);
        } finally {
            DB.close(st);
        }

        return account;
    }

    @Override
    public int getAccountId(String name) {
        int id = -1;
        PreparedStatement st = DB.prepareStatement("SELECT `id` FROM account_data WHERE `name` = ?");
        try {
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            rs.next();
            id = rs.getInt("id");
        } catch (SQLException e) {
            log.error("Can't select id after account insertion", e);
        }
        finally {
            DB.close(st);
        }

        return id;
    }

    @Override
    public int getAccountCount() {
        PreparedStatement st = DB.prepareStatement("SELECT count(*) AS c FROM account_data");
        ResultSet rs = DB.executeQuerry(st);
        try {
            rs.next();
            return rs.getInt("c");
        } catch (SQLException e) {
            log.error("Can't get account count", e);
        } finally {
            DB.close(st);
        }

        return -1;
    }


    @Override
    public boolean insertAccount(Account account) {

        int result = 0;
        PreparedStatement st = DB.prepareStatement("INSERT INTO account_data(`name`, `password`, last_active, expiration_time, penalty_end, access_level, last_server, last_ip, ip_force) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            st.setString(1, account.getName());
            st.setString(2, account.getPasswordHash());
            st.setTimestamp(3, account.getLastActive());
            st.setTimestamp(4, account.getExpirationTime());
            st.setTimestamp(5, account.getPenaltyEnd());
            st.setByte(6, account.getAccessLevel());
            st.setByte(7, account.getLastServer());
            st.setString(8, account.getIpForce());
            result = st.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't inser account", e);
        } finally {
            DB.close(st);
        }

        if (result > 0) {
            account.setId(getAccountId(account.getName()));
        }

        return result > 0;
    }

    @Override
    public boolean updateAccount(Account account) {
        int result = 0;
        PreparedStatement st = DB.prepareStatement("UPDATE account_data SET `name` = ?, `password` = ?, last_active = ?, expiration_time = ?, penalty_end = ?, access_level = ?, last_server = ?, last_ip = ?, ip_force = ? WHERE `id` = ?");
        try {
            st.setString(1, account.getName());
            st.setString(2, account.getPasswordHash());
            st.setTimestamp(3, account.getLastActive());
            st.setTimestamp(4, account.getExpirationTime());
            st.setTimestamp(5, account.getPenaltyEnd());
            st.setByte(6, account.getAccessLevel());
            st.setByte(7, account.getLastServer());
            st.setString(8, account.getIpForce());
            st.setInt(9, account.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            log.error("Can't update account");
        } finally {
            DB.close(st);
        }

        return result > 0;
    }

    @Override
    public boolean updateLastActive(final int accountId, final Timestamp time) {

        return DB.insertUpdate("UPDATE account_data SET last_active = ? WHERE id = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setTimestamp(1, time);
                preparedStatement.setInt(2, accountId);
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean updateLastServer(final int accountId, final byte lastServer) {
        return DB.insertUpdate("UPDATE account_data SET last_server = ? WHERE id = ?", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setByte(1, lastServer);
                preparedStatement.setInt(2, accountId);
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean updateLastIp(final int accountId, final String ip) {
        return DB.insertUpdate("UPDATE account_data SET last_ip = ? WHERE id = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, ip);
                preparedStatement.setInt(2, accountId);
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean supports(String database, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
    }
}
