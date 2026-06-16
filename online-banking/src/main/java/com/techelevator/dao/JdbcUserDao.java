package com.techelevator.dao;

import java.util.ArrayList;
import java.util.List;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Account;
import com.techelevator.model.Signer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.techelevator.model.User;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(int userId) {

        User user = new User();
        String sql = "SELECT * FROM users WHERE user_id = ?";


            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while(results.next()) {
                user.setUsername(results.getString("username"));
                user.setId(results.getInt("user_id"));
            }

        return user;
    }

    @Override
    public List<User> getUsers() {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return users;
    }

    @Override
    public User getUserByUsername(String username) {

        if (username == null) {
            username = "";
        }
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public User createUser(User newUser) {

        User user = null;
        String insertUserSql = "INSERT INTO users " +
                "(username, password_hash, role) " +
                "VALUES (?, ?, ?) " +
                "RETURNING user_id";

        if (newUser.getHashedPassword() == null) {
            throw new DaoException("User cannot be created with null password");
        }
        try {
            String passwordHash = new BCryptPasswordEncoder().encode(newUser.getHashedPassword());

            Integer userId = jdbcTemplate.queryForObject(insertUserSql, int.class,
                    newUser.getUsername(), passwordHash, newUser.getRole());
            user =  getUserById(userId);
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return user;
    }

    @Override
    public Signer getSignerBySignerSsn(String ssn) {
        Signer signer = null;
        String sql = "SELECT * FROM customers WHERE ssn = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, ssn);
        while(results.next()){
            signer = new Signer(results.getInt("customer_id"), results.getString("name"), results.getString("ssn"));
        }
        return signer;
    }
    @Override
    public String getAccountNickname(String accNum){
        String sql = "SELECT account_nickname FROM accounts WHERE account_number = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accNum);
        String nickname = "";
        if(results.next()){
            nickname = results.getString("account_nickname");
        }
        return nickname;
    }


    public Signer getSignerBySignerId(int signerId) {
        Signer signer = null;
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, signerId);
        while(results.next()){
            signer = new Signer(results.getInt("customer_id"), results.getString("name"), results.getString("ssn"));
        }
        return signer;
    }

    @Override
    public String getSignerByUsername(String username) {
        String signerSsn = "";
        String sql = "SELECT * FROM customers WHERE customer_id = (SELECT signer_id FROM users WHERE username = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if(results.next()){
            signerSsn = results.getString("ssn");
        }
        return signerSsn;
    }

    @Override
    public List<Account> getAccountsBySignerId(int signerId) {
        return null;
    }

    @Override
    public List<Account> getAccountsBySignerSsn(String ssn) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE primary_signer = ? UNION SELECT * FROM accounts WHERE secondary_signer = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, ssn, ssn);
        while(results.next()){
            Account curAcc = new Account(results.getInt("account_id"), results.getString("primary_signer"), results.getString("secondary_signer"), results.getString("account_number"), results.getDouble("balance"), results.getString("account_nickname"));
            accounts.add(curAcc);
        }
        return accounts;
    }
    public Signer getSignerByUserId(int userId){
        Signer signer = null;
        String sql = "SELECT * FROM customers WHERE customer_id = (SELECT signer_id FROM users WHERE signer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        signer.setSignerId(results.getInt("customer_id"));
        signer.setName(results.getString("name"));
        signer.setSSN(results.getString("ssn"));
        return signer;
    }
    public String getUsernameByUserId(int id){
        String sql = "SELECT username FROM users WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        return results.getString("username");
    }
    @Override
    public void transferBalance(String fromAccount, String toAccount, double transferAmount){
        double balanceToAccount = 0.00;
        double balanceFromAccount = 0.00;
        String sqlGetBalanceToAccount = "SELECT balance FROM accounts WHERE account_number = ?";
        SqlRowSet currentBalanceToAccount = jdbcTemplate.queryForRowSet(sqlGetBalanceToAccount, toAccount);

        if(currentBalanceToAccount.next()){
            balanceToAccount = currentBalanceToAccount.getDouble("balance");
        }
        balanceToAccount = balanceToAccount + transferAmount;
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        jdbcTemplate.update(sql, balanceToAccount, toAccount);
        String sqlGetBalanceFromAccount = "SELECT balance FROM accounts WHERE account_number = ?";
        SqlRowSet currentBalanceFromAccount = jdbcTemplate.queryForRowSet(sqlGetBalanceFromAccount, fromAccount);
        if(currentBalanceFromAccount.next()){
            balanceFromAccount = currentBalanceFromAccount.getDouble("balance");
        }

        balanceFromAccount = balanceFromAccount - transferAmount;
        String sql2 = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        jdbcTemplate.update(sql2, balanceFromAccount, fromAccount);

    }

    @Override
    public void updateNickname(String accountNumber, String nickName) {
        String sql = "UPDATE accounts SET account_nickname = ? WHERE account_number = ?";
        jdbcTemplate.update(sql, nickName, accountNumber);
    }

    @Override
    public void closeAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        jdbcTemplate.update(sql, accountNumber);

    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setHashedPassword(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
