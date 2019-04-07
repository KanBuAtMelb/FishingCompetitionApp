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

import com.example.fishingtest.Adapter.CompAdapter;
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


public class CompetitionsFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    ArrayList<Competition> comps_registered;
    ArrayList<String> compIDs_registered;

    private FirebaseUser user;
    private DatabaseReference databaseComps;
    private DatabaseReference databaseUser;

    public CompetitionsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_competitions, container, false);

        // Get a reference to recyclerView
        recyclerView =  view.findViewById(R.id.recyclerView_comps);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        // Create an adapter
        comps_registered = new ArrayList<>();
        final CompAdapter cAdapter = new CompAdapter(comps_registered, getContext());
        // Set adaptor
        recyclerView.setAdapter(cAdapter);


        //Floating button
        fab = (FloatingActionButton) view.findViewById(R.id.floating_button_comp);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // TODO: Check if an Item have been selected
                Intent myIntent = new Intent(getActivity(), ViewCompDetailsActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        // Get Competitions enrolled by the user from Firebase

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        // Get competitions IDs of all registered competitions by the current user
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                compIDs_registered= temp.getComps_registered();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: add something here
            }
        });

        // Find the full details of the registered competition from Firebase database
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    comp.checkArrayList();
                    if(compIDs_registered.contains(comp.getCompID())){
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
