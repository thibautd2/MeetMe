package com.mti.meetme.Tools;


import android.util.Log;

import com.firebase.client.Firebase;
import com.ibm.watson.developer_cloud.personality_insights.v2.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Trait;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Alex on 12/07/2016.
 */
public class PersonalityInsightsAccess {
    static PersonalityInsights service;

    private static PersonalityInsightsAccess ourInstance = null;

    public static PersonalityInsightsAccess getInstance() {
        if (ourInstance == null || service == null)
            ourInstance = new PersonalityInsightsAccess();

        return ourInstance;
    }

    private PersonalityInsightsAccess() {
        service = new PersonalityInsights();
        service.setUsernameAndPassword("625ed887-b912-4fe8-aa13-d201184652a5", "B8hzkU5NDHdA");
     //   service.setEndPoint("https://gateway.watsonplatform.net/personality-insights/api");
    }

    private void updateFirebasePersonality(List<Trait> traits)
    {
        InterestType artist = new InterestType(Interest.Artist);
        InterestType adventure = new InterestType(Interest.Adventure);
        InterestType emotional = new InterestType(Interest.Emotional);
        InterestType imagination = new InterestType(Interest.Imagination);
        InterestType intellect = new InterestType(Interest.Intellect);
        InterestType liberalism = new InterestType(Interest.Liberalism);
        InterestType openness = new InterestType(Interest.Openness);

        for (Trait trait : traits) {
            if (trait.getChildren() != null) {
                updateFirebasePersonality(trait.getChildren());
            }
            else if (trait.getCategory() != null && trait.getCategory().equals("personality"))
                switch (trait.getId()) {
                    case "Artistic interests":
                        artist.score = trait.getPercentage();
                        break;
                    case "Adventurousness":
                        adventure.score = trait.getPercentage();
                        break;
                    case "Emotionality":
                        emotional.score = trait.getPercentage();
                        break;
                    case "Imagination":
                        imagination.score = trait.getPercentage();
                        break;
                    case "Intellect":
                        intellect.score = trait.getPercentage();
                        break;
                    case "Liberalism":
                        liberalism.score = trait.getPercentage();
                        break;
                    case "Openness":
                        openness.score = trait.getPercentage();
                        break;
                }
        }

        if (artist.score != -1 && adventure.score != -1 && emotional.score != -1 &&
                imagination.score != -1 && intellect.score != -1 && liberalism.score != -1 /*&& openness.score != -1*/)
        {
            String interest = bigest(artist, adventure, emotional, imagination, intellect, liberalism, openness).interest.toString();

            // FacebookUser.getInstance().setInterest(interest);
            Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());

            Map<String, Object> desc = new HashMap<>();
            desc.put("interest", interest);

            ref.updateChildren(desc, null);
            return;
        }
    }

    private InterestType bigest(InterestType... interests)
    {
        InterestType bigestScore = interests[0];

        for (int i = 1; i < interests.length ; i++)
            if (bigestScore.score < interests[i].score)
                bigestScore = interests[i];

            return bigestScore;
    }

    private class InterestType {
        public Interest interest;
        public double score = -1;

        public InterestType(Interest interest) {
            this.interest = interest;
        }
    }

    public enum Interest
    {
        Artist ("Artiste"),
        Adventure ("Aventurié"),
        Emotional ("Sensible"),
        Imagination ("Imaginatif"),
        Intellect ("Intellectuelle"),
        Liberalism ("Tolérant"),
        Openness ("Ouvert d'esprit");

        private String interest = "";
        Interest(String name){
            this.interest = name;
        }

        public String toString(){
            return interest;
        }
    }

    //call this function with the text of the user messages to update his profile on firebase
    public void updatePersonality(final String text)
    {
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    Profile profile = service.getProfile(text).execute();
                    updateFirebasePersonality(profile.getTree().getChildren());

                    Log.e("peronality", "success: " + profile.toString());
                }
                catch (Exception e)
                {
                    Log.e("personality", "error: " + e.getMessage() + ", " + e.getCause() + ", " + e.toString() );
                }
            }
        });

        thread.start();
    }
}
