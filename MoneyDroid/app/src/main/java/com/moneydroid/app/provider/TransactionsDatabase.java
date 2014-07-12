package com.moneydroid.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.moneydroid.app.provider.TransactionContract.TransactionColumns;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Helper for Managing {@link android.database.sqlite.SQLiteDatabase} that stores data for
 * {@link com.moneydroid.app.provider.TransactionsProvider}
 */
public class TransactionsDatabase extends SQLiteOpenHelper {
    final String TAG = makeLogTag(TransactionsDatabase.class);

    public static final String DATABASE_NAME = "transactions.db";

    public static final int DATABASE_VERSION = 1;

    private final Context mContext;

    interface Tables {
        String TRANSACTIONS = "transactions";
        String SHARES = "shares";
    }

    public TransactionsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.TRANSACTIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TransactionColumns.TRANSACTION_ID + " INTEGER NOT NULL,"
                + TransactionColumns.AMOUNT + " INTEGER NOT NULL,"
                + TransactionColumns.DESC + " TEXT,"
                + TransactionColumns.CURRENCY + " TEXT )");
                //+ "UNIQUE (" + TransactionColumns.TRANSACTION_ID + ") )");

        db.execSQL("CREATE TABLE " + Tables.SHARES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TransactionContract.SplitsColumns.SPLIT_ID + " INGETER NOT NULL,"
                + TransactionContract.SplitsColumns.TRANSACTION_ID + " INTEGER NOT NULL,"
                + TransactionContract.SplitsColumns.USER_ID + " STRING NOT NULL,"
                + TransactionContract.SplitsColumns.SHARE + " INTEGER NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
