package com.example.fishingtest.Model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Post{
    public String userId;
    public String compId;
    public String oriDownloadUrl;
    public String meaDownloadUrl;
    public String measuredData;
    public String timeStamp;

    public Post() {
    }

    public Post(String userId, String compId, String oriDownloadUrl, String meaDownloadUrl, String measuredData, String timeStamp) {
        this.userId = userId;
        this.compId = compId;
        this.oriDownloadUrl = oriDownloadUrl;
        this.meaDownloadUrl = meaDownloadUrl;
        this.measuredData = measuredData;
        this.timeStamp = timeStamp;
    }
}
