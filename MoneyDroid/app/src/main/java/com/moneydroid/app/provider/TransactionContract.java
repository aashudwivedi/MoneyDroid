package com.moneydroid.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ashu on 5/4/14.
 */
public class TransactionContract {
    public static final String CONTENT_AUTHORITY = "com.moneydroid.app.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRANSACTIONS = "transactions";

    public static final String PATH_SPLITS = "splits";

    protected interface TransactionColumns {
        String TRANSACTION_ID = "transaction_id";
        String AMOUNT = "transaction_amount";
        String CURRENCY = "currency";
        String DESC = "transaction_desc";
        String IS_TRANSIENT= "is_transient";
    }

    public interface SplitsColumns {
        String SPLIT_ID = "split_id";
        String TRANSACTION_ID = "transaction_id";
        String USER_ID = "user_id";
        String SHARE = "split";

    }

    public static class Transactions implements TransactionColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.moneydroid.transaction";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.moneydroid.transaction";

        public static Uri buildTransactionUri(int transactionId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(transactionId)).build();
        }
    }

    public static class Splits implements SplitsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPLITS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.moneydroid.split";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.moneydroid.split";

        public Uri buildSplitUri(int splitId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(splitId)).build();
        }
    }

    public static String getIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

}
