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
    }

    public TransactionsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE" + Tables.TRANSACTIONS + " ("
                + BaseColumns._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TransactionColumns.TRANSACTION_ID + " TEXT NOT NULL,"
                + TransactionColumns.TITLE + " TEXT NOT NULL,"
                + TransactionColumns.AMOUNT + "INTEGER NOT NULL,"
                + TransactionColumns.DESC + "TEXT,"
                //+ TransactionColumns.IS_EQUAL_SPLIT + "INTEGER"
                //+ TransactionColumns.PEOPLE_INVOLVED + " TEXT,"
                + "UNIQUE (" + TransactionColumns.TRANSACTION_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
