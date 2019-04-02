package com.example.fishingtest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fishingtest.Models.CompAdapter;
import com.example.fishingtest.Models.CompetitionItem;

import java.util.ArrayList;


public class CompetitionsFragment extends Fragment {


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
        RecyclerView recyclerView =  view.findViewById(R.id.recyclerView_comps);
        // Set layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        // TODO: Get Competitions enrolled by the user from Firebase
        ArrayList<CompetitionItem> comps_enrolled = new ArrayList<CompetitionItem>();

        // Temporary data for recycler view
        comps_enrolled.add(new CompetitionItem("1"));
        comps_enrolled.add(new CompetitionItem("2"));
        comps_enrolled.add(new CompetitionItem("3"));
        comps_enrolled.add(new CompetitionItem("4"));

        // TODO: decide what image sample to be used to decide "comps"
        ArrayList<Integer> comps = new ArrayList<>();
        // Required empty public constructor
        for(int i = 1; i < 12; i++){
            comps.add(R.drawable.ic_fish_black);
        }

        // Create an adapter
        CompAdapter cAdapter = new CompAdapter(comps);
        // Set adaptor
        recyclerView.setAdapter(cAdapter);


        return view;
    }

}
