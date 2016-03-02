package com.mti.meetme.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by thiba_000 on 26/02/2016.
 */
public class User implements Serializable {

    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    public Boolean getGender() {
        return Gender;
    }

    public void setGender(Boolean gender) {
        Gender = gender;
    }

    @com.google.gson.annotations.SerializedName("Gender")
    private Boolean Gender;


    @com.google.gson.annotations.SerializedName("Latitude")
    private Double Latitude;

    @com.google.gson.annotations.SerializedName("Longitude")
    private Double Longitude;

    @com.google.gson.annotations.SerializedName("Email")
    private String Email;

    @com.google.gson.annotations.SerializedName("Name")
    private String Name;

    @com.google.gson.annotations.SerializedName("Age")
    private int Age;

    @com.google.gson.annotations.SerializedName("Description")
    private String Description;

    @com.google.gson.annotations.SerializedName("AzureID")
    private String AzureID;

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

    public User(String name, int age, String description, String azureID, String email, String pic1, Boolean gender) {
        Name = name;
        Age = age;
        Description = description;
        AzureID = azureID;
        Pic1 = pic1;
        Gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return Age;
    }

    public void setBirthday(int age) {
        Age = age;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAzureID() {
        return AzureID;
    }

    public void setAzureID(String azureID) {
        AzureID = azureID;
    }

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
}
