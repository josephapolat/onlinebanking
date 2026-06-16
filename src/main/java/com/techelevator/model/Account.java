package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
    private int accountId;
    private String primarySigner;
    private String secondarySigner;
    private String accountNumber;
    private double balance;
    private String accountNickname;
    public Account(){

    }

    public Account(int accountId, String primarySigner, String secondarySigner, String accountNumber, double balance, String accountNickname){
        this.accountId = accountId;
        this.primarySigner = primarySigner;
        this.secondarySigner = secondarySigner;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountNickname = accountNickname;
    }

    @JsonProperty("primarySigner")
    String getPrimarySigner(){return primarySigner;}
    @JsonProperty("secondarySigner")
    String getSecondarySigner(){return secondarySigner;}
    @JsonProperty("accountNumber")
    String getAccountNumber(){return accountNumber;}
    @JsonProperty("balance")
    double getBalance(){return balance;}
    @JsonProperty("account_id")
    int getAccountId(){return accountId;}
    @JsonProperty("accountNickname")
    String getAccountNickname(){return accountNickname;}
    void setPrimarySigner(){this.primarySigner = primarySigner;}
    void setAccountId(){this.accountId = accountId;}
    void setAccountNumber(){this.accountNumber = accountNumber;}
    void setBalance(){this.balance = balance;}
    void setSecondarySigner(){this.secondarySigner = secondarySigner;}
}
