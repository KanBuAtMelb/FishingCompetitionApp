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

import com.example.fishingtest.Interface.ItemClickListener;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;

import java.util.ArrayList;


public class CompAdapter extends RecyclerView.Adapter<CompAdapter.CompViewHolder>{



    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int currentItem;
        ImageView compImage;
        TextView compTittle;
        TextView compDescription;

        ItemClickListener itemClickListener;

        public CompViewHolder(View itemView){
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            compTittle = itemView.findViewById(R.id.comp_title);
            compDescription = itemView.findViewById(R.id.comp_description);
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
    ArrayList<Competition> comps;
    Context context;

    int row_index = -1;


    // Constructor
    public  CompAdapter(ArrayList<Competition> comps, Context context){
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

        Competition comp = comps.get(position);
        viewHolder.compTittle.setText(comp.getCname());
        viewHolder.compDescription.setText(comp.getcDescription());



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
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffff66"));

            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else{
                // TODO: Set the customised competition image
            }
        }else{
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_black);
            else{
                // TODO: Set the customised competition image
            }
        }

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

    public Boolean contains(Competition comp){
        return comps.contains(comp);
    }
}






