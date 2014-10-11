package com.moneydroid.app.sync;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.moneydroid.app.io.RestClient;
import com.moneydroid.app.io.Split;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.io.Transactions;
import com.moneydroid.app.provider.TransactionContract;
import com.moneydroid.app.provider.TransactionContract.SplitsColumns;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.provider.BaseColumns._ID;
import static com.moneydroid.app.provider.TransactionContract.SplitsColumns.SHARE;
import static com.moneydroid.app.provider.TransactionContract.SplitsColumns.SPLIT_ID;
import static com.moneydroid.app.provider.TransactionContract.SplitsColumns.USER_ID;
import static com.moneydroid.app.provider.TransactionContract.Transactions.AMOUNT;
import static com.moneydroid.app.provider.TransactionContract.Transactions.CURRENCY;
import static com.moneydroid.app.provider.TransactionContract.Transactions.DESC;
import static com.moneydroid.app.provider.TransactionContract.Transactions.IS_TRANSIENT;
import static com.moneydroid.app.provider.TransactionContract.Transactions.TRANSACTION_ID;
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

    public void performSync() {
        RestAdapter restAdapter = RestClient.getAdapter();
        RestClient.UserTransactions userTransactions = restAdapter.create(
                RestClient.UserTransactions.class);
        Transactions transactions = userTransactions.getTransactions();

        ArrayList<ContentProviderOperation> batch =
                new ArrayList<ContentProviderOperation>();

        String transactionProjection [] = {
            _ID, TRANSACTION_ID, IS_TRANSIENT };

        String splitsProjection[] =   {
            SPLIT_ID };

        // fetch everything which is not transient, and overwrite with the
        // server's copy
         Cursor c = mContext.getContentResolver().query(
                TransactionContract.Transactions.CONTENT_URI, transactionProjection,
                null,
                null,
                null);

        final int transactionIdIndex = c.getColumnIndexOrThrow(TRANSACTION_ID);
        final int isTransientIndex = c.getColumnIndexOrThrow(IS_TRANSIENT);
        final HashSet<Integer> transientIds = new HashSet<Integer>();
        final HashSet<Integer> nonTransientIds = new HashSet<Integer>();

        Log.d(LOG_TAG, "all rows fetched, count = " + c.getCount());
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if(c.getInt(isTransientIndex) == 0) {
                nonTransientIds.add(c.getInt(transactionIdIndex));
            } else {
                transientIds.add(c.getInt(transactionIdIndex));
            }
        }
        c.close();

        Log.d(LOG_TAG, "transient ids = " + transientIds);
        Log.d(LOG_TAG, "non-transient ids = " + nonTransientIds);

        for(Transaction transaction: transactions.results) {
            if(nonTransientIds.contains(transaction.id)) {
                // nothing has changed locally simply overwrite the local copy with server data
                Log.d(LOG_TAG, "overwriting id = " + transaction.id  + "with server's copy");
                // TODO: implement Etags based checking, once backend supports to avoid this
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(TransactionContract.Transactions.CONTENT_URI);
                builder.withValue(TRANSACTION_ID, transaction.id);
                builder.withValue(AMOUNT, transaction.amount);
                builder.withValue(DESC, transaction.desc);
                builder.withValue(CURRENCY, transaction.currency);
                builder.withSelection( TRANSACTION_ID + " = ?",
                        new String[] {String.valueOf(transaction.id)});
                batch.add(builder.build());


                nonTransientIds.remove(transaction.id);

                Map<Integer, Split> serversSplits = new HashMap<Integer, Split>();
                for(Split s : transaction.splits) {
                    serversSplits.put(s.Id, s);
                }

                c = mContext.getContentResolver().query(
                        TransactionContract.Splits.CONTENT_URI, splitsProjection,
                        SplitsColumns.TRANSACTION_ID + " == ?",
                        new String[]{String.valueOf(transaction.id)}, null);

                // cross check all the locally exiting split ids for this transaction
                int splitIdIndex = c.getColumnIndex(SPLIT_ID);
                for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    int splitId = c.getInt(splitIdIndex);
                    if(!serversSplits.containsKey(c.getInt(splitIdIndex))) {
                        builder = ContentProviderOperation
                                .newDelete(TransactionContract.Splits.CONTENT_URI);
                        builder.withSelection(TransactionContract.Splits.SPLIT_ID + " = ?",
                                new String[] {String.valueOf(splitId)});
                        batch.add(builder.build());
                        serversSplits.remove(splitId);
                    } else {
                        Split s = serversSplits.get(splitId);
                        builder = ContentProviderOperation
                                .newUpdate(TransactionContract.Splits.CONTENT_URI);
                        builder.withValue(SplitsColumns.SPLIT_ID, s.Id);
                        builder.withValue(SplitsColumns.SHARE, s.split);
                        builder.withValue(SplitsColumns.USER_ID, s.userid);
                        builder.withValue(SplitsColumns.TRANSACTION_ID, s.transactionId);
                        batch.add(builder.build());
                        serversSplits.remove(splitId);
                    }
                }

                c.close();

                for(Split s : serversSplits.values()) {
                    builder = ContentProviderOperation
                            .newInsert(TransactionContract.Splits.CONTENT_URI);
                    builder.withValue(SplitsColumns.SPLIT_ID, s.Id);
                    builder.withValue(SplitsColumns.SHARE, s.split);
                    builder.withValue(SplitsColumns.USER_ID, s.userid);
                    builder.withValue(SplitsColumns.TRANSACTION_ID, s.transactionId);
                    batch.add(builder.build());
                }

            } else if(transientIds.contains(transaction.id)) {
                // local modification exists, decide based on time of change

            } else {
                // its a new transaction from server
                Log.d(LOG_TAG, "a new transaction found on server t-id = " + transaction.id);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(TransactionContract.Transactions.CONTENT_URI);
                builder.withValue(TRANSACTION_ID, transaction.id);
                builder.withValue(AMOUNT, transaction.amount);
                builder.withValue(DESC, transaction.desc);
                builder.withValue(CURRENCY, transaction.currency);
                builder.withValue(IS_TRANSIENT, 0);
                batch.add(builder.build());

                builder = ContentProviderOperation.newInsert(
                        TransactionContract.Splits.CONTENT_URI);
                for(Split split: transaction.splits) {
                    builder.withValue(SPLIT_ID, split.Id);
                    builder.withValue(SplitsColumns.TRANSACTION_ID, transaction.id);
                    builder.withValue(USER_ID, split.userid);
                    builder.withValue(SHARE, split.split);
                }
                batch.add(builder.build());
            }

            // these transactions don't exist on server anymore - delete locally as well
            for(int id : nonTransientIds) {
                Log.d(LOG_TAG, "t-id doesn't exist on server anymore deleting = " + transaction.id);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newDelete(TransactionContract.Transactions.CONTENT_URI);
                builder.withSelection(TRANSACTION_ID + " = ?", new String[] {String.valueOf(id)});
                batch.add(builder.build());
            }

        }

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
