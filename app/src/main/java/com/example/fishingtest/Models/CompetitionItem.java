package com.example.fishingtest.Models;

import java.util.ArrayList;

public class CompetitionItem {
    public String key;
    public String imageId;
    public String downloadUrl;
    public String description;
    public String timeStamp;
    public ArrayList<User> winners;

    public CompetitionItem(String key) {
        this.key = key;
        this.imageId = "Not Set";
        this.downloadUrl = "Not Set";
        this.description = "Empty Competition Object";
        this.timeStamp = "Not Set";
        this.winners = new ArrayList<User>();
    }

    public CompetitionItem() {
        this.key = "Not Set";
        this.imageId = "Not Set";
        this.downloadUrl = "Not Set";
        this.description = "Empty Competition Object";
        this.timeStamp = "Not Set";
        this.winners = new ArrayList<User>();
    }

    public CompetitionItem(String key, String imageId, String downloadUrl, String description, String timeStamp, ArrayList<User> winners){
        this.key = key;
        this.imageId = imageId;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.timeStamp = timeStamp;
        this.winners = winners;
    }

}
