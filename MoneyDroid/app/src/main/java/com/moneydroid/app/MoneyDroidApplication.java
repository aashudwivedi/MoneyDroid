package com.moneydroid.app;

import android.app.Application;

import com.facebook.model.GraphUser;

import java.util.List;

/**
 * Created by ashu on 7/10/14.
 */
public class MoneyDroidApplication extends Application {
    private List<GraphUser> selectedUsers;

    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}
