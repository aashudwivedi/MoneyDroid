package com.moneydroid.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moneydroid.app.R;

/**
 * Created by ashu on 6/7/14.
 */
public class ExpenseSummeryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_expnse_summery, container, false);
    }
}
