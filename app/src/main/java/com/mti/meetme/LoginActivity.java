package com.mti.meetme;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.MalformedURLException;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.mti.meetme.Model.User;

public class LoginActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            init_connection();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        User bob = new User("bob", "dilan");
        new CreateUser().execute(bob);
    }

    public class CreateUser extends AsyncTask<User, Void, Void>
    {
        @Override
        protected Void doInBackground(User... params) {
            if(params!=null && params.length>0)
                mUsers.insert(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

     public void init_connection() throws MalformedURLException {
        mClient = new MobileServiceClient(
                "https://meetmee.azurewebsites.net",
                this);
        mUsers = mClient.getTable(User.class);
    }
}
