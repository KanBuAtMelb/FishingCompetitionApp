package com.example.fishingtest.Controller;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseReference databaseUser;
    private FirebaseAuth mAuth;
    private EditText oldpaw;
    private EditText newpaw;
    private EditText conpaw;
    private EditText email;
    private Button button1;
    private String password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        oldpaw = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        newpaw = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        conpaw = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);
        email = (EditText) findViewById(R.id.resetpwd_edit_name);
        button1 = (Button) findViewById(R.id.resetpwd_btn_sure);
        button1.setOnClickListener(EditProfileActivity.this);

        //Display email address
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String a = user.getEmail();
                password1 = user.getPassword();
                email.setText("Email Address: " + a);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        attemptReset();
    }

    public void attemptReset() {
//        String userID = mAuth.getInstance().getCurrentUser().getUid();
//        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        String mOldpaw = oldpaw.getText().toString();
        String mNewpaw = newpaw.getText().toString();
        String mConpaw = conpaw.getText().toString();

        if (mOldpaw.equals(password1) && mNewpaw.equals(mConpaw) && mNewpaw.length() > 6) {
//                Log.d("Password1: ", password1);
//                Log.d("Password2: ", mNewpaw);
//                Log.d("Password3: ", mConpaw);
            databaseUser.child("password").setValue(mNewpaw);
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfileActivity.this);
            dialog.setTitle("");
            dialog.setMessage("Please try again.");
            dialog.setPositiveButton(android.R.string.ok, null);
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.show();
        }
    }
//    private void attemptReset() {
//        final String mOldpaw = oldpaw.getText().toString();
//        final String mNewpaw = newpaw.getText().toString();
//        final String mConpaw = conpaw.getText().toString();
//
//
//        databaseUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                String oldpaw = user.getPassword();
//
//                Log.d("Password1: ", oldpaw);
//                Log.d("Password2: ", mNewpaw);
//                Log.d("Password3: ", mConpaw);
//
//                if(oldpaw.equals(mOldpaw) && mNewpaw.equals(mConpaw) && mNewpaw.length() > 6){
//
//                    String id = mAuth.getCurrentUser().getUid();
//
//                    User reSetUser = new User(id, user.getEmail(),mNewpaw.trim(),
//                            user.getDisplayName());
//                    databaseUser.setValue(reSetUser);
//
//                    Log.d("New Password: ", user.getPassword());
//                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfileActivity.this);
//                    dialog.setTitle("");
//                    dialog.setMessage("Please try again.");
//                    dialog.setPositiveButton(android.R.string.ok, null);
//                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
//                    dialog.show();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

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

