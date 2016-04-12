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
import com.firebase.client.Firebase;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.User;
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
    private Context callerContext;

    private JSONObject fullLikes;
    private JSONObject fullFriends;
    private JSONObject fullPictures;

    private boolean likesReady;
    private boolean friendsReady;
    private boolean picturesReady;

    private User currentUser;

    public FacebookHandler(Context context)
    {
        callerContext = context;
    }

    public void loadFacebookDataForCurrentUser()
    {
        currentUser = FacebookUser.getInstance();

        fullLikes = null;
        fullFriends = null;
        fullPictures = null;

        likesReady = false;
        picturesReady = false;
        friendsReady = false;

        getUserLikes("");
        getUserFriends("");
        getUserProfilePics("");
    }

    private void switchToMaps()
    {
        FacebookUser.setFacebookUser(currentUser);

        Firebase ref = Network.getAlluser;
        Firebase userRef = ref.child(currentUser.getUid());
        userRef.setValue(currentUser);

        FacebookUser.getInstance().setFriends(fullFriends);
        FacebookUser.getInstance().setLikes(fullLikes);

        Intent intent = new Intent(callerContext, MapsActivity.class);
        callerContext.startActivity(intent);
    }

    /***************************************
    *
    *       CURRENT USER DATA
    *
     ***************************************/

    private void getUserLikes(String next)
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
                                likesReady = true;
                                Log.w("Likes", "READY");
                                if (likesReady && friendsReady && picturesReady)
                                    switchToMaps();
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

    private void getUserFriends(String next)
    {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (fullFriends == null)
                                fullFriends = response.getJSONObject();
                            else {
                                JSONArray array = response.getJSONObject().getJSONArray("data");

                                for (int i = 0; i < array.length(); i++)
                                    fullFriends.getJSONArray("data").put(response.getJSONObject().getJSONArray("data").get(i));
                            }

                            if (!response.getJSONObject().getJSONObject("paging").isNull("next") && response.getJSONObject().getJSONObject("paging").has("cursors"))
                                getUserFriends(response.getJSONObject().getJSONObject("paging").getJSONObject("cursors").getString("after"));
                            else {
                                friendsReady = true;
                                Log.w("Friends", "READY");
                                if (likesReady && friendsReady && picturesReady)
                                    switchToMaps();
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

    private void getUserProfilePics(String next)
    {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                currentUser.getUid() + "/photos/uploaded",
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (fullPictures == null)
                                fullPictures = response.getJSONObject();
                            else {
                                JSONArray array = response.getJSONObject().getJSONArray("data");

                                for (int i = 0; i < array.length(); i++)
                                    fullPictures.getJSONArray("data").put(response.getJSONObject().getJSONArray("data").get(i));
                            }

                            if (!response.getJSONObject().getJSONObject("paging").isNull("next"))
                                getUserProfilePics(response.getJSONObject().getJSONObject("paging").getJSONObject("cursors").getString("after"));
                            else {
                                int nbPics = 0;
                                JSONArray array = fullPictures.getJSONArray("data");

                                for (int i = 0; i < array.length() && nbPics < 5; i++)
                                {
                                    JSONObject picture = array.getJSONObject(i);
                                    String album_name = picture.getJSONObject("album").optString("name");

                                    if (album_name.compareTo("Profile Pictures") == 0) {
                                        String url = picture.optString("source");

                                        if (nbPics == 0)
                                            currentUser.setPic1(url);
                                        else if (nbPics == 1)
                                            currentUser.setPic2(url);
                                        else if (nbPics == 2)
                                            currentUser.setPic3(url);
                                        else if (nbPics == 3)
                                            currentUser.setPic4(url);
                                        else if (nbPics == 4)
                                            currentUser.setPic5(url);


                                        nbPics++;
                                    }
                                }

                                picturesReady = true;
                                Log.w("Pictures", "READY");
                                if (likesReady && friendsReady && picturesReady)
                                    switchToMaps();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        if (next == null)
        {
            Bundle parameters = new Bundle();
            parameters.putString("fields", "source,album");
            request.setParameters(parameters);
            request.executeAsync();
        }
        else {
            Bundle parameters = new Bundle();
            parameters.putString("after", next);
            parameters.putString("fields", "source,album");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }
    /*********************************************
     *
     *          OTHER USER'S DATA
     *
     ********************************************/

    private void handleFriendsInCommon(User user) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + user.getUid().split(":")[1] + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response)
                    {

                    }
                }
        ).executeAsync();
    }

    private ArrayList<String> getFriendsInCommonPictures(JSONObject friends) throws JSONException {

        if(friends != null) {
            JSONArray friendsArray = friends.getJSONArray("data");

            ArrayList<String> friendsId = new ArrayList<String>();
            ArrayList<String> friendsIdCurrent = new ArrayList<String>();

            for (int i = 0; i < friendsArray.length(); i++) {
                friendsId.add(friendsArray.getJSONObject(i).getString("id"));
            }

            for (int i = 0; i < FacebookUser.getInstance().getFriends().length(); i++) {
                friendsIdCurrent.add(FacebookUser.getInstance().getFriends().getJSONArray("data").getJSONObject(i).getString("id"));
            }

            friendsId.retainAll(friendsIdCurrent);
            return friendsId;
        }

        return null;
    }
}
