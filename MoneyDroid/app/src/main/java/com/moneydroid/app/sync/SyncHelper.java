package com.moneydroid.app.sync;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Handler;
import android.os.RemoteException;
import com.moneydroid.app.io.RestClient;
import com.moneydroid.app.io.Split;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.io.Transactions;
import com.moneydroid.app.provider.TransactionContract;
import com.moneydroid.app.provider.TransactionContract.SplitsColumns;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 10/6/14.
 */
public class SyncHelper {
    private Context mContext;

    private static final String LOG_TAG = makeLogTag(SyncHelper.class);

    public SyncHelper(final Context context) {
        mContext = context;
    }

    private void buildBatchOperationsFromTransaction(
            List<ContentProviderOperation> batch, List<Transaction> transactions) {
        for(Transaction transaction: transactions) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(TransactionContract.Transactions.CONTENT_URI);
            builder.withValue(TransactionContract.Transactions.TRANSACTION_ID, transaction.id);
            builder.withValue(TransactionContract.Transactions.AMOUNT, transaction.amount);
            builder.withValue(TransactionContract.Transactions.DESC, transaction.desc);
            builder.withValue(TransactionContract.Transactions.CURRENCY, transaction.currency);
            batch.add(builder.build());

            builder = ContentProviderOperation.newInsert(
                    TransactionContract.Splits.CONTENT_URI);
            for(Split split: transaction.splits) {
                builder.withValue(SplitsColumns.SPLIT_ID, split.Id);
                builder.withValue(SplitsColumns.TRANSACTION_ID, transaction.id);
                builder.withValue(SplitsColumns.USER_ID, split.userid);
                builder.withValue(SplitsColumns.SHARE, split.split);
            }
            batch.add(builder.build());
        }
    }

    public void performSync() {
        RestAdapter restAdapter = RestClient.getAdapter();
        RestClient.UserTransactions userTransactions = restAdapter.create(
                RestClient.UserTransactions.class);
        Transactions transactions = userTransactions.getTransactions();

        ArrayList<ContentProviderOperation> batch =
                new ArrayList<ContentProviderOperation>();

        // delete all the shares first followed by all transactions
        batch.add(ContentProviderOperation.newDelete(
                TransactionContract.Splits.CONTENT_URI).build());
        batch.add(ContentProviderOperation.newDelete(
                TransactionContract.Transactions.CONTENT_URI).build());

        buildBatchOperationsFromTransaction(batch, transactions.results);

        try {
            mContext.getContentResolver().applyBatch(
                    TransactionContract.CONTENT_AUTHORITY, batch);
        } catch(OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendNewTransactions(List<Transaction> transactions) {
        RestAdapter restAdapter = RestClient.getAdapter();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        RestClient.UserTransactions userTransactions = restAdapter.create(
                RestClient.UserTransactions.class);
        for(Transaction t: transactions) {
            userTransactions.addTransaction(t, new Callback<JSONObject>() {
                @Override
                public void success(JSONObject jsonObject, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public List<Transaction> getTransactions() {
        return null;
    }
}
