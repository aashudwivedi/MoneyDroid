package com.moneydroid.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ashu on 10/6/14.
 */
public class SyncService extends Service {

    private static SyncAdapter sSyncAdapter;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (Service.class) {
            if(sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return sSyncAdapter.getSyncAdapterBinder();
    }
}
