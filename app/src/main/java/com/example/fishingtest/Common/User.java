package com.example.fishingtest.Common;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {
    String uid;
    String email;
    String password;
    String displayName;
    String imagePath;
    ArrayList<String> comps_attended;
    ArrayList<String> comps_enrolled;
    ArrayList<String> comps_won;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String email, String password, String displayName, String imagePath, ArrayList<String> comps_attended, ArrayList<String> comps_enrolled, ArrayList<String> comps_won) {
        this.uid = uid;
        this.displayName = displayName;
        this.password = password;
        this.email = email;
        this.imagePath = imagePath;
        this.comps_attended = comps_attended;
        this.comps_enrolled = comps_enrolled;
        this.comps_won = comps_won;
    }

    public User(String uid, String email, String password, String displayName) {
        this.uid = uid;
        this.displayName = displayName;
        this.password = password;
        this.email = email;
        this.comps_attended = new ArrayList<>();
//        comps_attended.add("Test1");
//        comps_attended.add("Test2");
        this.comps_enrolled = new ArrayList<>();
        this.comps_won = new ArrayList<>();
    }
}
