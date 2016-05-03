package com.mti.meetme;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.pubnub.api.Pubnub;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private User targetUser;
    private User currentUser;

    private ScrollView scrollMessages;
    private LinearLayout messagesLayout;
    private EditText input;
    private Button sendButton;
    boolean test = true;
    public Pubnub pubnub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        targetUser = getIntent().getParcelableExtra("User");
        currentUser = FacebookUser.getInstance();

        bindViews();

        pubnub = new Pubnub("pub-c-f7cbc4e1-aad3-41d5-b85a-9a857617d32a", "sub-c-247762d4-0176-11e6-8916-0619f8945a4f");

        /*ry {
            pubnub.subscribe("demo_tutorial", new Callback() {
                public void successCallback(String channel, Object message) {
                    Log.i("SUBSCRIBE CALLBACK", message.toString());
                }

                public void errorCallback(String channel, PubnubError error) {
                    System.out.println(error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

        pubnub.publish("demo_tutorial", "last", new Callback() {});
        pubnub.publish("demo_tutorial", "message", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.i("PUBLISH callback", message.toString());
            }
        });

        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.i("HISTORY CALLBACK", response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        pubnub.history("demo_tutorial", 100, callback);*/
    }

    private void bindViews()
    {
        scrollMessages = (ScrollView) findViewById(R.id.scrollMessages);
        messagesLayout = (LinearLayout) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.message_input);

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v == sendButton && !input.getText().toString().isEmpty())
        {
            if (test)
                displayMessageIn(input.getText().toString());
            else
                displayMessageOut(input.getText().toString());

            test = !test;
            input.setText("");
        }
    }
}
