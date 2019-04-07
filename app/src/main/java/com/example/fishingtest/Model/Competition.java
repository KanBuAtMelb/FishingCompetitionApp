package com.example.fishingtest.Model;

import java.util.ArrayList;

public class Competition {
    String compID;
    String cname;
    String date;  // TODO: should not be String...?
    String startTime; //TODO: should be not String?
    String stopTime; //TODO: should be not String?
    String geo_map; //TODO: should be not String
    ArrayList<String> attendants;
    String results;
    ArrayList<String> winners; //TODO: can be arraylist, fix it later
    String comp_type;
    String cDescription;
    String image_url;
    String cStatus;  //TODO: maybe be required in case a comp was planned but cancelled?

    public Competition() {
    }

    // Constructor with ID only
    public Competition(String compID) {
        this.compID = compID;

        this.cname = Common.NA;
        this.date = Common.NA;
        this.startTime = Common.NA;
        this.stopTime = Common.NA;
        this.geo_map = Common.NA;
        this.attendants = new ArrayList<>();
        this.results = Common.NA;
        this.winners = new ArrayList<>();
        this.comp_type = Common.NA;
        this.cDescription = Common.NA;
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
    }

    //Constructor with name and date only
    public Competition(String compID, String cname, String date, String startTime, String stopTime) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;

        this.geo_map = Common.NA;
        this.attendants = new ArrayList<>();
        this.results = Common.NA;
        this.winners = new ArrayList<>();
        this.comp_type = Common.NA;
        this.cDescription = Common.NA;
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
    }

    // Constructor with default system image
    public Competition(String compID, String cname, String date, String startTime, String stopTime, String geo_map, ArrayList<String> attendants, String results, ArrayList<String> winners, String comp_type, String cDescription) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.geo_map = geo_map;
        this.attendants = attendants;
        this.results = results;
        this.winners = winners;
        this.comp_type = comp_type;
        this.cDescription = cDescription;

        this.image_url = Common.NA;
        this.cStatus = Common.NA;
    }


    // Constructor with all info
    public Competition(String compID, String cname, String date, String startTime, String stopTime, String geo_map, ArrayList<String> attendants, String results, ArrayList<String> winners, String cDescription, String image_url, String cStatus) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.geo_map = geo_map;
        this.attendants = attendants;
        this.results = results;
        this.winners = winners;
        this.cDescription = cDescription;
        this.image_url = image_url;
        this.cStatus = cStatus;
    }

    // Check if any Arraylist is null after taken from Firebase
    public void checkArrayList(){
        if(this.attendants == null)
            this.attendants = new ArrayList<>();
        if(this.winners == null)
            this.winners = new ArrayList<>();
    }


    // Add user to attendant list
    public void addAttendant(String userID){
        if(!this.attendants.contains(userID))
            this.attendants.add(userID);
    }

    // Add user to winner list
    public void addWinner(String userID){
        if (!this.winners.contains(userID))
            this.winners.add(userID);
    }


    // Getters
    public String getCompID() {
        return compID;
    }

    public String getCname() {
        return cname;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getGeo_map() {
        return geo_map;
    }

    public ArrayList<String> getAttendants() {
        return attendants;
    }

    public String getResults() {
        return results;
    }

    public ArrayList<String> getWinners() {
        return winners;
    }

    public String getcDescription() {
        return cDescription;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getcStatus() {
        return cStatus;
    }

    public String getComp_type() {
        return comp_type;
    }

    // Setters
    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public void setGeo_map(String geo_map) {
        this.geo_map = geo_map;
    }

    public void setAttendants(ArrayList<String> attendants) {
        this.attendants = attendants;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public void setWinners(ArrayList<String> winners) {
        this.winners = winners;
    }

    public void setcDescription(String cDescription) {
        this.cDescription = cDescription;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    public void setComp_type(String comp_type) {
        this.comp_type = comp_type;
    }
}
