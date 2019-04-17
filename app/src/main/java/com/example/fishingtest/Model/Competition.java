package com.example.fishingtest.Model;

import java.util.ArrayList;

public class Competition {
    private String compID;
    private String cname;
    private String reward;
    private String date;  // Format: "dd/MM/yyyy", default time zone is AEST
    private String startTime; //Format: "dd/MM/yyyy", default time zone is AEST
    private String stopTime; //Format: "dd/MM/yyyy", default time zone is AEST
    private String geo_map; //TODO: should be not String
    private ArrayList<String> attendants;
    private String results;
    private String winner; // Only assume one winner on this design stage
    private String compType;
    private String cDescription;
    private String image_url; // All competitions have the same hard-coded image on this design stage
    private String cStatus;  //TODO: maybe be required in case a comp was planned but cancelled?
    private ArrayList<String> postIDs;

    public Competition() {
    }

    // Constructor with ID only
    public Competition(String compID) {
        this.compID = compID;

        this.cname = Common.NA;
        this.date = Common.NA;
        this.reward = Common.NA;
        this.startTime = Common.NA;
        this.stopTime = Common.NA;
        this.geo_map = Common.NA;
        this.attendants = new ArrayList<>();
        this.results = Common.NA;
        this.winner = Common.NA;
        this.compType = Common.NA;
        this.cDescription = Common.NA;
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
        this.postIDs = new ArrayList<>();
    }

    //Constructor with name and date only
    public Competition(String compID, String cname, String reward, String date, String startTime, String stopTime) {
        this.compID = compID;
        this.cname = cname;
        this.reward = reward;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;

        this.geo_map = Common.NA;
        this.attendants = new ArrayList<>();
        this.results = Common.NA;
        this.winner = Common.NA;
        this.compType = Common.NA;
        this.cDescription = Common.NA;
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
        this.postIDs = new ArrayList<>();
    }

    // Constructor without attendants, winner, result and status
    public Competition(String compID, String cname, String reward, String date, String startTime, String stopTime, String geo_map,  String compType, String cDescription) {
        this.compID = compID;
        this.cname = cname;
        this.reward = reward;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.geo_map = geo_map;
        this.compType = compType;
        this.cDescription = cDescription;


        this.attendants = new ArrayList<>();
        this.results = Common.NA;
        this.winner = Common.NA;
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
        this.postIDs = new ArrayList<>();
    }

    // Constructor without attendants and status
    public Competition(String compID, String cname, String reward, String date, String startTime, String stopTime, String geo_map, String results, String winner, String compType, String cDescription) {
        this.compID = compID;
        this.cname = cname;
        this.reward = reward;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.geo_map = geo_map;
        this.results = results;
        this.winner = winner;
        this.compType = compType;
        this.cDescription = cDescription;

        this.attendants = new ArrayList();
        this.image_url = Common.NA;
        this.cStatus = Common.NA;
        this.postIDs = new ArrayList<>();
    }

    // Constructor with all info
    public Competition(String compID, String cname, String reward, String date, String startTime, String stopTime, String geo_map, ArrayList<String> attendants, String results, String winner, String cDescription, String image_url, String cStatus) {
        this.compID = compID;
        this.cname = cname;
        this.reward = reward;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.geo_map = geo_map;
        this.attendants = attendants;
        this.results = results;
        this.winner = winner;
        this.cDescription = cDescription;
        this.image_url = image_url;
        this.cStatus = cStatus;
        this.postIDs = new ArrayList<>();
    }

    // Check if any Arraylist is null after taken from Firebase
    public void checkArrayList(){
        if(this.attendants == null)
            this.attendants = new ArrayList<>();
        if(this.postIDs == null)
            this.postIDs = new ArrayList<>();
    }


    // Add user to attendant list
    public void addAttendant(String userID){
        if(!this.attendants.contains(userID))
            this.attendants.add(userID);
    }

    // Remove user from attendant list
    public void removeAttendant(String userID){
        if(this.attendants.contains(userID))
            this.attendants.remove(userID);
    }



    // Getters
    public String getCompID() {
        return compID;
    }

    public String getCname() {
        return cname;
    }

    public String getReward(){return reward;}

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

    public String getWinner() { return winner; }

    public String getcDescription() {
        return cDescription;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getcStatus() {
        return cStatus;
    }

    public String getCompType() {
        return compType;
    }

    public ArrayList<String> getPostIDs() {
        return postIDs;
    }

    // Setters
    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCompID(String compID){this.compID = compID;}

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

    public void setWinners(String winner) {
        this.winner = winner;
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

    public void setComp_type(String compType) {
        this.compType = compType;
    }

}
