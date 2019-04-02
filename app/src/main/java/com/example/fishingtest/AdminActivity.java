package com.example.fishingtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Models.Competition;
import com.example.fishingtest.Models.CompetitionItem;
import com.example.fishingtest.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    // UI
    EditText uName;
    EditText uEmail;
    EditText uPw;
//    EditText uComp_attended;
    Button uAdd;


    EditText cName;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    EditText cTopic;
    Spinner cStatus;
    EditText cWinners;
    EditText cResult;
    EditText cGeo;
    Button cAdd;
    // Firebase
    DatabaseReference databaseUsers;
    DatabaseReference databaseComps;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Firebase
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");

        // UI
        uName = (EditText) findViewById(R.id.admin_userName);
        uEmail = (EditText) findViewById(R.id.admin_user_email);
        uPw = (EditText) findViewById(R.id.admin_user_password);
        uAdd = (Button) findViewById(R.id.admin_button_addUser);
//        uComp_attended = (EditText) findViewById(R.id.userprofile_comp_attended);


        cName = (EditText) findViewById(R.id.admin_comp_name);
        cDate= (EditText) findViewById(R.id.admin_comp_date);
        cStartTime= (EditText) findViewById(R.id.admin_comp_start_time);
        cStopTime= (EditText) findViewById(R.id.admin_comp_stop_time);
        cTopic  = (EditText) findViewById(R.id.admin_comp_topic);
        cStatus = (Spinner) findViewById(R.id.admin_comp_status);
        cWinners= (EditText) findViewById(R.id.admin_comp_winner);
        cResult= (EditText) findViewById(R.id.admin_comp_winner);
        cGeo= (EditText) findViewById(R.id.admin_comp_geo);
        cAdd = (Button) findViewById(R.id.admin_button_addComp);

        // Click the "Add User" button
        uAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
        // Click the "Add Comp" button
        cAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addComp();
            }
        });



    }

    private void addComp() {
        String name = cName.getText().toString().trim();
        String date = cDate.getText().toString().trim();
        String startT = cStartTime.getText().toString().trim();
        String stopT = cStopTime.getText().toString().trim();
        String status = cStatus.getSelectedItem().toString().trim();
        String topic = cTopic.getText().toString().trim();
        // TODO; temporary test
        ArrayList<String> attendants = new ArrayList<>();
        attendants.add("Mary for test");
        attendants.add("Ben for test");

        String result = cResult.getText().toString().trim();
        String winner = cWinners.getText().toString().trim();
        String cgeo = cGeo.getText().toString().trim();

        if(!TextUtils.isEmpty(name)){

            String id = databaseUsers.push().getKey();

            Competition comp = new Competition(id,name,date, startT, stopT, status, topic, attendants, result, winner, cgeo);

            databaseComps.child(id).setValue(comp);

        }else{
            Toast.makeText(this, "Please enter a competition name", Toast.LENGTH_SHORT).show();
        }


    }

    private void addUser() {
        String name = uName.getText().toString().trim();
        String email = uEmail.getText().toString().trim();
        String pw = uPw.getText().toString().trim();

        if(!TextUtils.isEmpty(name)){

            String id = databaseUsers.push().getKey();

            User user = new User(id, email,pw, name);
            
            databaseUsers.child(id).setValue(user);

        }else{
            Toast.makeText(this, "Please enter a user name", Toast.LENGTH_SHORT).show();
        }


    }
}
