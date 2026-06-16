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
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------- USERS ----------------

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            users.add(mapRowToUser(rs));
        }
        return users;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);

        if (rs.next()) {
            return mapRowToUser(rs);
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, username);

        if (rs.next()) {
            return mapRowToUser(rs);
        }
        return null;
    }

    @Override
    public User createUser(User newUser) {
        String sql = "INSERT INTO users (username, password_hash, role, signer_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING user_id";

        String hash = encoder.encode(newUser.getHashedPassword());

        Integer id = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                newUser.getUsername(),
                hash,
                newUser.getRole(),
                newUser.getSignerId()
        );

        return getUserById(id);
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
    public String getSignerByUsername(String username) {
        String sql =
                "SELECT c.ssn " +
                "FROM customers c " +
                "JOIN users u ON u.signer_id = c.customer_id " +
                "WHERE u.username = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, username);

        if (rs.next()) {
            return rs.getString("ssn");
        }
        return null;
    }

    // ---------------- ACCOUNTS ----------------

    @Override
    public List<Account> getAccountsBySignerId(int signerId) {
        List<Account> accounts = new ArrayList<>();

        String sql =
                "SELECT * FROM accounts " +
                "WHERE primary_signer = (SELECT ssn FROM customers WHERE customer_id = ?) " +
                "OR secondary_signer = (SELECT ssn FROM customers WHERE customer_id = ?)";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, signerId, signerId);

        while (rs.next()) {
            accounts.add(mapRowToAccount(rs));
        }

        return accounts;
    }

    @Override
    public List<Account> getAccountsBySignerSsn(String ssn) {
        List<Account> accounts = new ArrayList<>();

        String sql =
                "SELECT * FROM accounts WHERE primary_signer = ? OR secondary_signer = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, ssn, ssn);

        while (rs.next()) {
            accounts.add(mapRowToAccount(rs));
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

    // ---------------- TRANSACTIONS ----------------

    @Override
    public void transferBalance(String fromAccount, String toAccount, double amount) {
        String debit = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String credit = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        jdbcTemplate.update(debit, amount, fromAccount);
        jdbcTemplate.update(credit, amount, toAccount);
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

    // ---------------- MAPPERS ----------------

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setHashedPassword(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        return user;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        return new Account(
                rs.getInt("account_id"),
                rs.getString("primary_signer"),
                rs.getString("secondary_signer"),
                rs.getString("account_number"),
                rs.getDouble("balance"),
                rs.getString("account_nickname")
        );
    }
}
