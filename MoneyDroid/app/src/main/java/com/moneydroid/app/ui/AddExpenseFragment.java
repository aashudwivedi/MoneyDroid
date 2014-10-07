package com.moneydroid.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.OpenGraphAction;
import com.moneydroid.app.MoneyDroidApplication;
import com.moneydroid.app.R;
import com.moneydroid.app.io.Split;
import com.moneydroid.app.io.Transaction;
import com.moneydroid.app.sync.SyncHelper;
import com.moneydroid.app.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.moneydroid.app.util.LogUtils.makeLogTag;

/**
 * Created by ashu on 6/7/14.
 */
public class AddExpenseFragment extends Fragment implements AddExpenseActivity.Callback{
    private EditText mAmount;
    private EditText mDesc;
    private EditText mCurrency;
    private EditText mPeopleInvolved;
    private Button mAddButton;

    private ListView selectedFrieldsListView;
    private List<BaseListElement> selectedListElements;

    private static final String TAG = makeLogTag(AddExpenseFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);

       mAmount = (EditText)rootView.findViewById(R.id.edittext_amount);
       mCurrency = (EditText)rootView.findViewById(R.id.edittext_currency);
       mDesc = (EditText)rootView.findViewById(R.id.edittext_description);
       mAddButton = (Button)rootView.findViewById(
               R.id.add_expense);
       mCurrency.setText(PrefUtils.getCurrency(this.getActivity()));

       mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });

       selectedFrieldsListView = (ListView) rootView.findViewById(R.id.selection_list);

       // Set up the list view items, based on a list of
       // BaseListElement items
       selectedListElements = new ArrayList<BaseListElement>();
       // Add an item for the friend picker
       selectedListElements.add(new PeopleListElement(0));
       // Set the list view adapter
       selectedFrieldsListView.setAdapter(new ActionListAdapter(getActivity(),
               R.id.selection_list, selectedListElements));

       // Check for an open session
       Session session = Session.getActiveSession();
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

    @Override
    public void onSaveClicked() {
        Toast.makeText(getActivity(), "save clicked", Toast.LENGTH_LONG).show();
        saveExpense();
    }

    public class PeopleListElement extends BaseListElement {
        private List<GraphUser> selectedUsers;
        private static final String FRIENDS_KEY = "friends";

        public PeopleListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.abc_ab_bottom_solid_dark_holo),
                    getActivity().getResources().getString(R.string.action_people),
                    getActivity().getResources().getString(R.string.action_people_default),
                    requestCode);
        }

        private byte[] getByteArray(List<GraphUser> users) {
            // convert the list of GraphUsers to a list of String
            // where each element is the JSON representation of the
            // GraphUser so it can be stored in a Bundle
            List<String> usersAsString = new ArrayList<String>(users.size());

            for (GraphUser user : users) {
                usersAsString.add(user.getInnerJSONObject().toString());
            }
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new ObjectOutputStream(outputStream).writeObject(usersAsString);
                return outputStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, "Unable to serialize users.", e);
            }
            return null;
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedUsers != null) {
                bundle.putByteArray(FRIENDS_KEY,
                        getByteArray(selectedUsers));
            }
        }

        private List<GraphUser> restoreByteArray(byte[] bytes) {
            try {
                @SuppressWarnings("unchecked")
                List<String> usersAsString =
                        (List<String>) (new ObjectInputStream
                                (new ByteArrayInputStream(bytes)))
                                .readObject();
                if (usersAsString != null) {
                    List<GraphUser> users = new ArrayList<GraphUser>
                            (usersAsString.size());
                    for (String user : usersAsString) {
                        GraphUser graphUser = GraphObject.Factory
                                .create(new JSONObject(user),
                                        GraphUser.class);
                        users.add(graphUser);
                    }
                    return users;
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (IOException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (JSONException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            }
            return null;
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
            if (bytes != null) {
                selectedUsers = restoreByteArray(bytes);
                setUsersText();
                return true;
            }
            return false;
        }

        private void setUsersText() {
            String text = null;
            if (selectedUsers != null) {
                // If there is one friend
                if (selectedUsers.size() == 1) {
                    text = String.format(getResources()
                                    .getString(R.string.single_user_selected),
                            selectedUsers.get(0).getName());
                } else if (selectedUsers.size() == 2) {
                    // If there are two friends
                    text = String.format(getResources()
                                    .getString(R.string.two_users_selected),
                            selectedUsers.get(0).getName(),
                            selectedUsers.get(1).getName());
                } else if (selectedUsers.size() > 2) {
                    // If there are more than two friends
                    text = String.format(getResources()
                                    .getString(R.string.multiple_users_selected),
                            selectedUsers.get(0).getName(),
                            (selectedUsers.size() - 1));
                }
            }
            if (text == null) {
                // If no text, use the placeholder text
                text = getResources()
                        .getString(R.string.action_people_default);
            }
            // Set the text in list element. This will notify the
            // adapter that the data has changed to
            // refresh the list view.
            setText2(text);
        }

        @Override
        protected void onActivityResult(Intent data) {
            selectedUsers = ((MoneyDroidApplication) getActivity()
                    .getApplication())
                    .getSelectedUsers();
            setUsersText();
            notifyDataChanged();
        }

        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do nothing for now
                    startPickerActivity(FriendPickerActivity.FRIEND_PICKER, getRequestCode());
                }
            };
        }

        @Override
        protected void populateOGAction(OpenGraphAction action) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
     /*   super.onSaveInstanceState(bundle);
        for (BaseListElement listElement : listElements) {
            listElement.onSaveInstanceState(bundle);
        }
        uiHelper.onSaveInstanceState(bundle);*/
    }

    private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listElements;

        public ActionListAdapter(Context context, int resourceId,
                                 List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
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

            BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    if (listElement.getText2() != null) {
                        text2.setVisibility(View.VISIBLE);
                        text2.setText(listElement.getText2());
                    } else {
                        text2.setVisibility(View.GONE);
                    }
                }
            }
            return view;
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
        /*if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        } else */if (resultCode == Activity.RESULT_OK) {
            // Do nothing for now
            selectedListElements.get(0).onActivityResult(data);
        }
    }
}
