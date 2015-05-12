package com.shortstack.hackertracker.Utils;

import com.shortstack.hackertracker.Api.ApiError;
import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Model.ApiBase;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public class ApiResponseUtil {

    public static Object parseResponse(ApiBase apiBase, Class type) throws ApiException {

        //if class type is null - set it to Object
        if (type == null) {
            type = Object.class;
        }

        //check if its the correct type - do nothing if it is.
        if (!isInstance(apiBase, type)) { // if not - wrap it w/ ApiException and toss it up
            if (apiBase instanceof ApiError) {
                throw new ApiException("An API error occurred.", (ApiError) apiBase);
            } else if (apiBase != null) {
                throw new ApiException("Error while trying to parse type.", null);
            } else if (apiBase == null) {
                throw new ApiException("Can't communicate with server.", null);
            }
        }

        return apiBase;
    }

    public static boolean isInstance(ApiBase apiBase, Class type) {
        return type.isInstance(apiBase);
    }
}
