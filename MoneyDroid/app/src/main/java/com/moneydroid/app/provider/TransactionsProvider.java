package com.moneydroid.app.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import static com.moneydroid.app.util.LogUtils.makeLogTag;
import static com.moneydroid.app.provider.TransactionContract.Transactions;

/**
 * Created by ashu on 5/4/14.
 */
public class TransactionsProvider extends ContentProvider {

    private static final String TAG = makeLogTag(TransactionsProvider.class);

    private TransactionsDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int TRANSACTIONS = 100;
    private static final int TRANSACTIONS_BETWEEN = 101;
    private static final int TRANSACTIONS_ID = 102;
    private static final int TRANSACTION_WITH_USER = 104;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TransactionContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "transactions", TRANSACTIONS);
        matcher.addURI(authority, "transactions/between/*/*", TRANSACTIONS_BETWEEN);
        matcher.addURI(authority, "transactions/*", TRANSACTIONS_ID);
        matcher.addURI(authority, "transactions/user/*", TRANSACTION_WITH_USER);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TransactionsDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        TransactionsDatabase.deleteDatabase(context);
        mOpenHelper = new TransactionsDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRANSACTIONS:
                return Transactions.CONTENT_TYPE;
            case TRANSACTION_WITH_USER:
                return Transactions.CONTENT_TYPE;
            case TRANSACTIONS_ID:
                return Transactions.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
