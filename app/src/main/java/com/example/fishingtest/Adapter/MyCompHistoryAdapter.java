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

        final Competition comp = comps.get(position);

        viewHolder.compTittle.setText(comp.getCname());

        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);

        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: "+ comp.getDate()+ " Time: From "+comp.getStartTime() +" To "+comp.getStopTime());
        if(currentUser != null){
            if(currentUser.getComps_won().contains(comp.getCompID())){
                viewHolder.compWon.setText("You are the Winner! Congrats!");
                viewHolder.compWon.setTextColor(Color.parseColor("#EC7063"));

                viewHolder.itemView.setBackgroundColor(Color.parseColor("#F4D0CC"));
            }else{
                viewHolder.compWon.setText("Unfortunatly you missed the awards");
                viewHolder.compWon.setTextColor(Color.parseColor("#D3D3D3"));

                viewHolder.itemView.setBackgroundColor(Color.parseColor("#006495"));

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
            viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.card_selected_text)));
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffff66"));

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

    public void addComp(Competition comp){

        comps.add(comp);
        notifyDataSetChanged();
    }


    public void setCurrentUser(User user){this.currentUser = user;}

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
        comps.sort(Comparator.comparing(Competition::calCompDateTime));
    }

    public void sortByReward(){
        comps.sort(Comparator.comparing(Competition::getReward).reversed());
    }

}
