package com.example.fishingtest.Model;

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
    ArrayList<String> comps_registered;
    ArrayList<String> comps_won;
    String accessLevel;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Constructor for Admin with full info
    public User(String uid, String email, String password, String displayName, String imagePath, ArrayList<String> comps_attended, ArrayList<String> comps_registered, ArrayList<String> comps_won, String accessLevel) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.imagePath = imagePath;
        this.comps_attended = comps_attended;
        this.comps_registered = comps_registered;
        this.comps_won = comps_won;
        this.accessLevel = accessLevel;
    }

    // Constructor for user just registered in the system
    public User(String uid, String email, String password, String displayName) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.displayName = displayName;

        this.imagePath = Common.NA;
        this.comps_attended = new ArrayList<>();
        this.comps_registered = new ArrayList<>();
        this.comps_won = new ArrayList<>();
        this.accessLevel = Common.user_member;
    }

    // Constructor for users starting registering competition(s)
    public User(String uid, String email, String password, String displayName, ArrayList<String> comps_registered) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.comps_registered = comps_registered;

        this.comps_attended = new ArrayList<>();
        this.comps_won = new ArrayList<>();
        this.accessLevel = Common.user_member;
    }

    // Constructor for users starting attending competition(s)
    public User(String uid, String email, String password, String displayName, String imagePath, ArrayList<String> comps_attended, ArrayList<String> comps_registered) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.imagePath = imagePath;
        this.comps_attended = comps_attended;
        this.comps_registered = comps_registered;

        this.comps_won = new ArrayList<>();
    }

    // Constructor for users starting winning competition(s)
    public User(String uid, String email, String password, String displayName, String imagePath, ArrayList<String> comps_attended, ArrayList<String> comps_registered, ArrayList<String> comps_won) {
        this.uid = uid;
        this.displayName = displayName;
        this.password = password;
        this.email = email;
        this.imagePath = imagePath;
        this.comps_attended = comps_attended;
        this.comps_registered = comps_registered;
        this.comps_won = comps_won;

        this.accessLevel = Common.user_member;
    }

    //Check if any ArrayList is null, if yes, instantiate it
    public void checkArrayList(){
        if(this.comps_attended == null)
            comps_attended = new ArrayList<>();

        if(this.comps_registered == null)
            comps_registered = new ArrayList<>();

        if(this.comps_won == null)
            comps_won = new ArrayList<>();
    }

    // Add a new competition registered
    public void addRegComp(String compID){
        if(!this.comps_registered.contains(compID))
            this.comps_registered.add(compID);
    }

    // Add a new competition attended
    public void addAttComp(String compID){
        if(this.comps_attended.contains(compID))
            this.comps_attended.add(compID);
    }

    // Add new competition won
    public void addWonComp(String compID){
        if(!this.comps_won.contains(compID))
            this.comps_won.add(compID);
    }
    // Getters

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ArrayList<String> getComps_attended() {
        return comps_attended;
    }

    public ArrayList<String> getComps_registered() {
        return comps_registered;
    }

    public ArrayList<String> getComps_won() {
        return comps_won;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    // Setters

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setComps_attended(ArrayList<String> comps_attended) {
        this.comps_attended = comps_attended;
    }

    public void setComps_registered(ArrayList<String> comps_registered) {
        this.comps_registered = comps_registered;
    }

    public void setComps_won(ArrayList<String> comps_won) {
        this.comps_won = comps_won;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

}
