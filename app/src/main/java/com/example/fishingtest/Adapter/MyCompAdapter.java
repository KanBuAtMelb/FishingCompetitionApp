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

import java.util.List;
import java.util.Map;


public class MyCompAdapter extends RecyclerView.Adapter {

    // Local variables
    private final static String TAG = "MyCompetition Adapter";
    //    private ArrayList<Competition> comps;
    private Context context;



    public int row_index = -1;
    public static final int VIEW_TYPE_TITLE= 0;
    public static final int VIEW_TYPE_INPRG=1;
    public static final int VIEW_TYPE_24HR =2;
    public static final int VIEW_TYPE_ITEM= 3;

    private List<Map<Integer,Competition>> mData;


    // New class addressing each "Competition" item view in the list
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView compImage;
        TextView compTittle;
        TextView compType;
        TextView compReward;
        TextView compDateTime;
        TextView timeBeforeStart;
        Button compUnregisterBtn;
        ItemClickListener itemClickListener;

        public ItemViewHolder(View itemView) {
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

    class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recyclerView_title);
        }
    }


    // Constructor
    public MyCompAdapter(List<Map<Integer,Competition>> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        switch(viewType){
            case VIEW_TYPE_TITLE:
                viewHolder = new TitleViewHolder(mInflater.inflate(R.layout.recyclerview_title, viewGroup, false));
                break;
            case VIEW_TYPE_INPRG:
                viewHolder = new ItemViewHolder(mInflater.inflate(R.layout.cardview_rgst_comp_item_inprogress, viewGroup,false));
                break;
            case VIEW_TYPE_24HR:
                viewHolder = new ItemViewHolder(mInflater.inflate(R.layout.cardview_rgst_comp_item_within24hours, viewGroup,false));
                break;
            case VIEW_TYPE_ITEM:
                viewHolder = new ItemViewHolder(mInflater.inflate(R.layout.cardview_rgst_comp_item_normal, viewGroup,false));
                break;

        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Competition comp;

        // Set up Division Title
        if(mData.get(position).containsKey(VIEW_TYPE_TITLE)){
            TitleViewHolder vh = (TitleViewHolder) viewHolder;
            vh.title.setText(mData.get(position).get(VIEW_TYPE_TITLE).getCompID());
        }

        // Set up competition card view
        else if(mData.get(position).containsKey(VIEW_TYPE_INPRG)){
            ItemViewHolder vh = (ItemViewHolder) viewHolder;
            comp = mData.get(position).get(VIEW_TYPE_INPRG);
            fillView(vh, comp, position);
            vh.timeBeforeStart.setText("Competition in Progress! ");
            vh.timeBeforeStart.setTextSize(18);
            vh.timeBeforeStart.setTextColor(Color.parseColor("#00335c"));
//            vh.itemView.setBackgroundColor(Color.parseColor("#EC7063"));
        }

        else if(mData.get(position).containsKey(VIEW_TYPE_24HR)){
            ItemViewHolder vh = (ItemViewHolder) viewHolder;
            comp = mData.get(position).get(VIEW_TYPE_24HR);
            fillView(vh, comp,position);
            String compStartTime = comp.getDate().concat(" ").concat(comp.getStartTime()).concat(" GMT+10:00");
            long timeLeftStart = Common.timeToCompStart(compStartTime);
            long diffMinutes = timeLeftStart / (60 * 1000) % 60;
            long diffHours = timeLeftStart / (60 * 60 * 1000) % 24;
            vh.timeBeforeStart.setText("Only " + (int) diffHours + " hours, " + (int) diffMinutes + " min left!");
            vh.timeBeforeStart.setTextSize(16);
            vh.timeBeforeStart.setTextColor(Color.parseColor("#00335c"));
//            vh.itemView.setBackgroundColor(Color.parseColor("#ffa600"));
        }

        else if(mData.get(position).containsKey(VIEW_TYPE_ITEM)){
            ItemViewHolder vh = (ItemViewHolder) viewHolder;
            comp = mData.get(position).get(VIEW_TYPE_ITEM);
            fillView(vh, comp,position);
            String compStartTime = comp.getDate().concat(" ").concat(comp.getStartTime()).concat(" GMT+10:00");
            long timeLeftStart = Common.timeToCompStart(compStartTime);
            long diffHours = timeLeftStart / (60 * 60 * 1000) % 24;
            long diffDays = timeLeftStart / (24 * 60 * 60 * 1000);
            vh.timeBeforeStart.setText("Still have " + (int) diffDays + " days, " + (int) diffHours + " hours, ");
            vh.timeBeforeStart.setTextSize(12);
            vh.timeBeforeStart.setTextColor(Color.parseColor("#66000000"));
//            vh.itemView.setBackgroundColor(Color.parseColor("#6495ED"));
        }


        if(viewHolder instanceof ItemViewHolder){
            ItemViewHolder vh = (ItemViewHolder) viewHolder;
            vh.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int i) {
                    if(position>= 0){
                        row_index = position;
                        Common.currentItem = findComp(position);

                        notifyDataSetChanged();
                    }else{
                        Toast.makeText(context, "Please select a competition to view.",Toast.LENGTH_LONG).show();
                    }
                }
            });

            vh.compUnregisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    final String compID = findComp(position).getCompID();

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
                            if (temp.getAttendants().contains(userID)) {
                                temp.removeAttendant(userID);
                                databaseComp.setValue(temp);
                                Log.d(TAG, "User " + userID + " removed from competition " + compID + " attendant list");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, "Competition still Registered", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //Unsubscribe the competition for leaving out the Notification from this competition
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(compID);

                    Toast.makeText(context, "Competition Removed from My Competition List", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private Competition findComp(int position) {
        Competition c = new Competition();

        if(mData.get(position).get(VIEW_TYPE_INPRG)!= null)
            c= mData.get(position).get(VIEW_TYPE_INPRG);
        else if(mData.get(position).get(VIEW_TYPE_24HR)!= null)
            c= mData.get(position).get(VIEW_TYPE_24HR);
        else if(mData.get(position).get(VIEW_TYPE_ITEM)!= null)
            c= mData.get(position).get(VIEW_TYPE_ITEM);

        return c;
    };

    private void fillView(ItemViewHolder viewHolder, Competition comp, int position) {

        viewHolder.compTittle.setText(comp.getCname());
        // Spinner item index to text view
        String[] compTypes = context.getResources().getStringArray(R.array.comp_type);
        viewHolder.compType.setText(compTypes[comp.getCompType()]);
        viewHolder.compReward.setText("Reward: $" + comp.getReward() + " AUD");
        viewHolder.compDateTime.setText("Date: " + comp.getDate() + " Time: From " + comp.getStartTime() + " To " + comp.getStopTime());

        if (row_index == position) {
            viewHolder.compTittle.setBackgroundColor(Color.parseColor(context.getString(R.string.card_selected_text)));

            if (comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_orange);
            else {
                // Set the customised competition image
                Picasso.get().load(comp.getImage_url()).fit().into(viewHolder.compImage);
            }
        } else {
            viewHolder.compTittle.setBackgroundColor(Color.parseColor("#6495ED"));

            if (comp.getImage_url().equals(Common.NA))
                viewHolder.compImage.setImageResource(R.drawable.ic_fish_deep_aqua);
            else {
                // Set the customised competition image
                Picasso.get().load(comp.getImage_url()).fit().into(viewHolder.compImage);
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        // Count down date and time
        if(mData.get(position).get(VIEW_TYPE_TITLE) != null)
            return VIEW_TYPE_TITLE;
        else if(mData.get(position).get(VIEW_TYPE_INPRG)!= null)
            return VIEW_TYPE_INPRG;
        else if(mData.get(position).get(VIEW_TYPE_24HR)!= null)
            return VIEW_TYPE_24HR;
        else
            return VIEW_TYPE_ITEM;


    };


    @Override
    public int getItemCount() {
        if(mData!= null)
            return mData.size();
        else
            return 0;
    }


    public void addCompMap(Map map){
        mData.add(map);
        notifyDataSetChanged();
    }

    public void clearCompList(){
        this.mData.clear();
    }
}






