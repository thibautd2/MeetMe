package com.mti.meetme.controller;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.mti.meetme.model.User;

/**
 * Created by thiba_000 on 01/03/2016.
 */
public class Network {

    public static MobileServiceClient getClient() {
        return mClient;
    }

    public static void setClient(MobileServiceClient mClient) {
        Network.mClient = mClient;
    }

    public static MobileServiceTable<User> getUsers() {
        return mUsers;
    }

    public static void setUsers(MobileServiceTable<User> mUsers) {
        Network.mUsers = mUsers;
    }

    private static MobileServiceClient mClient;
    private static MobileServiceTable<User> mUsers;
}
