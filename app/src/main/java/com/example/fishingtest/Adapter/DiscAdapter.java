package com.example.fishingtest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DiscAdapter extends RecyclerView.Adapter<DiscAdapter.CompViewHolder>{

    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int currentItem;
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
    ArrayList<Competition> comps;

    public int row_index = -1;
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

        final Competition comp = comps.get(position);


        viewHolder.compTittle.setText(comp.getCname());

        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);

        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: "+ comp.getDate()+ " Time: From "+comp.getStartTime() +" To "+comp.getStopTime());

        // Click on "Registration Now" button
        viewHolder.compRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            databaseUser.setValue(temp);
                            Log.d(TAG, "Competition" + compID + " added to User " + userID + " Registration Competition List");
                            //Toast.makeText(context, "Competition now in your Competition List", Toast.LENGTH_SHORT).show();
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
                            databaseComp.setValue(temp);
                            Log.d(TAG,"User "+userID+" added to Competition "+compID+" Attendant list");
                            //Toast.makeText(context, "Competition Registration Succeeds", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: add something here
                    }
                });

                //Subscribe this competition for waiting Notification from the competition
                FirebaseMessaging.getInstance().subscribeToTopic(compID);
            }
        });


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                row_index = position;
                Common.currentItem = comps.get(position);
                notifyDataSetChanged();
            }
        });


        if(row_index ==position){
            viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.card_selected_text)));
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffff66"));

            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else{
                // TODO: Set the customised competition image
            }
        }else{
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#6495ED"));
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_blue);
            else{
                // TODO: Set the customised competition image
            }
        }

    }


    @Override
    public int getItemCount() {
        return comps.size();
    }

    public void addComp(Competition comp){
        comps.add(0,comp);
        notifyDataSetChanged();
    }



    public Boolean contains(Competition comp){
        return comps.contains(comp);
    }

    public void clearCompList(){
        this.comps.clear();
    }

    public void sortByName(){
        // create sorted comp list for different usage
        comps.sort(Comparator.comparing(Competition::getCname));
    }

    public void sortByDate(){
        comps.sort(Comparator.comparing(Competition::getCompDateTime));
    }

    public void sortByReward(){
        comps.sort(Comparator.comparing(Competition::getReward));
    }

}
