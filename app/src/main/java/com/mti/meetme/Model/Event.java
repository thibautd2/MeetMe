package com.mti.meetme.Model;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by thiba_000 on 05/06/2016.
 */

public class Event implements Serializable {

    @com.google.gson.annotations.SerializedName("name")
    public String name;
    @com.google.gson.annotations.SerializedName("description")
    public String description;
    @com.google.gson.annotations.SerializedName("adresse")
    public String adresse;
    @com.google.gson.annotations.SerializedName("ownerid")
    public String ownerid;
    @com.google.gson.annotations.SerializedName("username")
    public String username;
    @com.google.gson.annotations.SerializedName("visibility")
    public String visibility;
    @com.google.gson.annotations.SerializedName("categorie")
    public String categorie;
    @com.google.gson.annotations.SerializedName("date")
    public String date;
    @com.google.gson.annotations.SerializedName("participants")
    public String participants;
    @com.google.gson.annotations.SerializedName("type")
    public String type;
    @com.google.gson.annotations.SerializedName("latitude")
    public Double latitude;
    @com.google.gson.annotations.SerializedName("longitude")
    public Double longitude;
    @com.google.gson.annotations.SerializedName("invited")
    public String invited;
    @com.google.gson.annotations.SerializedName("endDate")
    public String endDate;

    @com.google.gson.annotations.SerializedName("streamUrl")
    public String streamUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @com.google.gson.annotations.SerializedName("id")
    public String id;

    public String getBaniere() {
        return baniere;
    }

    public void setBaniere(String baniere) {
        this.baniere = baniere;
    }

    @com.google.gson.annotations.SerializedName("baniere")
    public String baniere;

    public Event(String name, String description, String adresse, String ownerid,
                 String username, String visibility, String categorie, String date, String participants,
                 String type, Double latitude, Double longitude, String invited, String endDate,
                 String baniere, String id) {
        this.name = name;
        this.description = description;
        this.adresse = adresse;
        this.ownerid = ownerid;
        this.username = username;
        this.visibility = visibility;
        this.categorie = categorie;
        this.date = date;
        this.participants = participants;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.invited = invited;
        this.endDate = endDate;
        this.baniere = baniere;
        this.id = id;
        this.streamUrl = "";
    }

    public Event(){};

    public Event(String name, String description, String adresse, String ownerid, String visibility, String categorie,
                 String date, String endDate, Double latitude, Double longitude, String username, String type) {
        this.name = name;
        this.description = description;
        this.adresse = adresse;
        this.ownerid = ownerid;
        this.visibility = visibility;
        this.categorie = categorie;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
        this.participants = "";
        this.type = type;
        this.endDate = endDate;
        this.streamUrl= "";
    }



    public String receiveEventId() {
        return "Event :" + name + ownerid;
    }

    public ArrayList<String> receiveParticipants() {
        ArrayList participantsTab = new ArrayList();

        if (participants == null || participants.equals(""))
            return null;

        String str[] = participants.split(";");

        for (String s: str)
            participantsTab.add(s);

        return participantsTab;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStreamUrl() { return streamUrl; }

    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
}