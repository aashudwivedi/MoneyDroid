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

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_TRANSACTIONS).build();

    public interface TransactionColumns {
        String TRANSACTION_ID = "transaction_id";
        String AMOUNT = "transaction_amount";
        String TITLE = "transaction_title";
        String DESC = "transaction_desc";
        //String IS_EQUAL_SPLIT = "is_equal_split";
        // String PEOPLE_INVOLVED = "people_involved";
    }

    public static class Transactions implements TransactionColumns, BaseColumns {
        public static final String TRANSACTION_TYPE_GENERIC = "generic";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.moneydroid.transaction";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.moneydroid.transaction";
    }
}
