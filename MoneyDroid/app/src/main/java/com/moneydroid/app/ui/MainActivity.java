package com.moneydroid.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.moneydroid.app.R;
import com.moneydroid.app.sync.SyncHelper;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements
        ActionBar.TabListener, ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;

    private static final String TAG = makeLogTag(MainActivity.class);

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
                .setText(R.string.expense_list)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.summery)
                .setTabListener(this));

        SyncHelper.requestImmediateSync(this);
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
        if(id == R.id.perform_sync) {
            SyncHelper.requestSyncDebuggable(this);
        }
        return super.onOptionsItemSelected(item);
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
                   return new ExpenseListFragment();
               case 1:
                   return new ExpenseSummeryFragment();
           }
           return null;
       }

        public int getCount() {return 2;}
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
