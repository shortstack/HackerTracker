package com.shortstack.hackertracker.Api;

import java.io.IOException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Model.ApiError;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class ApiHelper {

    // API URLs
    public static final String BASE_URL = "http://short-stack.net/api";
    public static final String FIND_DATES = "/date";
    public static final String FIND_SPEAKER = "/speaker";
    public static final String FIND_SPEAKER_BY_DATE = "/speaker/date";
    public static final String FIND_ENTERTAINMENT = "/event";
    public static final String FIND_ENTERTAINMENT_BY_DATE = "/event/date";
    public static final String FIND_CONTEST = "/contest";
    public static final String FIND_CONTEST_BY_DATE = "/contest/date";
    public static final String FIND_VENDOR = "/vendor";


    public static void get(String url, Context context, Type type, AsyncTaskCompleteListener<Object> callback) {
        new ApiRequestAsyncTask(context, type, callback).execute(url, "GET");
    }


    public static void post(String url, String jsonEntity, Context context, Type type,
                            AsyncTaskCompleteListener<Object> callback) {
        new ApiRequestWithEnclosureAsyncTask(context, type, callback).execute(url, "POST", jsonEntity);
    }


    public static void put(String url, String jsonEntity, Context context, Type type,
                           AsyncTaskCompleteListener<Object> callback) {
        new ApiRequestWithEnclosureAsyncTask(context, type, callback).execute(url, "PUT", jsonEntity);
    }

    public static void delete(String url, Context context, Type type, AsyncTaskCompleteListener<Object> callback) {
        new ApiRequestAsyncTask(context, type, callback).execute(url, "DELETE");
    }


    public static Object get(String url, Context context, Type type) {
        return getDelete(url, "GET", type, context);
    }


    public static Object post(String url, String jsonEntity, Context context, Type type) {
        return putPost(url, "POST", jsonEntity, type, context);
    }

    public static Object put(String url, String jsonEntity, Context context, Type type) {
        return putPost(url, "PUT", jsonEntity, type, context);
    }


    public static Object delete(String url, Context context, Type type) {
        return getDelete(url, "DELETE", type, context);
    }

    private static class ApiRequestWithEnclosureAsyncTask extends AsyncTask<String, Void, Object> {

        private Context context;
        private Type type;
        private AsyncTaskCompleteListener<Object> callback;

        public ApiRequestWithEnclosureAsyncTask(Context context, Type type,
                                                AsyncTaskCompleteListener<Object> callback) {
            this.context = context;
            this.type = type;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(String... params) {

            // Parse parameters
            String url = params[0];
            String requestType = params[1];
            String jsonEntity = params[2];

            return putPost(url, requestType, jsonEntity, type, context);
        }

        @Override
        protected void onPostExecute(Object object) {
            callback.onTaskComplete(object);
        }
    }

    private static class ApiRequestAsyncTask extends AsyncTask<String, Void, Object> {

        private Context context;
        private Type type;
        private AsyncTaskCompleteListener<Object> callback;

        public ApiRequestAsyncTask(Context context, Type type, AsyncTaskCompleteListener<Object> callback) {
            this.context = context;
            this.type = type;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(String... params) {

            // Parse parameters
            String url = params[0];
            String requestType = params[1];

            return getDelete(url, requestType, type, context);
        }

        @Override
        protected void onPostExecute(Object object) {
            callback.onTaskComplete(object);
        }
    }

    private static Object putPost(String url, String requestType, String jsonEntity, Type type, Context context) {




        // Create HttpClient
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntityEnclosingRequestBase httpRequest;
        if(requestType.equalsIgnoreCase("POST")) {
            httpRequest = new HttpPost(getAbsoluteUrl(url));
        } else if(requestType.equalsIgnoreCase("PUT")) {
            httpRequest = new HttpPut(getAbsoluteUrl(url));
        } else {
            Log.e("error", "Incorrect HTTP Request was made (" + requestType + "). Expecting POST or PUT");
            throw new IllegalArgumentException("Incorrect HTTP Request was made (" + requestType + "). " +
                    "Expecting POST or PUT");
        }

        // Set Secure Header Information
        Date timestamp = new Date();
        if(url.contains("?")){
            url = url.substring(0, url.indexOf("?"));
        }
        setHttpRequestHeader(httpRequest, timestamp);

        // Create and set JSON Entity
        setHttpRequestEntity(httpRequest, jsonEntity);

        // Execute HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpRequest);
            String responseJson = parseHttpResponse(response);
            Gson gson = new Gson();

            // If an error occurs when interacting with the API
            if(responseJson.contains("\"reason\":")) {
                return gson.fromJson(responseJson, ApiError.class);
            }

            return gson.fromJson(responseJson, type);

        } catch (IOException e) {
            Log.e("error", "Could not execute API HttpRequest.", e);
        } catch (JsonSyntaxException e) {
            Log.e("error", "Could not parse JSON response.", e);
        }

        return null;
    }

    private static Object getDelete(String url, String requestType, Type type, Context context) {


        // Create HttpClient
        HttpClient httpClient = new DefaultHttpClient();
        HttpRequestBase httpRequest;
        if(requestType.equalsIgnoreCase("GET")) {
            httpRequest = new HttpGet(getAbsoluteUrl(url));
        } else if(requestType.equalsIgnoreCase("DELETE")) {
            httpRequest = new HttpDelete(getAbsoluteUrl(url));
        } else {
            Log.e("error", "Incorrect HTTP Request was made (" + requestType + "). Expecting GET or DELETE");
            throw new IllegalArgumentException("Incorrect HTTP Request was made (" + requestType + "). " +
                    "Expecting GET or DELETE");
        }

        // Set Secure Header Information
        Date timestamp = new Date();
        if(url.contains("?")){
            url = url.substring(0, url.indexOf("?"));
        }
        setHttpRequestHeader(httpRequest, timestamp);

        // Execute HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpRequest);

            String responseJson = parseHttpResponse(response);

            Gson gson = new Gson();

            // If an error occurs when interacting with the API
            if(responseJson == null || responseJson.contains("\"reason\":")) {
                return gson.fromJson(responseJson, ApiError.class);
            }

            return gson.fromJson(responseJson, type);

        } catch (IOException e) {
            Log.e("error", "Could not execute API HttpRequest.", e);
        }

        return null;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    private static void setHttpRequestHeader(HttpRequestBase httpRequest, Date timestamp) {
        httpRequest.setHeader("X-Crowdflik-Timestamp", timestamp.getTime() / 1000 + "");
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setHeader("Accept", "application/json");
    }


    private static void setHttpRequestEntity(HttpEntityEnclosingRequestBase httpRequest, String jsonEntity) {
        if(StringUtils.isNotEmpty(jsonEntity)) {
            try {
                StringEntity entity = new StringEntity(jsonEntity, HTTP.UTF_8);
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");

                httpRequest.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                Log.e("error", "Could not convert jsonObject to a StringEntity", e);
            }
        }
    }


    private static String parseHttpResponse(HttpResponse httpResponse) {

        try {
            // Parse Response into a String and convert to JSONObject
            if (httpResponse.getEntity() == null) {
                return null;
            } else {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((httpResponse.getEntity().getContent())));

                String output;
                StringBuilder builder = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    builder.append(output);
                }

                return builder.toString();
            }
        } catch (IOException e) {
            Log.e("error", "Could not read API HttpResponse.", e);
            return null;
        }
    }
}