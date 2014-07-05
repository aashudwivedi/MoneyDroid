package com.moneydroid.app.util;

import android.content.Context;
import android.content.Intent;
import com.facebook.Session;
import com.moneydroid.app.ui.AccountActivity;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 13/6/14.
 */
public class AccountUtils {
    private static final String TAG = makeLogTag(AccountUtils.class);

    public static boolean isAuthenticated(final Context context) {
        if(Session.getActiveSession() == null) {
            return  false;
        } else {
            return Session.getActiveSession().isOpened();
        }
    }

    public static void logOut(final Context context) {
        if(Session.getActiveSession() != null) {
            Session.getActiveSession().close();
        }
    }

    public static void startAuthenticationFlow(final Context context, final Intent finishIntent) {
        Intent loginFlowIntent = new Intent(context, AccountActivity.class);
        loginFlowIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginFlowIntent.putExtra(AccountActivity.EXTRA_FINISH_INTENT, finishIntent);
        context.startActivity(loginFlowIntent);
    }
}
