package com.mti.meetme.Model;


import org.joda.time.LocalDate;
import org.joda.time.Years;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by thiba_000 on 26/02/2016.
 */
public class User implements Serializable {


    @com.google.gson.annotations.SerializedName("Gender")
    private String Gender;

    @com.google.gson.annotations.SerializedName("Latitude")
    private Double Latitude;
    @com.google.gson.annotations.SerializedName("Longitude")
    private Double Longitude;

    @com.google.gson.annotations.SerializedName("Email")
    private String Email;

    @com.google.gson.annotations.SerializedName("Birthday")
    private String Birthday; //Format : MM/DD/YYYY

    @com.google.gson.annotations.SerializedName("Name")
    private String Name;

    @com.google.gson.annotations.SerializedName("AgeRange")
    private String AgeRange;

    @com.google.gson.annotations.SerializedName("Description")
    private String Description;

    @com.google.gson.annotations.SerializedName("Uid")
    private String Uid;

    @com.google.gson.annotations.SerializedName("Pic1")
    private String Pic1;

    @com.google.gson.annotations.SerializedName("Pic2")
    private String Pic2;

    @com.google.gson.annotations.SerializedName("Pic3")
    private String Pic3;

    @com.google.gson.annotations.SerializedName("Pic4")
    private String Pic4;

    @com.google.gson.annotations.SerializedName("Pic5")
    private String Pic5;

    @com.google.gson.annotations.SerializedName("Likes")
    private String LikesString;

    @com.google.gson.annotations.SerializedName("Friends")
    private String FriendsString;

    private JSONObject Likes;
    private JSONObject Friends;

    private ArrayList<String> likesId = null;
    private ArrayList<String> friendsId = null;


    public User(String ageRange, String uid, String name, String birthday, String description, String email, String pic1, String gender) {
        Name = name;
        Birthday = birthday;
        Description = description;
        Uid = uid;
        Pic1 = pic1;
        Gender = gender;
        Email = email;
        AgeRange = ageRange;
        Longitude = null;
        Latitude = null;
    }

    public User() {}

    public int convertBirthdayToAge()
    {
        /*AgeRange = "23";
        if (Birthday != null && !Birthday.isEmpty()) {
            try {
                LocalDate birthdate = new LocalDate(Integer.parseInt(Birthday.split("/")[2]), //YYYY
                        Integer.parseInt(Birthday.split("/")[0]), //MM
                        Integer.parseInt(Birthday.split("/")[1])); //DD
                LocalDate now = new LocalDate();
                return Years.yearsBetween(birthdate, now).getYears();
            }
            catch (NoClassDefFoundError e)
            {
                return 23;
            }*/
        return 23;
    }

    public String getLikesString() {
        return LikesString;
    }

    public void setLikesString(String likesString) {
        LikesString = likesString;
    }

    public String getFriendsString() {
        return FriendsString;
    }

    public void setFriendsString(String friendsString) {
        FriendsString = friendsString;
    }

    public ArrayList<String> getLikesID() {
        return likesId;
    }

    public ArrayList<String> getFriendsID() {
        return friendsId;
    }

    public void setLikesId(ArrayList<String> likesId)
    {
        this.likesId = likesId;
    }

    public void setFriendsId(ArrayList<String> friendsId)
    {
        this.friendsId = friendsId;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public JSONObject getFriends() { return Friends; }

    public void setFriends(JSONObject friends) { this.Friends = friends; }

    public JSONObject getLikes() { return Likes; }

    public void setLikes(JSONObject likes) { this.Likes = likes; }


    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAgeRange() {
        return AgeRange;
    }

    public void setAgeRange(String ageRange) {
        AgeRange = ageRange;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) { Uid = uid; }

    public String getPic1() {
        return Pic1;
    }

    public void setPic1(String pic1) {
        Pic1 = pic1;
    }

    public String getPic2() {
        return Pic2;
    }

    public void setPic2(String pic2) {
        Pic2 = pic2;
    }

    public String getPic3() {
        return Pic3;
    }

    public void setPic3(String pic3) {
        Pic3 = pic3;
    }

    public String getPic4() {
        return Pic4;
    }

    public void setPic4(String pic4) {
        Pic4 = pic4;
    }

    public String getPic5() {
        return Pic5;
    }

    public void setPic5(String pic5) {
        Pic5 = pic5;
    }
}
