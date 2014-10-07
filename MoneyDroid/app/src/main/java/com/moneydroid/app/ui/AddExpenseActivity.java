package com.moneydroid.app.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.moneydroid.app.R;

/**
 * Created by ashu on 7/10/14.
 */
public class AddExpenseActivity extends ActionBarActivity {

    public static String DISPLAY_BACK_BUTTON;

    private Callback mCallback;

    public interface Callback{
        public void onSaveClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        mCallback = (Callback)getSupportFragmentManager()
                .findFragmentById(R.id.add_expense_fragement);

        boolean displayBack = getIntent().getBooleanExtra(DISPLAY_BACK_BUTTON, false);
        // there's no back when launched from launcher
        if(displayBack) {
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        } else {
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        getSupportActionBar().setCustomView(R.layout.add_expense_title);
        Button saveButton = (Button) getSupportActionBar().getCustomView().findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSaveClicked();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}