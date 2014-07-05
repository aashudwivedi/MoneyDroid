package com.moneydroid.app.sync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import com.moneydroid.app.io.RestClient;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.util.LogUtils;
import retrofit.RestAdapter;

import java.util.List;

import static com.moneydroid.app.util.LogUtils.LOGD;

/**
 * Created by ashu on 10/6/14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = LogUtils.makeLogTag(SyncAdapter.class);

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context,autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        /*LOGD(TAG, "onPerformSyncCalled");
        RestAdapter restAdapter = RestClient.getAdapter();
        RestClient.UserTransactions userTransactions = restAdapter.create(
                RestClient.UserTransactions.class);

        List<Transaction> transactions = userTransactions.transactions("ashu");

        for(Transaction transaction: transactions) {
            Log.d("ashu", transaction.title);
        }*/
    }
}
