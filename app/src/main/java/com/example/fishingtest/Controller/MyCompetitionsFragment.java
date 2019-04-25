package com.example.fishingtest.Controller;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fishingtest.Adapter.MyCompAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyCompetitionsFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    ArrayList<Competition> comps_registered;
    ArrayList<String> compIDs_registered;

    private FirebaseUser user;
    private DatabaseReference databaseComps;
    private DatabaseReference databaseUser;

    public MyCompetitionsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the user from Firebase
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_competitions, container, false);

        // Get a reference to recyclerView
        recyclerView =  view.findViewById(R.id.recyclerView_comps);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        // Create an adapter
        comps_registered = new ArrayList<>();
        final MyCompAdapter cAdapter = new MyCompAdapter(comps_registered, getContext());
        // Set adaptor
        recyclerView.setAdapter(cAdapter);


        //Floating button
        fab = (FloatingActionButton) view.findViewById(R.id.floating_button_comp);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(cAdapter.row_index >= 0){
                    Intent myIntent = new Intent(getActivity(), ViewCompDetailsActivity.class);
                    getActivity().startActivity(myIntent);
                }
                else{
                    Toast.makeText(getContext(), "Please select a competition for full detail review", Toast.LENGTH_SHORT).show();
                }
            }
        });



//        compIDs_registered = new ArrayList<>();
//
//        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
//
//        // Get competitions IDs of all registered competitions by the current user
//        databaseUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User temp = dataSnapshot.getValue(User.class);
//                compIDs_registered= temp.getComps_registered();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // TODO: add something here
//            }
//        });

        // Find the full details of the registered competition from Firebase database
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cAdapter.clearCompList();
                for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    comp.checkArrayList();
                    if(comp.getAttendants().contains(userID)){
                        String compStartAt = comp.getDate().trim() + " " + comp.getStartTime().trim() + " GMT+08:00"; // Competition Time is based on AEST by default
                        long result = Common.timeToCompStart(compStartAt);
                        if(result >= 0.0)
                            cAdapter.addComp(comp);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

}
