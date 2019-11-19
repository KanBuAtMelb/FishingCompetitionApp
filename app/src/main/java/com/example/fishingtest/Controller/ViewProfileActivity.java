package com.example.fishingtest.Controller;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

/**
 * Completed by Qin Xu on 8/06/2019.
 *
 * The controller for the "View Personal Profile" activity.
 */

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener{

    // UI views
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
            case R.id.profile_reset_pwd: // Upon "Reset My Password" button clicked

                if(userEmail != null){
                    firebaseAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ViewProfileActivity.this,
                                                "Password link sent to your email", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(ViewProfileActivity.this,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                break;

            case R.id.default_picture: // Upon personal avatar image clicked
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RC_IMAGE_GALLERY);
                break;

            case R.id.profile_comp_history:  // Upon "My Competition History" button clicked
                Intent intent = new Intent(ViewProfileActivity.this, ViewMyCompHistoryActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    // Overridden method for image selection from the mobile photo gallery
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
                    Toast.makeText(ViewProfileActivity.this, "Avatar update completed!", Toast.LENGTH_LONG).show();
                    String url = taskSnapshot.getDownloadUrl().toString();
                    databaseUser.child("imagePath").setValue(url);
                }
            });
         }
    }

    // Overridden method for "Back" button pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewProfileActivity.this, HomePageActivity.class);
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
