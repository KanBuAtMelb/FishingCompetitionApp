package com.example.fishingtest.Model;

import java.sql.Timestamp;

public class Post{
    public String postId;
    public String userId;
    public String compId;
    public String oriDownloadUrl;
    public String meaDownloadUrl;
    public String measuredData;
    public String timeStamp;
    public double longitude;
    public double latitude;

    public Post() {
    }

    public Post(String postId, String userId, String compId, String oriDownloadUrl, String meaDownloadUrl, String measuredData, String timeStamp, double longitude, double latitude) {
        this.postId = postId;
        this.userId = userId;
        this.compId = compId;
        this.oriDownloadUrl = oriDownloadUrl;
        this.meaDownloadUrl = meaDownloadUrl;
        this.measuredData = measuredData;
        this.timeStamp = timeStamp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getMeasuredData() {
        return measuredData;
    }
}
