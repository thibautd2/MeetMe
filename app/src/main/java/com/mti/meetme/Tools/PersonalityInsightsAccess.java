package com.mti.meetme.Tools;


import android.util.Log;

import com.ibm.watson.developer_cloud.personality_insights.v2.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Trait;

import java.util.List;


/**
 * Created by Alex on 12/07/2016.
 */
public class PersonalityInsightsAccess {
    PersonalityInsights service = new PersonalityInsights();

    private static PersonalityInsightsAccess ourInstance = new PersonalityInsightsAccess();

    public static PersonalityInsightsAccess getInstance() {
        return ourInstance;
    }

    private PersonalityInsightsAccess() {
        service.setUsernameAndPassword("625ed887-b912-4fe8-aa13-d201184652a5", "B8hzkU5NDHdA");
     //   service.setEndPoint("https://gateway.watsonplatform.net/personality-insights/api");
    }

    private void updateFirebasePersonality(List<Trait> traits)
    {
        for (Trait trait : traits) {
            if (trait.getCategory() == null && trait.getChildren() != null) {
                updateFirebasePersonality(trait.getChildren());
            }
            //todo update firebase
            else if (trait.getCategory().equals("personality"))
                switch (trait.getId()) {
                    case "Artistic interests":
                        break;
                    case "Adventurousness":
                        break;
                    case "Emotionality":
                        break;
                    case "Imagination":
                        break;
                    case "Intellect":
                        break;
                    case "Liberalism":
                        break;
                    case "Openness":
                        break;
                }
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
