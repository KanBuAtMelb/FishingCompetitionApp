package com.example.fishingtest.Controller;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseReference databaseUser;
    StorageReference userImage;
    Button buttonPassword;
    Button buttonHistory;
    TextView email;
    TextView username;
    TextView attended;
    TextView won;
    TextView upcoming;
    Uri imageUri;
    String userID;
    ImageView profileImage;
    String userEmail;
    static final int RC_IMAGE_GALLERY = 2;

    // Firebase for password reset
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userImage = FirebaseStorage.getInstance().getReference("Comp_Images");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonPassword = (Button) findViewById(R.id.profile_reset_pwd);
        buttonHistory = (Button) findViewById(R.id.profile_comp_history);
        profileImage = (ImageView) findViewById(R.id.default_picture);
        buttonPassword.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        email = (TextView) findViewById(R.id.email_address);
        username = (TextView) findViewById(R.id.user_name);
        attended = (TextView) findViewById(R.id.attended);
        upcoming = (TextView) findViewById(R.id.upcoming);
        won = (TextView) findViewById(R.id.won);

//
//        attended.setOnClickListener(this);
//        upcoming.setOnClickListener(this);
//        won.setOnClickListener(this);
//

        //display user name and email address
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userEmail = user.getEmail();
                email.setText("Email: " + userEmail);
                username.setText("Name: " + user.getDisplayName());

                int c = check(user.getComps_attended()).size();
                int d = check(user.getComps_registered()).size();
                int e = check(user.getComps_won()).size();

                attended.setText("Finished: " + c);
                upcoming.setText("Upcoming: " + d);
                won.setText("Won: " + e);

                if (!user.getImagePath().equals(Common.NA)){
                    Picasso.get().load(user.getImagePath()).into(profileImage);
                }
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
            case R.id.profile_reset_pwd:

                if(userEmail != null){
                    firebaseAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this,
                                                "Password link sent to your email", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(ProfileActivity.this,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }

                break;
            case R.id.default_picture:
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RC_IMAGE_GALLERY);
                break;
            case R.id.profile_comp_history:
                Intent intent = new Intent(ProfileActivity.this, ViewMyCompHistoryActivity.class);
                startActivity(intent);

                break;
            case R.id.attended:
                //TODO: Add Game_Record Intent
                break;
            case R.id.upcoming:
                //TODO: Add Upcoming Competition Intent
            case R.id.won:
                //TODO: Add Reward Record Intent
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("User_Images");
            StorageReference fileRef = imagesRef.child(userID);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ProfileActivity.this, "Avatar update completed!", Toast.LENGTH_LONG).show();
                    String url = taskSnapshot.getDownloadUrl().toString();
                    databaseUser.child("imagePath").setValue(url);
                }
            });
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();

    }

    //Check if any ArrayList is null, if yes, instantiate it
    public ArrayList<String> check(ArrayList<String> arr){
        if (arr==null)
        return new ArrayList<>();
        return arr;

    }

}
