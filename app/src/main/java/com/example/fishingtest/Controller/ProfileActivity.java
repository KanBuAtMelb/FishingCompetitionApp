package com.example.fishingtest.Controller;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    Button button1;
    Button button2;

    DatabaseReference databaseUser;

    TextView email;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        button1 = (Button) findViewById(R.id.edit_profile);
        button2 = (Button) findViewById(R.id.game_record);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        email = (TextView) findViewById(R.id.email_address);
        username = (TextView) findViewById(R.id.user_name);

        //display user name and email address
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String a = user.getEmail();
                String b = user.getDisplayName();
                email.setText("Email: " + a);
                username.setText("Name: " + b);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.game_record:
                //TODO: Add Game_Record Intent
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
