package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class ContestList extends ApiBase {

    private Contest[] contests;
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Contest[] getContest() {
        return contests;
    }

    public void setContest(Contest[] contests) {
        this.contests = contests;
    }

}