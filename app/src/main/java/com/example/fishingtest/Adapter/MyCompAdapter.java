package com.example.fishingtest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;


public class MyCompAdapter extends RecyclerView.Adapter<MyCompAdapter.CompViewHolder> {

    // Local variables
    private final static String TAG = "MyCompetition Adapter";
    ArrayList<Competition> comps;
    Context context;
    public int row_index = -1;

    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView compImage;
        TextView compTittle;
        TextView compType;
        TextView compReward;
        TextView compDateTime;
        TextView timeBeforeStart;
        Button compUnregisterBtn;

        ItemClickListener itemClickListener;


        public CompViewHolder(View itemView) {
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            compTittle = itemView.findViewById(R.id.comp_title);
            compType = itemView.findViewById(R.id.comp_type);
            compReward = itemView.findViewById(R.id.comp_reward);
            compDateTime = itemView.findViewById(R.id.comp_date_time);
            timeBeforeStart = itemView.findViewById(R.id.time_before_start);
            compUnregisterBtn = itemView.findViewById(R.id.btn_comp_unregister);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }


    // Constructor
    public MyCompAdapter(ArrayList<Competition> comps, Context context) {
        this.comps = comps;
        this.context = context;
    }

    @NonNull
    @Override
    public CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        CompViewHolder imageViewHolder = null;
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        imageViewHolder = new CompViewHolder(mInflater.inflate(R.layout.cardview_rgst_comp_item_top, viewGroup, false));


        return imageViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CompViewHolder viewHolder, int position) {

        final Competition comp = comps.get(position);
        viewHolder.compTittle.setText(comp.getCname());

        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);

        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: " + comp.getDate() + " Time: From " + comp.getStartTime() + " To " + comp.getStopTime());

        // Count down date and time
        String compTime = comp.getDate().concat(" ").concat(comp.getStartTime()).concat(" GMT+08:00");
        long timeleft = Common.timeToCompStart(compTime);
        long diffMinutes = timeleft / (60 * 1000) % 60;
        long diffHours = timeleft / (60 * 60 * 1000) % 24;
        long diffDays = timeleft / (24 * 60 * 60 * 1000);

        if (diffDays < 1) {
            viewHolder.timeBeforeStart.setText("Only " + (int) diffHours + " hours, " + (int) diffMinutes + " min left!");
            viewHolder.timeBeforeStart.setTextSize(16);

            viewHolder.timeBeforeStart.setTextColor(Color.parseColor("#00335c"));
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffa600"));
        } else {
            viewHolder.timeBeforeStart.setText("Still have " + (int) diffDays + " days, " + (int) diffHours + " hours, " + (int) diffMinutes + " min");
            viewHolder.timeBeforeStart.setTextSize(12);
            viewHolder.timeBeforeStart.setTextColor(Color.parseColor("#66000000"));
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#6495ED"));
        }

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int i) {
                if(position>= 0){
                    row_index = position;
                    Common.currentItem = comps.get(position);
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "Please select a competition to view.",Toast.LENGTH_LONG).show();
                }
            }
        });


        if (row_index == position) {
            //viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.card_selected_text)));

            if (comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else {
                // TODO: Set the customised competition image
            }
        } else {
            //viewHolder.compTittle.setBackgroundColor(Color.parseColor("#6495ED"));

            if (comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_deep_aqua);
            else {
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
                        if (temp.getComps_registered().contains(compID)) {
                            temp.removeRegComp(compID);
                            databaseUser.setValue(temp);
                            Log.d(TAG, "Competition " + compID + " removed from User " + userID + " registration list");
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
                        if (temp.getAttendants().contains(userID)) ;
                        {
                            temp.removeAttendant(userID);
                            databaseComp.setValue(temp);
                            Log.d(TAG, "User " + userID + " removed from competition " + compID + " attendant list");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: add something here
                    }
                });

                //Unsubscribe the competition for leaving out the Notification from this competition
                FirebaseMessaging.getInstance().unsubscribeFromTopic(compID);
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






