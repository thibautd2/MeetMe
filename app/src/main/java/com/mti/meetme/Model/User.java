package com.mti.meetme.model;

/**
 * Created by thiba_000 on 26/02/2016.
 */
public class User {

    public User(String username, String password) {
        Username = username;
        Password = password;
    }
    public User(String username) {
        Username = username;
    }


    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImg_perso() {
        return img_perso;
    }

    public void setImg_perso(String img_perso) {
        this.img_perso = img_perso;
    }

    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    @com.google.gson.annotations.SerializedName("Username")
    private String Username;

    @com.google.gson.annotations.SerializedName("Description")
    private String Description;

    @com.google.gson.annotations.SerializedName("Password")
    private String Password;

    @com.google.gson.annotations.SerializedName("Img_perso")
    private String img_perso;

}
