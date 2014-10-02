package com.moneydroid.app.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.moneydroid.app.R;
import com.moneydroid.app.util.PrefUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.moneydroid.app.util.LogUtils.LOGD;
import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 13/6/14.
 */
public class AccountActivity extends FragmentActivity {

    public static final String TAG = makeLogTag(AccountActivity.class);

    public static final String EXTRA_FINISH_INTENT = "com.moneydroid.app.EXTRA_FINISH_INTENT";

    private Intent mFinishIntent;

    private UiLifecycleHelper mFbUiHelper;

    private Fragment mSignInFragment;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFbUiHelper = new UiLifecycleHelper(this, callback);
        mFbUiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FINISH_INTENT)) {
            mFinishIntent = intent.getParcelableExtra(EXTRA_FINISH_INTENT);
        }

        mSignInFragment = new SignInMainFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_container, mSignInFragment, "signin_main")
                .commit();
    }

    public void onSessionStateChange(Session session, SessionState sessionState, Exception e) {
        LOGD(TAG, "Facebook session callback called");
        if (sessionState.isOpened()) {
            LOGD(TAG, "Facebook session is opened");
            getSupportFragmentManager().beginTransaction()
                    .remove(mSignInFragment)
                    .add(R.id.root_container, new DefaultCurrencyChooserFragment(), "default_currency")
                    .commit();
        }
    }

    public static class SignInMainFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Session.getActiveSession().close();
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_login_main, container, false);

            LoginButton loginButton = (LoginButton) rootView.findViewById(
                    R.id.authButton);
            loginButton.setReadPermissions(Arrays.asList("public_profile",
                    "friends_about_me"));
            return rootView;
        }
    }

    public static class DefaultCurrencyChooserFragment extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final Activity activity = getActivity();

            final ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_currency_chooser, container, false);
            final Button finishBtn = (Button)rootView.findViewById(R.id.btnFinish);

            finishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Spinner defaultCurrency = (Spinner)rootView.findViewById(
                            R.id.default_currency_spinner);
                    PrefUtils.setCurrency(DefaultCurrencyChooserFragment.this.getActivity(),
                            defaultCurrency.getSelectedItem().toString());
                    ((AccountActivity)activity).finishSetup();

                }
            });
            return rootView;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mFbUiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFbUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFbUiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFbUiHelper.onSaveInstanceState(outState);
    }

    private void finishSetup() {
        finish();
        startActivity(mFinishIntent);
    }
}
