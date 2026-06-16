package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Account;
import com.techelevator.model.Signer;
import com.techelevator.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------- USERS ----------------

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);
            if (rs.next()) {
                return mapRowToUser(rs);
            }
            return null;
        } catch (Exception e) {
            throw new DaoException("Error getting user by id", e);
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("DB connection error", e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, username);
            if (rs.next()) {
                return mapRowToUser(rs);
            }
            return null;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("DB connection error", e);
        }
    }

    @Override
    public User createUser(User newUser) {
        String sql = """
            INSERT INTO users (username, password_hash, role, signer_id)
            VALUES (?, ?, ?, ?)
            RETURNING user_id
        """;

        try {
            String hash = new BCryptPasswordEncoder().encode(newUser.getHashedPassword());

            Integer userId = jdbcTemplate.queryForObject(
                    sql,
                    Integer.class,
                    newUser.getUsername(),
                    hash,
                    newUser.getRole(),
                    newUser.getSignerId()
            );

            return getUserById(userId);

        } catch (DataIntegrityViolationException e) {
            throw new DaoException("User already exists or invalid data", e);
        }
    }

    // ---------------- SIGNER ----------------

    @Override
    public Signer getSignerBySignerSsn(String ssn) {
        String sql = "SELECT * FROM customers WHERE ssn = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, ssn);
        if (rs.next()) {
            return new Signer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("ssn")
            );
        }
        return null;
    }

    @Override
    public Signer getSignerBySignerId(int signerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, signerId);
        if (rs.next()) {
            return new Signer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("ssn")
            );
        }
        return null;
    }

    @Override
    public String getSignerByUsername(String username) {
        String sql = """
            SELECT c.ssn
            FROM customers c
            JOIN users u ON u.signer_id = c.customer_id
            WHERE u.username = ?
        """;

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, username);
        if (rs.next()) {
            return rs.getString("ssn");
        }
        return "";
    }

    // ---------------- ACCOUNTS ----------------

    @Override
    public List<Account> getAccountsBySignerSsn(String ssn) {
        List<Account> accounts = new ArrayList<>();

        String sql = """
            SELECT * FROM accounts
            WHERE primary_signer = ?
               OR secondary_signer = ?
        """;

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, ssn, ssn);

        while (rs.next()) {
            accounts.add(new Account(
                    rs.getInt("account_id"),
                    rs.getString("primary_signer"),
                    rs.getString("secondary_signer"),
                    rs.getString("account_number"),
                    rs.getDouble("balance"),
                    rs.getString("account_nickname")
            ));
        }

        return accounts;
    }

    @Override
    public String getAccountNickname(String accNum) {
        String sql = "SELECT account_nickname FROM accounts WHERE account_number = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, accNum);
        if (rs.next()) {
            return rs.getString("account_nickname");
        }
        return "";
    }

    // ---------------- TRANSFERS ----------------

    @Override
    public void transferBalance(String fromAccount, String toAccount, double amount) {

        String getSql = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        SqlRowSet rsTo = jdbcTemplate.queryForRowSet(getSql, toAccount);
        SqlRowSet rsFrom = jdbcTemplate.queryForRowSet(getSql, fromAccount);

        double toBalance = 0;
        double fromBalance = 0;

        if (rsTo.next()) toBalance = rsTo.getDouble("balance");
        if (rsFrom.next()) fromBalance = rsFrom.getDouble("balance");

        jdbcTemplate.update(updateSql, toBalance + amount, toAccount);
        jdbcTemplate.update(updateSql, fromBalance - amount, fromAccount);
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

    // ---------------- MAPPING ----------------

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setHashedPassword(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));

        // IMPORTANT: avoid breaking if column exists
        try {
            user.setSignerId(rs.getInt("signer_id"));
        } catch (Exception ignored) {}

        return user;
    }
}
