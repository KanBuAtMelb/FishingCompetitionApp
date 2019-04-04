package com.example.fishingtest.Common;

import java.util.ArrayList;

public class Competition {
    public String compID;
    public String cname;
    public String date;  // TODO: should not be String...
    public String startTime; //TODO: should be not String
    public String stopTime; //TODO: should be not String
    public String status;
    public String topic;
    public ArrayList<String> attandants;
    public String results;
    public String winner; //TODO: can be arraylist, fix it later
    public String coordinator; //TODO: should be not String
    public String downloadUrl;
    public String description;
    public String imageId;

    public Competition() {
    }


    public Competition(String compID, String cname, String date, String startTime, String stopTime, String status, String topic, ArrayList<String> attandants, String results, String winner, String coordinator, String downloadUrl, String description, String imageId) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.topic = topic;
        this.attandants = attandants;
        this.results = results;
        this.winner = winner;
        this.coordinator = coordinator;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.imageId = imageId;
    }

    public Competition(String compID, String cname) {
        this.compID = compID;
        this.cname = cname;
        this.date = "TBC";
        this.startTime = "TBC";
        this.stopTime = "TBC";
        this.status = "TBC";
        this.topic = "TBC";
        this.description = "N/A";
        this.attandants = new ArrayList<>();
        this.results = "TBC";
        this.winner = "TBC";  // should be ArrayList??
        this.coordinator = "TBC";
        this.imageId = "TBC";
        this.downloadUrl = "TBC"; // TODO: comp images can be anything?
    }


    // TODO: to be removed...after figuring out how to add and store comp pictures...
    public Competition(String compID, String cname, String date, String startTime, String stopTime, String status, String topic, ArrayList<String> attendants, String results, String winners, String coordinator) {
        this.compID = compID;
        this.cname = cname;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.topic = topic;
        this.attandants = attendants;
        this.results = results;
        this.winner = winners;
        this.coordinator = coordinator;
    }


}
