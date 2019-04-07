package com.example.fishingtest.Controller;


import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fishingtest.Adapter.CompAdapter;
import com.example.fishingtest.Adapter.DiscAdapter;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DiscoveryFragment extends Fragment {

    private DatabaseReference databaseComps;
    ArrayList<Competition> comps = new ArrayList<>();

    public DiscoveryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Get a reference to recyclerView
        RecyclerView recyclerView =  view.findViewById(R.id.recyclerView_discovery);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        // TODO: Get Competitions enrolled by the user from Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                comps.clear();

                for(DataSnapshot compSnapshot : dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    comps.add(comp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: write something here??
            }
        });

        // Create an adapter
        DiscAdapter dAdapter = new DiscAdapter(comps);
        // Set adaptor
        recyclerView.setAdapter(dAdapter);


        return view;
    }


}
