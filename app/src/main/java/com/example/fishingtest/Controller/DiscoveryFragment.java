package com.example.fishingtest.Controller;


import android.os.Bundle;
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

import java.util.ArrayList;


public class DiscoveryFragment extends Fragment {



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


        // TODO: decide what image sample to be used to decide "comps"
        ArrayList<Competition> comps = new ArrayList<>();
        // Required empty public constructor
        for(int i = 1; i < 12; i++){
            Competition temp = new Competition(Integer.toString(i),"Competition #2",R.drawable.ic_fish_blue);
            comps.add(temp);
        }

        // Create an adapter
        DiscAdapter dAdapter = new DiscAdapter(comps);
        // Set adaptor
        recyclerView.setAdapter(dAdapter);


        return view;
    }
}
