package com.mti.meetme;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private User targetUser;
    private User currentUser;

    private ScrollView scrollMessages;
    private LinearLayout messagesLayout;
    private EditText input;
    private Button sendButton;

    private Pubnub pubnub;
    private String chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        targetUser = getIntent().getParcelableExtra("User");
        currentUser = FacebookUser.getInstance();

        bindViews();
        initPubNub();
    }

    private void bindViews()
    {
        scrollMessages = (ScrollView) findViewById(R.id.scrollMessages);
        messagesLayout = (LinearLayout) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.message_input);

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

    private void initPubNub()
    {
        pubnub = new Pubnub("pub-c-f7cbc4e1-aad3-41d5-b85a-9a857617d32a", "sub-c-247762d4-0176-11e6-8916-0619f8945a4f");
        chatName = getChatName();

        try {
            pubnub.subscribe(chatName, new Callback() {
                public void successCallback(String channel, Object message)
                {
                    try {
                        final JSONObject messageObj = new JSONObject(message.toString());

                        if (messageObj.getString("sender").compareTo(currentUser.getUid()) != 0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        displayMessageOut(messageObj.getString("text"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                try {
                    final JSONArray history = new JSONArray(response.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fillFromHistory(history.getJSONArray(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        pubnub.history(chatName, 100, callback);
    }

    @Override
    public void onClick(View v) {
        if (v == sendButton && !input.getText().toString().isEmpty())
        {
            JSONObject messageObj = new JSONObject();

            try {
                messageObj.put("sender", currentUser.getUid());
                messageObj.put("text", input.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pubnub.publish(chatName, messageObj, new Callback() {});

            displayMessageIn(input.getText().toString());

            input.setText("");
        }
    }

    private void displayMessageIn(String message)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 5);
        params.gravity = Gravity.END;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        TextView messageView = new TextView(this);
        messageView.setLayoutParams(params);
        messageView.setMaxWidth(size.x / 2);
        messageView.setBackground(getResources().getDrawable(R.drawable.bubble_in));
        messageView.setText(message);

        messagesLayout.addView(messageView);

        scrollMessages.post(new Runnable() { public void run() { scrollMessages.fullScroll(View.FOCUS_DOWN); } });
    }

    private void displayMessageOut(String message)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 5);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        TextView messageView = new TextView(this);
        messageView.setLayoutParams(params);
        messageView.setMaxWidth(size.x / 2);
        messageView.setBackground(getResources().getDrawable(R.drawable.bubble_out));
        messageView.setText(message);

        messagesLayout.addView(messageView);

        scrollMessages.post(new Runnable() { public void run() { scrollMessages.fullScroll(View.FOCUS_DOWN); } });
    }

    private void fillFromHistory(JSONArray messages) throws JSONException
    {
        LinearLayout.LayoutParams paramsIn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsIn.setMargins(0, 0, 0, 5);
        paramsIn.gravity = Gravity.END;

        LinearLayout.LayoutParams paramsOut = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsOut.setMargins(0, 0, 0, 5);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        for (int i = 0; i < messages.length(); i++)
        {
            JSONObject message = (JSONObject) messages.get(i);

            if (message.getString("sender").compareTo(currentUser.getUid()) == 0)
            {
                TextView messageView = new TextView(this);
                messageView.setLayoutParams(paramsIn);
                messageView.setMaxWidth(size.x / 2);
                messageView.setBackground(getResources().getDrawable(R.drawable.bubble_in));
                messageView.setText(message.getString("text"));

                messagesLayout.addView(messageView);
            }
            else
            {
                TextView messageView = new TextView(this);
                messageView.setLayoutParams(paramsOut);
                messageView.setMaxWidth(size.x / 2);
                messageView.setBackground(getResources().getDrawable(R.drawable.bubble_out));
                messageView.setText(message.getString("text"));

                messagesLayout.addView(messageView);
            }
        }

        scrollMessages.post(new Runnable() { public void run() { scrollMessages.fullScroll(View.FOCUS_DOWN); } });
    }

    private String getChatName()
    {
        if (currentUser.getUid().compareTo(targetUser.getUid()) <= 0)
            return currentUser.getUid() + "-" + targetUser.getUid();
        else
            return targetUser.getUid() + "-" + currentUser.getUid();
    }
}
