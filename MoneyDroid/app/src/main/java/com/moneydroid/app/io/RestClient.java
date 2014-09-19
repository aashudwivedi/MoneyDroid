package com.moneydroid.app.io;

import com.moneydroid.app.Config;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.*;

import java.util.List;

/**
 * RestClient to interact with the moneydroid webservice
 */
public class RestClient {
    private static final String API_URL = Config.API_ROOT;

    public interface UserTransactions {
        @GET("/transactions/")
        List<Transaction> getTransactions (@Path("user") String user);

        @POST("/transactions/")
        void addTransaction(@Body Transaction t, Callback<JSONObject> callback);

        @PUT("/transactions/")
        void updateTransaction(@Body Transaction t, Callback<Transaction> cb);
    }

    public static RestAdapter getAdapter() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        return restAdapter;
    }
}