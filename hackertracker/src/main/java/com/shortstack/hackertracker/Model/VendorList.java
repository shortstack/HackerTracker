package com.shortstack.hackertracker.Model;

/**
 * Created by Whitney Champion on 4/21/14.
 */
public class VendorList extends ApiBase {

    private Vendor[] vendors;
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Vendor[] getVendor() {
        return vendors;
    }

    public void setVendor(Vendor[] vendors) {
        this.vendors = vendors;
    }

}