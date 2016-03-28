package com.mti.meetme.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.SplashActivity;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by W_Corentin on 28/03/2016.
 */
public class FacebookHandler
{
    //Temp vars
    private JSONObject fullLikes;
    private ArrayList<String> resultLikes;
    private Context target;
    private boolean likesReady = false;

    public FacebookHandler(Context context)
    {
        target = context;

        resultLikes = new ArrayList<>();
    }

    public void fillFacebookInfos() throws JSONException
    {
        getUserLikes("");
    }

    public void getUserLikes(String next)
    {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/likes",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (fullLikes == null)
                                fullLikes = response.getJSONObject();
                            else {
                                JSONArray array = response.getJSONObject().getJSONArray("data");

                                for (int i = 0; i < array.length(); i++)
                                    fullLikes.getJSONArray("data").put(response.getJSONObject().getJSONArray("data").get(i));

                            }

                            if (!response.getJSONObject().getJSONObject("paging").isNull("next"))
                                getUserLikes(response.getJSONObject().getJSONObject("paging").getJSONObject("cursors").getString("after"));
                            else {
                                FacebookUser.getInstance().setLikes(fullLikes);
                                getLikesPictures();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        if (next == null)
        {
            request.executeAsync();
        }
        else {
            Bundle parameters = new Bundle();
            parameters.putString("after", next);
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public void getLikesPictures() throws JSONException {

        JSONArray likesArray = FacebookUser.getInstance().getLikes().getJSONArray("data");

        ArrayList<String> likesId = new ArrayList<String>();

        for (int i = 0; i < likesArray.length(); i++)
        {
            likesId.add(likesArray.getJSONObject(i).getString("id"));
        }

        getLikesPicturesURL(likesId);
    }

    private void getLikesPicturesURL(final ArrayList<String> likesId)
    {
        for (int i = 0; i < likesId.size(); i++) {
            Bundle params = new Bundle();
            params.putBoolean("redirect", false);

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + likesId.get(i) + "/picture",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback()
                    {
                        public void onCompleted(GraphResponse response) {
                            try {
                                resultLikes.add(response.getJSONObject().getJSONObject("data").getString("url"));

                                if (resultLikes.size() == likesId.size()) {
                                    likesReady = true;

                                    FacebookUser.getInstance().setLikesURL(resultLikes);

                                    Intent intent = new Intent(target, ProfileActivity.class);
                                    target.startActivity(intent);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
            ).executeAsync();
        }
    }
}
