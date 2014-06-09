package com.moneydroid.app;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;
import com.moneydroid.app.io.RestClient;
import com.moneydroid.app.io.Transaction;
import retrofit.RestAdapter;

import com.moneydroid.app.io.RestClient.*;

import java.util.List;

public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test, container, false);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    RestAdapter restAdapter = RestClient.getAdapter();
                    UserTransactions userTransactions = restAdapter.create(UserTransactions.class);

                    List<Transaction> transactions = userTransactions.transactions("ashu");

                    for(Transaction transaction: transactions) {
                        Log.d("ashu", transaction.title);
                    }
                }
            });
            thread.start();

            TextView helloTextView = (TextView)rootView.findViewById(R.id.helloTextView);
            helloTextView.setText("hi");

            return rootView;
        }
    }

}
