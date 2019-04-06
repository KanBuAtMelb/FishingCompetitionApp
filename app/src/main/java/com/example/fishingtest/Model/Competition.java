package com.example.fishingtest.Model;

import java.util.ArrayList;

public class Competition {
    public String compID;
    public String cname;
    public String date;  // TODO: should not be String...
    public String startTime; //TODO: should be not String
    public String stopTime; //TODO: should be not String
    public String status;
    public ArrayList<String> attendants;
    public String results;
    public String winner; //TODO: can be arraylist, fix it later
    public String coordinator; //TODO: should be not String
    public String downloadUrl;
    public String description;
    public int imageId;



    public Competition() {
    }


    public Competition(String compID, String cname, String date, String startTime, String stopTime, String status, String topic, ArrayList<String> attandants, String results, String winner, String coordinator, String downloadUrl, String description, int imageId) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.attendants = attandants;
        this.results = results;
        this.winner = winner;
        this.coordinator = coordinator;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.imageId = imageId;

    }

    public Competition(String compID, String cname, int imageID) {
        this.compID = compID;
        this.cname = cname;
        this.date = "TBC";
        this.startTime = "TBC";
        this.stopTime = "TBC";
        this.status = "TBC";
        this.description = "N/A";
        this.attendants = new ArrayList<>();
        this.results = "TBC";
        this.winner = "TBC";  // should be ArrayList??
        this.coordinator = "TBC";
        this.imageId = imageID;
        this.downloadUrl = "TBC"; // TODO: comp images can be anything?

    }


    // TODO: to be removed...after figuring out how to add and store comp pictures...
    public Competition(String compID, String cname, String date, String startTime, String stopTime, String status, ArrayList<String> attendants, String results, String winners, String coordinator) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.attendants = attendants;
        this.results = results;
        this.winner = winners;
        this.coordinator = coordinator;

    }


}
