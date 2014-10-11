package com.moneydroid.app.sync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import com.moneydroid.app.util.LogUtils;

import static com.moneydroid.app.util.LogUtils.LOGD;

/**
 * Created by ashu on 10/6/14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = LogUtils.makeLogTag(SyncAdapter.class);

    private ContentResolver mContentResolver;
    private SyncHelper mSyncHelper;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
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
        LOGD(TAG, "performing sync");

        if(mSyncHelper == null) {
            mSyncHelper = new SyncHelper(mContext);
        }
        mSyncHelper.performSync();
        LOGD(TAG, "sync done");
    }
}
