package com.moneydroid.app.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.moneydroid.app.R;
import com.moneydroid.app.io.Split;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.sync.SyncHelper;
import com.moneydroid.app.util.PrefUtils;

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
       mCurrency.setText(PrefUtils.getCurrency(this.getActivity()));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
        return rootView;
    }

    private void saveExpense() {
        final float amount = Float.valueOf(mAmount.getText().toString());
        final String desc = mDesc.getText().toString();
        String currency = mCurrency.getText().toString();
        String peopleInvolved = mPeopleInvolved.getText().toString();

        final String people[] = peopleInvolved.split(",");
        final float share = (float)amount / people.length;



        new AsyncTask() {
            @Override
            protected Void doInBackground(Object ... params) {
                List<Split> splits = new LinkedList<Split>();
                for(String person : people) {
                    Split split = new Split();
                    split.userid = person;
                    // TODO: make split a float
                    split.split = (int)share;
                    splits.add(split);
                }
                Transaction t = new Transaction();
                t.desc = desc;
                t.currency = PrefUtils.getCurrency(AddExpenseFragment.this.getActivity());
                t.amount = amount;
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
