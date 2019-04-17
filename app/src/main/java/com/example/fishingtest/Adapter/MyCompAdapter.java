package com.example.fishingtest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fishingtest.Interface.ItemClickListener;
import com.example.fishingtest.Model.Common;
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


public class MyCompAdapter extends RecyclerView.Adapter<MyCompAdapter.CompViewHolder>{



    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int currentItem;
        ImageView compImage;
        TextView compTittle;
        TextView compReward;
        TextView compDateTime;
        TextView timeBeforeStart;
        Button compUnregisterBtn;

        ItemClickListener itemClickListener;

        public CompViewHolder(View itemView){
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            compTittle = itemView.findViewById(R.id.comp_title);
            compReward = itemView.findViewById(R.id.comp_reward);
            compDateTime = itemView.findViewById(R.id.comp_date_time);
            timeBeforeStart = itemView.findViewById(R.id.time_before_start);
            compUnregisterBtn = itemView.findViewById(R.id.btn_comp_unregister);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }


    // Local variables
    private final static String TAG = "MyCompetition Adapter";
    ArrayList<Competition> comps;
    Context context;
    int row_index = -1;


    // Constructor
    public MyCompAdapter(ArrayList<Competition> comps, Context context){
        this.comps = comps;
        this.context = context;
    }

    @NonNull
    @Override
    public CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_rgst_comp_item,viewGroup,false);
        CompViewHolder imageViewHolder = new CompViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompViewHolder viewHolder, int position) {

        final Competition comp = comps.get(position);
        viewHolder.compTittle.setText(comp.getCname());
        viewHolder.compReward.setText(comp.getReward());
        viewHolder.compDateTime.setText(comp.getDate()+ " From "+comp.getStartTime() +" To "+comp.getStopTime());

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int i) {
                row_index = i;
                Common.currentItem = comps.get(i);
                notifyDataSetChanged();
            }
        });


        if(row_index ==position){
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#ff3300"));

            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else{
                // TODO: Set the customised competition image
            }
        }else{
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#FFFFFF"));

            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_black);
            else{
                // TODO: Set the customised competition image
            }
        }

        viewHolder.compUnregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: WHAT IF comp is null??
                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String compID = comp.getCompID();

                final DatabaseReference databaseComp = FirebaseDatabase.getInstance().getReference("Competitions").child(compID);
                final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                // Update Users database
                databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        temp.checkArrayList();
                        if(temp.getComps_registered().contains(compID)) {
                            temp.removeRegComp(compID);
                            databaseUser.setValue(temp);
                            Log.d(TAG, "Competition " + compID + " removed from User "+userID +" registration list");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: add something here
                    }
                });

                // Update Competition database
                databaseComp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Competition temp = dataSnapshot.getValue(Competition.class);
                        temp.checkArrayList();
                        if(temp.getAttendants().contains(userID));{
                            temp.removeAttendant(userID);
                            databaseComp.setValue(temp);
                            Log.d(TAG,"User " + userID +" removed from competition "+ compID+ " attendant list");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: add something here
                    }
                });

            }
        });

    }


    @Override
    public int getItemCount() {
        if(comps != null)
            return comps.size();
        else
            return 0;
    }


    public void addComp(Competition comp){
        comps.add(comp);
        notifyDataSetChanged();
    }

    public void clearCompList(){
        this.comps.clear();
    }
}






