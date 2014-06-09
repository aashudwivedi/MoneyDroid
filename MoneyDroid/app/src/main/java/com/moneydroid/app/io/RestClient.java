package com.moneydroid.app.io;

import com.moneydroid.app.Config;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * RestClient to interact with the moneydroid webservice
 */
public class RestClient {
    private static final String API_URL = Config.API_ROOT + "/moneydroid/api/v1.0";

    public interface UserTransactions {
        @GET("/{user}/transactions")
        List<Transaction> transactions (
            @Path("user") String user
        );
    }

    /*public interface Transaction {
        // although it always returns only a single transaction, i am making it a list because i am not sure how to
        // specify a the replacement in a single variable. // TODO: fix this
        @GET("/transactions/{id}")
        List<Transaction> transactions(@Path("id") String id);
    }*/

    /**
     * for transactions b/w two dates
     */
    public interface TransactionBetween {
        @GET("{user}/transactions/{start_date}/{end_date}")
        List<Transaction> transactions(
                @Path("user") String user,
                @Path("start_date") String startDate,
                @Path("end_date") String endDate
        );
    }

    public static RestAdapter getAdapter() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        return restAdapter;
    }
}