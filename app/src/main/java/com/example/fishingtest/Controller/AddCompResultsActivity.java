package com.example.fishingtest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Adapter.EditCompListAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddCompResultsActivity extends AppCompatActivity {

    final static String WAIT_FOR_RESULTS_STATUS = "2";


    // UI
    ListView cListView;
    TextView cName;
    TextView cDate;
    TextView cStartTime;
    TextView cStopTime;
    TextView cReward;
    TextView cType;
    Button btn_select_comp;


    // Firebase
    DatabaseReference databaseComps;
    ArrayList<Competition> compList;

    // Competition selected
    String compID;
    String compName;
    String cStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comp_results);

        compID = new String();

        //Fetch "Waiting-for-Results"competitions from Firebase and store into compList
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");

        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                compList.clear();
                for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    if(comp.getcStatus().equals(WAIT_FOR_RESULTS_STATUS)){
                        comp.checkArrayList();
                        if(comp.getWinner().equals(Common.NA) && comp.getResults().equals(Common.NA))
                            compList.add(comp);
                    }

                }

                EditCompListAdapter compListAdapter = new EditCompListAdapter(AddCompResultsActivity.this, compList);
                cListView.setAdapter(compListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // UI
        cListView = (ListView)findViewById(R.id.add_comp_result_comp_list);
        cName =(TextView)findViewById(R.id.add_comp_result_comp_name);
        cDate = (TextView)findViewById(R.id.update_comp_date);
        cStartTime = (TextView)findViewById(R.id.update_comp_start_time);
        cStopTime = (TextView)findViewById(R.id.update_comp_stop_time);
        cReward = (TextView)findViewById(R.id.update_comp_reward);
        cType = (TextView)findViewById(R.id.add_comp_result_comp_type);
        btn_select_comp = (Button)findViewById(R.id.admin_button_select_comp_for_results);

        // Set up List of Competition
        compList = new ArrayList<>();

        cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Competition comp = compList.get(position);

                cName.setText(comp.getCname());
                cDate.setText("Date: " + comp.getDate());
                cStartTime.setText("Start at: "+comp.getStartTime());
                cStopTime.setText("Stop at: " + comp.getStopTime());

                try{
                    cReward.setText("Reward: $" + Integer.toString(comp.getReward()) + " AUD");
                }
                catch(Exception e){
                    Toast.makeText(AddCompResultsActivity.this,"Reward data type incorrect",Toast.LENGTH_SHORT).show();
                };

                String[] compTypes = getBaseContext().getResources().getStringArray(R.array.comp_type);
                cType.setText("Competition Type: "+compTypes[comp.getCompType()]);

                // Hold these invisible values
                compID = comp.getCompID();
                compName = comp.getCname();
                cStatus = comp.getcStatus();     // TODO: DO WE NEED IT?
            }
        });

        // Click "View All the Posts" Buton
        btn_select_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectResultIntent = new Intent(AddCompResultsActivity.this, SelectCompWinnerActivity.class);
                selectResultIntent.putExtra(Common.COMPID,compID);
                selectResultIntent.putExtra(Common.COMPNAME, compName);
                startActivity(selectResultIntent);

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent goBack = new Intent(AddCompResultsActivity.this, HomePageActivity.class);
        startActivity(goBack);
        finish();
    }
}
