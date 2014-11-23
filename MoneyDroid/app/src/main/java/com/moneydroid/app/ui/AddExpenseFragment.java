package com.moneydroid.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.moneydroid.app.MoneyDroidApplication;
import com.moneydroid.app.R;
import com.moneydroid.app.sync.SyncHelper;

import java.util.Arrays;
import java.util.List;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 6/7/14.
 */
public class AddExpenseFragment extends Fragment implements AddExpenseActivity.Callback{
    private EditText mAmount;
    private EditText mDesc;
    private Spinner mCurrency;
    private Button mSelectFriends;

    private ListView selectedFriendsListView;
    private List<GraphUser> mSelectedUsers = Arrays.asList(new GraphUser[0]);
    private static final String TAG = makeLogTag(AddExpenseFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);

       mAmount = (EditText)rootView.findViewById(R.id.edittext_amount);
       mCurrency = (Spinner)rootView.findViewById(R.id.spinner_currency);
       mDesc = (EditText)rootView.findViewById(R.id.edittext_description);
       mSelectFriends = (Button)rootView.findViewById(R.id.select_friends);

       selectedFriendsListView = (ListView) rootView.findViewById(R.id.selection_list);
       mSelectFriends.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startPickerActivity(FriendPickerActivity.FRIEND_PICKER, 0);
           }
       });
       // Set the list view adapter
       selectedFriendsListView.setAdapter(new ActionListAdapter(getActivity(),
               R.id.selection_list));

       // Check for an open session
       Session session = Session.getActiveSession();
       return rootView;
    }

    private void saveExpense() {
        final float amount = Float.valueOf(mAmount.getText().toString());
        final String desc = mDesc.getText().toString();
        String currency = mCurrency.getSelectedItem().toString();
        final float share = (float)amount / mSelectedUsers.size();
        String users[] = new String[mSelectedUsers.size()];
        for(int i = 0; i < mSelectedUsers.size(); i++) {
            users[i] = mSelectedUsers.get(i).getId();
        }
        SyncHelper.addNewTransaction(getActivity(), amount, desc, currency, share, users);
    }

    @Override
    public void onSaveClicked() {
        saveExpense();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
     /*   super.onSaveInstanceState(bundle);
        for (BaseListElement listElement : graphUsers) {
            listElement.onSaveInstanceState(bundle);
        }
        uiHelper.onSaveInstanceState(bundle);*/
    }

    private class ActionListAdapter extends ArrayAdapter<GraphUser> {

        public ActionListAdapter(Context context, int resourceId) {
            super(context, resourceId, mSelectedUsers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friend_list_item, null);
            }

            GraphUser user = mSelectedUsers.get(position);
            if (user != null) {
                ImageView image = (ImageView) view.findViewById(R.id.image);
                TextView name = (TextView) view.findViewById(R.id.name);
                //TODO set image
                //image.setImageDrawable(user.get);
                name.setText(user.getFirstName());

            }
            return view;
        }

        @Override
        public int getCount() {
            return mSelectedUsers.size();
        }
    }


    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), FriendPickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSelectedUsers = ((MoneyDroidApplication) getActivity()
                .getApplication())
                .getSelectedUsers();
    }
}
