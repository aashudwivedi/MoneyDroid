package com.moneydroid.app.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.moneydroid.app.util.AccountUtils;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 13/6/14.
 */
public class BaseActivity extends ActionBarActivity {
    public static final String TAG = makeLogTag(BaseActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!AccountUtils.isAuthenticated(this)) {
            AccountUtils.startAuthenticationFlow(this, getIntent());
            finish();
        }
    }
}
