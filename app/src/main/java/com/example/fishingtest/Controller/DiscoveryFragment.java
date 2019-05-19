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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.fishingtest.Adapter.DiscAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DiscoveryFragment extends Fragment {

    RadioGroup radioGroup;
    RecyclerView recyclerView;
    DiscAdapter dAdapter;
    FloatingActionButton fab;

    ArrayList<Competition> comps;
    private DatabaseReference databaseComps;

    private FirebaseUser user;


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

        // Get current signed-in user ID
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Radio button
        radioGroup = view.findViewById(R.id.disc_sort_group);


        // Get a reference to recyclerView
        recyclerView =  view.findViewById(R.id.recyclerView_discovery);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        // Create an adapter
        comps = new ArrayList<>();
        dAdapter = new DiscAdapter(comps,getContext());
        // Set adaptor
        recyclerView.setAdapter(dAdapter);



        fab = (FloatingActionButton) view.findViewById(R.id.floating_button_discovery);


        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // TODO: Check if an Item have been selected
                if(dAdapter.row_index >= 0){
                    Intent myIntent = new Intent(getActivity(), ViewCompDetailsActivity.class);
                    getActivity().startActivity(myIntent);
                }
                else{
                    Toast.makeText(getContext(), "Please select a competition for full detail review", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Get Competitions unregistered by the user from Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dAdapter.clearCompList();
                for(DataSnapshot compSnapshot : dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);

                    // Only find competitions yet to start
                    String compStartAt = comp.getDate().trim() + " " + comp.getStartTime().trim() + " GMT+08:00"; // Competition Time is based on AEST by default
                    long result = Common.timeToCompStart(compStartAt);
                    if(result >= 0.0){
                        if(comp.getAttendants() == null) {
                            dAdapter.addComp(comp);
                        } else{
                            if(!comp.getAttendants().contains(userID)){
                                dAdapter.addComp(comp);
                            }
                        }


                    }
                }
                dAdapter.sortByName();
                dAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: write something here??
            }
        });

        // Sorting options
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
                    case R.id.disc_sort_by_name:
                        dAdapter.sortByName();
                        break;
                    case R.id.disc_sort_by_date:
                        dAdapter.sortByDate();
                        break;
                    case R.id.disc_sort_by_reward:
                        dAdapter.sortByReward();
                        break;
                    default:
                        dAdapter.sortByName();
                }

                dAdapter.notifyDataSetChanged();
            }
        });




        return view;
    }


}
