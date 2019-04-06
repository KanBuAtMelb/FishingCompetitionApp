package com.example.fishingtest.Adapter;

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

public class DiscAdapter extends RecyclerView.Adapter<DiscAdapter.CompViewHolder>{

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

    int row_index = -1;


    // Constructor
    public  DiscAdapter(ArrayList<Competition> comps){
        this.comps = comps;
    }

    @NonNull
    @Override
    public DiscAdapter.CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_comp_item,viewGroup,false);
        DiscAdapter.CompViewHolder imageViewHolder = new DiscAdapter.CompViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscAdapter.CompViewHolder viewHolder, int position) {

        Competition comp = comps.get(position);
        viewHolder.compTittle.setText(comp.cname);
        viewHolder.compDescription.setText(comp.description);


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                row_index = position;
                Common.currentItem = comps.get(position);

                notifyDataSetChanged();
            }
        });


        if(row_index ==position){
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#F8F8FA"));
            viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
        }else{
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            viewHolder.compImage.setImageResource(R.drawable.ic_fish_blue);
        }

    }


    @Override
    public int getItemCount() {
        return comps.size();
    }
}
