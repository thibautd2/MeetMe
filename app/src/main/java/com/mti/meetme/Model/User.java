package com.mti.meetme.Model;


import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by thiba_000 on 26/02/2016.
 */
public class User implements Serializable {

    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    @com.google.gson.annotations.SerializedName("Gender")
    private String Gender;

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

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

    private JSONObject Likes;

    public User() {}

    public User(String ageRange, String uid, String name, String birthday, String description, String email, String pic1, String gender) {
        Name = name;
        Birthday = birthday;
        Description = description;
        Uid = uid;
        Pic1 = pic1;
        Gender = gender;
        Email = email;
        AgeRange = ageRange;
      /*  Longitude = 0.0;
        Latitude = 0.0;*/
    }

    public int convertBirthdayToAge()
    {
        if (Birthday != null) {
            LocalDate birthdate = new LocalDate(Integer.parseInt(Birthday.split("/")[2]), //YYYY
                                                Integer.parseInt(Birthday.split("/")[0]), //MM
                                                Integer.parseInt(Birthday.split("/")[1])); //DD
            LocalDate now = new LocalDate();

            return Years.yearsBetween(birthdate, now).getYears();
        }

        return 0;
    }

    public void setLikes(JSONObject likes) { this.Likes = likes; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
