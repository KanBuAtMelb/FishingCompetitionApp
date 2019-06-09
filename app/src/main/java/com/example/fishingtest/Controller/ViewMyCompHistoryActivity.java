package com.example.fishingtest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.fishingtest.Adapter.MyCompHistoryAdapter;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * The controller for the "View My Competitions" activity
 * after the "View My Competition" buttion clicked on the "Profile" page.
 */

public class ViewMyCompHistoryActivity extends AppCompatActivity {

    final static String TAG = "My Competition History";

    // Local variables
    MyCompHistoryAdapter hAdapter;
    ArrayList<Competition> comps;
    DatabaseReference databaseComps;
    User currentUser;

    // UI views
    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_comp_history);

        toolbar = findViewById(R.id.profile_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(TAG);

        // Get a reference to recyclerView
        recyclerView =  findViewById(R.id.profile_recyclerView_comps);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Initialize the Comp List and the Recycler View Adapter
        comps = new ArrayList<>();
        hAdapter = new MyCompHistoryAdapter(comps, ViewMyCompHistoryActivity.this);

        // Get the current user for the adapter to judge competition-won results
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                currentUser.checkArrayList();
                hAdapter.setCurrentUser(currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get the Comp List for the Recycler View adapter
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the Comp List by the previous Firebase update
                hAdapter.clearCompList();

                for(DataSnapshot compSnapshot : dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);

                    // Only find competitions exist in User's attended list
                    if(currentUser != null){
                        if(currentUser.getComps_attended().contains(comp.getCompID())){
                            hAdapter.addComp(comp);
                        }
                    }

                }

                // Sort the Comp List by date and the notify the adapter of the changes caused by sorting
                hAdapter.sortByDate();
                hAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Set Recycler View adaptor
        recyclerView.setAdapter(hAdapter);

        // Set up floating action button OnClick listener
        fab = findViewById(R.id.floating_button_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if an Item have been selected
                if(hAdapter.row_index >= 0){
                    Intent myIntent = new Intent(ViewMyCompHistoryActivity.this, ViewCompDetailsActivity.class);
                    startActivity(myIntent);
                }
                else{
                    Toast.makeText(ViewMyCompHistoryActivity.this, "Please select a competition for full detail review", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
