package com.example.fishingtest.Controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Adapter.CompListAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddCompActivity extends AppCompatActivity {


    // Competition UI
    EditText cName;
    EditText cReward;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    EditText cGeo;
    Spinner cType;
    EditText cDescription;
    Button cAdd;

    // Firebase
    DatabaseReference databaseComps;
    ArrayList<Competition> compList;
    ArrayList<String> attendants = new ArrayList<>();
    ArrayList<String> winners = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_competition);

        ArrayList<String> winners = new ArrayList<>();


        // Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");

        cName = (EditText) findViewById(R.id.add_comp_name);
        cReward = (EditText) findViewById(R.id.add_comp_reward);
        cDate= (EditText) findViewById(R.id.add_comp_date);
        cStartTime= (EditText) findViewById(R.id.add_comp_start_time);
        cStopTime= (EditText) findViewById(R.id.add_comp_stop_time);
        cType = (Spinner) findViewById(R.id.add_comp_type);
        cGeo= (EditText) findViewById(R.id.add_comp_geo);
        cDescription = (EditText) findViewById(R.id.add_comp_description);



        cAdd = (Button) findViewById(R.id.admin_button_addComp);

        // Click the "Add Comp" button
        cAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Add a new competition to Firebase
                add_comp();

                // Empty text in each TextView
                clear_textview();
            }
        });
    }


    private void add_comp() {

        // TODO: Check input format, espeically date and time, dollar value and geo info
        String name = cName.getText().toString().trim();
        String date = cDate.getText().toString().trim();
        String reward = cReward.getText().toString().trim();
        String startT = cStartTime.getText().toString().trim();
        String stopT = cStopTime.getText().toString().trim();
        String type = cType.getSelectedItem().toString().trim();
        String geo = cGeo.getText().toString().trim();
        String description = cDescription.getText().toString().trim();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(date)||TextUtils.isEmpty(startT)||TextUtils.isEmpty(stopT)){

            Toast.makeText(this, "Please enter a competition name and date and times", Toast.LENGTH_SHORT).show();

        }else{

            String id = databaseComps.push().getKey();

            Competition comp = new Competition(id,name,reward,date,startT,stopT,geo,type,description);
            databaseComps.child(id).setValue(comp);
        }

    }

    private void clear_textview(){
        cName.setText(Common.EMPTY);
        cDate.setText(Common.EMPTY);
        cReward.setText(Common.EMPTY);
        cStartTime.setText(Common.EMPTY);
        cStopTime.setText(Common.EMPTY);
        cType.setSelection(0);
        cGeo.setText(Common.EMPTY);
        cDescription.setText(Common.EMPTY);
    }

}
