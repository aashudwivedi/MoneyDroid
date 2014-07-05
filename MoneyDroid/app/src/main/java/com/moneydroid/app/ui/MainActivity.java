package com.moneydroid.app.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.moneydroid.app.R;
import com.moneydroid.app.provider.TransactionContract;

import static com.moneydroid.app.util.LogUtils.LOGE;
import static com.moneydroid.app.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements
        ActionBar.TabListener, ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;

    private static final String TAG = makeLogTag(MainActivity.class);

    public static final String ACCOUNT_TYPE = "com.moneydroid";

    public static final String ACCOUNT = "dummyaccount";

    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        if(mViewPager != null) {
            mViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.add)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.expense_list)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.summery)
                .setTabListener(this));

        mAccount = createSyncAccount(this);
        requestImmediateSync();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private static Account createSyncAccount(Context context) {
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        if(accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {
            LOGE(TAG, "account is not created");
        }
        return newAccount;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private class HomePagerAdapter extends FragmentPagerAdapter {
       public HomePagerAdapter(FragmentManager fm) {
           super(fm);
       }

       @Override
       public Fragment getItem(int position) {
           switch (position) {
               case 0:
                   return new AddExpenseFragment();
               case 1:
                   return new ExpenseListFragment();
               case 2:
                   return new ExpenseSummeryFragment();
           }
           return null;
       }

        public int getCount() {return 3;}
   }

    public void requestImmediateSync() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.setSyncAutomatically(mAccount, TransactionContract.CONTENT_AUTHORITY, true);


        if (ContentResolver.isSyncPending(mAccount, TransactionContract.CONTENT_AUTHORITY)  ||
                ContentResolver.isSyncActive(mAccount, TransactionContract.CONTENT_AUTHORITY)) {
            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(mAccount, TransactionContract.CONTENT_AUTHORITY);
        }

        ContentResolver.requestSync(mAccount, TransactionContract.CONTENT_AUTHORITY, settingsBundle);

    }
}
