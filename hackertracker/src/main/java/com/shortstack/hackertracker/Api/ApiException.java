package com.shortstack.hackertracker.Api;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public class ApiException extends Exception {

    private ApiError apiError;

    public ApiException(String message, ApiError apiError) {
        super(message);
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }

    @Override
    public String getMessage() {
        if (apiError != null && apiError.getErrorMessage() != null) {
            return apiError.getErrorMessage();
        } else {
            return super.getMessage();
        }
    }
}
