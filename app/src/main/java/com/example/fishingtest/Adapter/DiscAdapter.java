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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * Recycler View Adapter for the Recycler View implemented in "DiscoveryFragment"
 * which is the controller for the "Discovery" page of the Home Page.
 */

public class DiscAdapter extends RecyclerView.Adapter<DiscAdapter.CompViewHolder>{

    // New class addressing each "Competition" item view in the Comp List
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView compImage;
        TextView compTittle;
        TextView compType;
        TextView compReward;
        TextView compDateTime;
        Button compRegisterBtn;

        ItemClickListener itemClickListener;

        public CompViewHolder(View itemView){
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            compTittle = itemView.findViewById(R.id.comp_title);
            compType = itemView.findViewById(R.id.comp_type);
            compReward = itemView.findViewById(R.id.comp_reward);
            compDateTime = itemView.findViewById(R.id.comp_date_time);
            compRegisterBtn = itemView.findViewById(R.id.btn_comp_register);

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
    private static final String TAG = "Discovery Adapter";
    ArrayList<Competition> comps; // Comp list
    public int row_index = -1;  // Selected item index in Comp List
    Context context;

    // Constructor
    public  DiscAdapter(ArrayList<Competition> comps, Context context){
        this.comps = comps;
        this.context = context;
    }

    @NonNull
    @Override
    public DiscAdapter.CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_dscv_comp_item,viewGroup,false);
        DiscAdapter.CompViewHolder imageViewHolder = new DiscAdapter.CompViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscAdapter.CompViewHolder viewHolder, int position) {
        // Get the Competition object from the Comp List based on the position selected
        final Competition comp = comps.get(position);

        // Set the text on each card view
        viewHolder.compTittle.setText(comp.getCname());
        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: "+ comp.getDate()+ " Time: From "+comp.getStartTime() +" To "+comp.getStopTime());

        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);

        // Click on "Registration Now" button
        viewHolder.compRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user
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
                        if(!temp.getComps_registered().contains(compID)) {
                            temp.addRegComp(compID);
                            databaseUser.child("comps_registered").setValue(temp.getComps_registered());
                            Log.d(TAG, "Competition" + compID + " added to User " + userID + " Registration Competition List");
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
                        if(!temp.getAttendants().contains(userID)){
                            temp.addAttendant(userID);
                            databaseComp.child("attendants").setValue(temp.getAttendants());
                            Log.d(TAG,"User "+userID+" added to Competition "+compID+" Attendant list"); // FOr debugging purpose
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context, "Competition Registration Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                //Subscribe this competition for waiting Notification from the competition
                FirebaseMessaging.getInstance().subscribeToTopic(compID);

                // Toast message
                Toast.makeText(context, "Competition Registration Successful", Toast.LENGTH_SHORT).show();

                // Remain original sorting order
                switch(Common.DISCOVERY_SORT_ORDER){
                    case 0:
                        sortByName();
                        break;
                    case 1:
                        sortByDate();
                        break;
                    case 2:
                        sortByReward();
                        break;
                    default:
                        sortByName();
                }

                // Notify the changes after sorting
                notifyDataSetChanged();

                //Reset selected card
                if(comps.size() == 0){
                    row_index = -1;
                }
            }
        });


        // Competition title background and fish image change upon selection
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position>= 0){
                    row_index = position;
                    Common.currentCompItem = comps.get(position);
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "Please select a competition to view.",Toast.LENGTH_LONG).show();
                }

            }
        });


        if(row_index ==position){
            // Set title background on competition selection
            viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.card_selected_text)));
            // Set the competition fish image color change if it has no customised competition image uploaded
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else{
                Picasso.get().load(comp.getImage_url()).fit().into(viewHolder.compImage);
            }
        }else{
            // Set title background when competition not selected
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#6495ED"));
            // Set the competition fish image color change if it has no customised competition image uploaded
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_blue);
            else{
                Picasso.get().load(comp.getImage_url()).fit().into(viewHolder.compImage);
            }
        }
    }


    @Override
    public int getItemCount() {
        return comps.size();
    }

    // Add competition to Comp List
    public void addComp(Competition comp){
        comps.add(comp);
        notifyDataSetChanged();
    }

    // Check if a competition already exists
    public Boolean contains(Competition comp){
        return comps.contains(comp);
    }

    // Clear Comp List
    public void clearCompList(){
        this.comps.clear();
    }

    // Sort Comp List by competition name
    public void sortByName(){
        // create sorted comp list for different usage
        comps.sort(Comparator.comparing(Competition::getCname));
    }

    // Sort Comp List by competition date
    public void sortByDate(){
        comps.sort(Comparator.comparing(Competition::calCompDateTime));
    }

    // Sort Comp List by reward
    public void sortByReward(){
        comps.sort(Comparator.comparing(Competition::getReward).reversed());
    }

}
