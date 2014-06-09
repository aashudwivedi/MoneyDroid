package com.moneydroid.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moneydroid.app.R;

/**
 * Created by ashu on 23/5/14.
 */
public class TransactionFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class TransactionAdapter extends CursorAdapter {
        public TransactionAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_transaction,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //final String type = cursor.getString(Tra)
        }
    }
}
