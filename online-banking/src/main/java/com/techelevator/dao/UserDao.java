package com.techelevator.dao;

import com.techelevator.model.Account;
import com.techelevator.model.Signer;
import com.techelevator.model.User;
import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int userId);

    User getUserByUsername(String username);

    User createUser(User newUser);
    Signer getSignerBySignerSsn(String ssn);
    String getAccountNickname(String accNum);
    String getSignerByUsername(String username);
    List<Account> getAccountsBySignerId(int signerId);

    List<Account> getAccountsBySignerSsn(String ssn);

    void transferBalance(String fromAccount, String toAccount, double transferAmount);
    void updateNickname(String accountNumber, String nickName);
    void closeAccount(String accountNumber);
}
