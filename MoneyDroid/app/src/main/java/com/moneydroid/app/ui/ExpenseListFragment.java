package com.moneydroid.app.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import com.moneydroid.app.R;
import com.moneydroid.app.provider.TransactionContract;

/**
 * Created by ashu on 23/5/14.
 */
public class ExpenseListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    SimpleCursorAdapter mTransactionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTransactionAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_transaction,
                null,
                null,
                null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.expense_list, container, false);
        return view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String selection = null;
        return new CursorLoader(getActivity(),
                TransactionContract.Transactions.CONTENT_URI,
                TransactionQuery.PROJECTION,
                null, // current selection is null, will be changed later
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if(!isAdded()) {
            return;
        }

        mTransactionAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

     private interface TransactionQuery {

        String [] PROJECTION = {
                TransactionContract.TransactionColumns.TRANSACTION_ID,
                TransactionContract.TransactionColumns.DESC,
                TransactionContract.TransactionColumns.CURRENCY,
                TransactionContract.TransactionColumns.AMOUNT
        };
    }
}
