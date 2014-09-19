package com.moneydroid.app.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.moneydroid.app.R;
import com.moneydroid.app.io.Split;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.sync.SyncHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ashu on 6/7/14.
 */
public class AddExpenseFragment extends Fragment {
    EditText mAmount;
    EditText mDesc;
    EditText mCurrency;
    EditText mPeopleInvolved;
    Button mAddButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);

       mAmount = (EditText)rootView.findViewById(R.id.edittext_amount);
       mCurrency = (EditText)rootView.findViewById(R.id.edittext_currency);
       mDesc = (EditText)rootView.findViewById(R.id.edittext_description);
       mPeopleInvolved = (EditText)rootView.findViewById(
               R.id.edittext_people_involved);
       mAddButton = (Button)rootView.findViewById(
               R.id.add_expense);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
        return rootView;
    }

    private void saveExpense() {
        float amount = Float.valueOf(mAmount.getText().toString());
        String desc = mDesc.getText().toString();
        String currency = mCurrency.getText().toString();
        String peopleInvolved = mPeopleInvolved.getText().toString();



        new AsyncTask() {
            @Override
            protected Void doInBackground(Object ... params) {
                List<Split> splits = new LinkedList<Split>();
                Split s1 = new Split();
                s1.userid = "ashu";
                s1.split = 100;
                Split s2 = new Split();
                s2.split = 300;
                s2.userid = "amol";
                splits.add(s1);
                splits.add(s2);
                Transaction t = new Transaction();
                t.desc = "mastkalandar";
                t.currency = "inr";
                t.amount = 400;
                t.splits = splits;
                List<Transaction> transactions = new LinkedList<Transaction>();
                transactions.add(t);
                SyncHelper helper = new SyncHelper(AddExpenseFragment.this.getActivity());
                helper.sendNewTransactions(transactions);
                return null;
            }
        }.execute();


    }
}
