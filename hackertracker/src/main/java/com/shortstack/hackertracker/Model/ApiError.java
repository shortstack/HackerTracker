package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 3/29/14.
 */

public class ApiError {

    private String status;
    private String reason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}