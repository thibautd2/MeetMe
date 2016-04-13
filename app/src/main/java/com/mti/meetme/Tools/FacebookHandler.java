package com.mti.meetme.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.firebase.client.Firebase;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

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
    private boolean isComparison;
    private boolean firstTime;

    private User currentUser;

    public FacebookHandler(Context context)
    {
        callerContext = context;

        likesReady = false;
        picturesReady = false;
        friendsReady = false;
        isComparison = false;
    }

    public FacebookHandler(User user)
    {
        currentUser = user;

        likesReady = false;
        picturesReady = false;
        friendsReady = false;
        isComparison = true;
        firstTime = false;
    }

    public User loadUserCommonData() throws InterruptedException, JSONException {
        if (currentUser.getLikes() != null)
            currentUser.setLikesId(getLikesInCommonId(currentUser.getLikes()));

        if (currentUser.getFriends() != null)
            currentUser.setFriendsId(getFriendsInCommonId(currentUser.getFriends()));

        return currentUser;
    }

    public void loadFacebookDataForCurrentUser()
    {
        currentUser = FacebookUser.getInstance();

        fullLikes = null;
        fullFriends = null;
        fullPictures = null;

        getUserLikes("");
        getUserFriends("");
        getUserProfilePics("");
    }

    private void switchToMaps() throws JSONException {
        FacebookUser.setFacebookUser(currentUser);

        FacebookUser.getInstance().setFriendsString(fullFriends.toString());
        FacebookUser.getInstance().setLikesString(fullLikes.toString());

        Firebase ref = Network.getAlluser;
        Firebase userRef = ref.child(currentUser.getUid());
        userRef.setValue(currentUser);

        FacebookUser.getInstance().setFriends(fullFriends);
        FacebookUser.getInstance().setLikes(fullLikes);
        FacebookUser.getInstance().setLikesId(getLikesId(fullLikes));
        FacebookUser.getInstance().setFriendsId(getFriendsId(fullFriends));

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
                "/" + currentUser.getUid() + "/likes",
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
                "/" + currentUser.getUid() + "/friends",
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

    public ArrayList<String> getFriendsId(JSONObject friends) throws JSONException {

        if (friends != null)
        {
            JSONArray friendsArray = friends.getJSONArray("data");

            ArrayList<String> friendsIdCurrent = new ArrayList<String>();


            for (int i = 0; i < friendsArray.length(); i++) {
                friendsIdCurrent.add(FacebookUser.getInstance().getFriends().getJSONArray("data").getJSONObject(i).getString("id"));
            }


            return friendsIdCurrent;
        }

        return null;
    }

    public ArrayList<String> getLikesId(JSONObject likes) throws JSONException {

        if (likes != null)
        {
            JSONArray likesArray = likes.getJSONArray("data");

            ArrayList<String> likesIdCurrent = new ArrayList<String>();

            for (int i = 0; i < likesArray.length(); i++) {
                likesIdCurrent.add(FacebookUser.getInstance().getLikes().getJSONArray("data").getJSONObject(i).getString("id"));
            }

            return likesIdCurrent;
        }

        return null;
    }

    /*********************************************
     *
     *          OTHER USER'S DATA
     *
     ********************************************/

    public ArrayList<String> getFriendsInCommonId(JSONObject friends) throws JSONException {

        if (friends != null)
        {
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

    public ArrayList<String> getLikesInCommonId(JSONObject likes) throws JSONException {

        if (likes != null)
        {
            JSONArray likesArray = likes.getJSONArray("data");

            ArrayList<String> likesId = new ArrayList<String>();
            ArrayList<String> likesIdCurrent = new ArrayList<String>();

            for (int i = 0; i < likesArray.length(); i++) {
                likesId.add(likesArray.getJSONObject(i).getString("id"));
            }

            for (int i = 0; i < FacebookUser.getInstance().getFriends().length(); i++) {
                likesIdCurrent.add(FacebookUser.getInstance().getLikes().getJSONArray("data").getJSONObject(i).getString("id"));
            }

            likesId.retainAll(likesIdCurrent);

            return likesId;
        }

        return null;
    }
}
