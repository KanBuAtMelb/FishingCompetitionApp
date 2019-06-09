package com.example.fishingtest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Interface.ItemClickListener;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * Recycler View Adapter for the Recycler View implemented in "ViewMyCompHistoryActivity"
 * which is the controller for "My Competition History" page.
 */

public class MyCompHistoryAdapter extends RecyclerView.Adapter<MyCompHistoryAdapter.CompViewHolder>{

    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView compImage;
        TextView compTittle;
        TextView compType;
        TextView compReward;
        TextView compDateTime;
        TextView compWon;

        ItemClickListener itemClickListener;

        public CompViewHolder(View itemView){
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            compTittle = itemView.findViewById(R.id.comp_title);
            compType = itemView.findViewById(R.id.comp_type);
            compReward = itemView.findViewById(R.id.comp_reward);
            compDateTime = itemView.findViewById(R.id.comp_date_time);
            compWon = itemView.findViewById(R.id.comp_winner);

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
    private static final String TAG = "My Competition History Adapter";
    ArrayList<Competition> comps;

    public int row_index = -1;
    Context context;

    User currentUser;

    // Constructor
    public MyCompHistoryAdapter(ArrayList<Competition> comps, Context context){

        this.comps = comps;
        this.context = context;
    }

    @NonNull
    @Override
    public MyCompHistoryAdapter.CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_profile_comp_item,viewGroup,false);
        MyCompHistoryAdapter.CompViewHolder imageViewHolder = new MyCompHistoryAdapter.CompViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCompHistoryAdapter.CompViewHolder viewHolder, int position) {
        // Get the Competition object from the Comp List based on the position selected
        final Competition comp = comps.get(position);

        // Set the text on each card view
        viewHolder.compTittle.setText(comp.getCname());
        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: "+ comp.getDate()+ " Time: From "+comp.getStartTime() +" To "+comp.getStopTime());

        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);

        // Set text to the competition result
        if(currentUser != null){
            if(currentUser.getComps_won().contains(comp.getCompID())){
                viewHolder.compWon.setText("You are the Winner! Congrats!");
                viewHolder.compWon.setTextColor(Color.parseColor(context.getString(R.string.cardview_mycompHistory_compWon_background_winner)));

                viewHolder.itemView.setBackgroundColor(Color.parseColor(context.getString(R.string.cardview_mycompHistory_itemView_background_winner)));
            }else{
                viewHolder.compWon.setText("Unfortunatly you missed the awards");
                viewHolder.compWon.setTextColor(Color.parseColor(context.getString(R.string.cardview_mycompHistory_compWon_background_loser)));
                viewHolder.itemView.setBackgroundColor(Color.parseColor(context.getString(R.string.cardview_mycompHistory_itemView_background_loser)));
            }
        }else{
            viewHolder.compWon.setText("Hold on! Loading the data");
        }

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


        // Competition images
        if(row_index ==position){
            viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.cardview_title_background_selected)));
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else{
                // Set the customised competition image
                Picasso.get().load(comp.getImage_url()).fit().into(viewHolder.compImage);
            }
        }else{
            String backgroundColor;
            if(currentUser.getComps_won().contains(comp.getCompID())){
                backgroundColor = "#F4D0CC";
            }else{
                backgroundColor = "#006495";
            }
                viewHolder.compTittle.setBackgroundColor(Color.parseColor(backgroundColor));
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_blue);
            else{
                // Set the customised competition image
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

    // Set the current user
    public void setCurrentUser(User user){this.currentUser = user;}

    // Check if a competition already exists
    public Boolean contains(Competition comp){
        return comps.contains(comp);
    }

    // Clear Comp List
    public void clearCompList(){
        this.comps.clear();
    }

    // Sort Comp List by date
    public void sortByDate(){
        comps.sort(Comparator.comparing(Competition::calCompDateTime));
    }

}
