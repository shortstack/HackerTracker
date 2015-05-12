package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class PartyList extends ApiBase {

    private Party[] parties;
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Party[] getParty() {
        return parties;
    }

    public void setParty(Party[] parties) {
        this.parties = parties;
    }

}