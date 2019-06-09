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
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * Recycler View Adapter for the Recycler View implemented in "SelectCompWinnerActivity"
 * which is the controller for the "Select Winner" page for the Administrator
 * after a competition waiting for results has been selected.
 */

public class WinnerPostListAdapter extends RecyclerView.Adapter<WinnerPostListAdapter.PostViewHolder>{

    // New class addressing each "Competition" item view in the list
    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView compImage;
        TextView author;
        TextView postTime;
        TextView compResult;

        ItemClickListener itemClickListener;

        public PostViewHolder(View itemView){
            super(itemView);
            compImage = itemView.findViewById(R.id.comp_image);
            author = itemView.findViewById(R.id.post_author);
            postTime = itemView.findViewById(R.id.post_date_time);
            compResult = itemView.findViewById(R.id.post_result);

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
    private static final String TAG = "Result List Adapter";
    ArrayList<Post> posts; // Post List
    DatabaseReference databaseUserName;
    String uName;
    String postTime;
    public int row_index = -1;
    Context context;

    // Constructor
    public WinnerPostListAdapter(ArrayList<Post> posts, Context context){
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_select_comp_results_comp_item,viewGroup,false);
        PostViewHolder imageViewHolder = new PostViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position) {

        final Post post = posts.get(position);

        // Display Post image
        Picasso.get().load(post.getMeaDownloadUrl()).fit().into(viewHolder.compImage);

        //  Display the user name of selected post
        databaseUserName = FirebaseDatabase.getInstance().getReference("Users").child(post.getUserId()).child("displayName");
        uName= new String();
        databaseUserName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uName = dataSnapshot.getValue(String.class);
                viewHolder.author.setText("Author: " + uName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                uName = Common.NA;
                Toast.makeText(context,"User error", Toast.LENGTH_SHORT).show();
            }
        });


        // Display post date and time
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(post.getTimeStamp()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        postTime = formatter.format(c.getTime());

        viewHolder.postTime.setText("Post Published Time: "+ postTime);

        // Set the post background color changes when the item is clicked
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position>= 0){
                    row_index = position;
                    Common.currentPostItem = posts.get(position);
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "Please select a post to view.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(row_index ==position){
            viewHolder.itemView.setBackgroundColor(Color.parseColor(context.getString(R.string.cardview_winnerPost_itemView_background_selected)));

        }else{
            viewHolder.itemView.setBackgroundColor(Color.parseColor(context.getString(R.string.cardview_winnerPost_itemView_background_unselected)));
        }

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Add post to the Post List
    public void addPost(Post post){
        posts.add(post);
    }

    public Boolean contains(Post post){
        return posts.contains(post);
    }

    public void clearPostList(){
        this.posts.clear();
    }

    public void sortByTime(){
        // create sorted comp list for different usage
        posts.sort(Comparator.comparing(Post::getTimeStamp));
    }
}
