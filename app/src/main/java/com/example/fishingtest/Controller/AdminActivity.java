package com.example.fishingtest.Controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {


    // Competition UI
    EditText cName;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    Spinner cStatus;
    EditText cWinners;
    EditText cResult;
    EditText cGeo;
    EditText cDescription;

    Button cAdd;

    // Firebase
    DatabaseReference databaseUsers;
    DatabaseReference databaseComps;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");


        cName = (EditText) findViewById(R.id.admin_comp_name);
        cDate= (EditText) findViewById(R.id.admin_comp_date);
        cStartTime= (EditText) findViewById(R.id.admin_comp_start_time);
        cStopTime= (EditText) findViewById(R.id.admin_comp_stop_time);
        cStatus = (Spinner) findViewById(R.id.admin_comp_status);
        cWinners= (EditText) findViewById(R.id.admin_comp_winner);
        cResult= (EditText) findViewById(R.id.admin_comp_results);
        cGeo= (EditText) findViewById(R.id.admin_comp_geo);
        cDescription = (EditText) findViewById(R.id.admin_comp_description);


        cAdd = (Button) findViewById(R.id.admin_button_addComp);

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

//        // TODO; temporary test
//        ArrayList<String> attendants = new ArrayList<>();
//        attendants.add("Mary for test");
//        attendants.add("Ben for test");

        String result = cResult.getText().toString().trim();
        String winner = cWinners.getText().toString().trim();
        String cgeo = cGeo.getText().toString().trim();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(date)||TextUtils.isEmpty(startT)||TextUtils.isEmpty(stopT)){

            Toast.makeText(this, "Please enter a competition name and date and times", Toast.LENGTH_SHORT).show();

        }else{
            String id = databaseComps.push().getKey();

            Competition comp = new Competition(id,name,date,startT,stopT);
            databaseComps.child(id).setValue(comp);
        }

    }

}
