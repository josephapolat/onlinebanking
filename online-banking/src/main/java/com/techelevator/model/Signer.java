package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Signer {
    private int signerId;
    private String name;
    private String ssn;

    public Signer(int signerId, String name, String ssn){
        this.name = name;
        this.ssn = ssn;
        this.signerId = signerId;
    }
    @JsonProperty("name")
    public String getName(){return name;}
    @JsonProperty("ssn")
    String getSSN(){return ssn;}
    @JsonProperty("signer_id")
    int getSignerId(){return signerId;}
    public int getSignerIdBackend(){return signerId;}
    public void setName(String name){this.name = name;}
    public void setSSN(String ssn){this.ssn = ssn;}
    public void setSignerId(int signerId){this.signerId = signerId;}
}
