package com.techelevator.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.techelevator.model.*;
import jakarta.validation.Valid;

import com.techelevator.exception.DaoException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.techelevator.dao.UserDao;
import com.techelevator.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

/**
 * AuthenticationController is a class used for handling requests to authenticate Users.
 *
 * It depends on an instance of a UserDao for retrieving and storing user data. This is provided
 * through dependency injection.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/online-banking")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            if(authentication.isAuthenticated()){
                String jwt = tokenProvider.createToken(authentication, false);
                User user = userDao.getUserByUsername(loginDto.getUsername());
                return new LoginResponseDto(jwt, user);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage());
        }
    }
    @RequestMapping(path = "/accounts", method = RequestMethod.POST)
    public List<Account> getAccounts(@RequestBody User user){

        String ssn = userDao.getSignerByUsername(user.getUsername());

        return new ArrayList<>(userDao.getAccountsBySignerSsn(ssn));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public User register(@Valid @RequestBody RegisterUserDto newUser) {

        if(!newUser.isPasswordsMatch()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and confirm password do not match");
        }

        try {
            if (userDao.getUserByUsername(newUser.getUsername()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists.");
            }

            User user = userDao.createUser(new User(newUser.getUsername(), newUser.getPassword(), newUser.getRole()));
            return user;
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage());
        }
    }
    @RequestMapping(path = "/transfer", method = RequestMethod.PUT)
    public void transferFunds(@RequestBody Map<String, String> info){
        userDao.transferBalance(info.get("fromAccount"), info.get("toAccount"), Double.parseDouble(info.get("transferAmount")));
    }
    @RequestMapping(path = "/submit-nickname", method = RequestMethod.PUT)
    public void submitNickname(@RequestBody Map<String, String> info){
        userDao.updateNickname(info.get("accountNumber"), info.get("nickName"));
    }
    @RequestMapping(path = "/account-details", method = RequestMethod.POST)
    public Map<String, String> getAccountDetails(@RequestBody Map<String, String> info){
        Map<String, String> accDetails = new HashMap<>();

            accDetails.put("primarySigner", userDao.getSignerBySignerSsn(info.get("primarySigner")).getName());
            if(info.get("secondarySigner") != null && !info.get("secondarySigner").isEmpty()){
                accDetails.put("secondarySigner", userDao.getSignerBySignerSsn(info.get("secondarySigner")).getName());
            }else{

            }


            accDetails.put("accountNickname", userDao.getAccountNickname(info.get("accountNumber")));

        return accDetails;
    }
    @RequestMapping(method = RequestMethod.DELETE)
    public void closeAccount(@RequestBody Map<String, String> info){
        userDao.closeAccount(info.get("accountNumber"));
    }

}
