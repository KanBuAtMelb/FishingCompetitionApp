package com.example.fishingtest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Controller.PostDetailActivity;
import com.example.fishingtest.Controller.ViewCompDetailsActivity;
import com.example.fishingtest.Controller.ViewPostsActivity;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Project: Fishing Competition
 * Author: Ziqi Zhang
 * Date: 8/06/2019
 * Recycler View Adapter for the Recycler View implemented in "ViewPostsActivity"
 *
 */

public class PostsAdapter extends RecyclerView.Adapter{
    //Initial variable
    List<Post> myPostsData = new ArrayList<>();
    Context mContext;

    public PostsAdapter(Context context) {
        mContext = context;
    }

    //Inflate the item view 'cardview_posts_items' in recycle view
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cardview_posts_items, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    //Read and bind the list of posts data to the 'cardview_posts_items' view attribute
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder mHolder = (MyViewHolder) viewHolder;
        Post post = myPostsData.get(i);
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(post.userId);
        //Read user data from database
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (!(user.getImagePath().equals(Common.NA))) {
                    Picasso.get().load(user.getImagePath()).fit().into(mHolder.userAvatar);
                } else {
                    //Bind image to the 'userAvatar' imageview
                    mHolder.userAvatar.setImageResource(R.drawable.people);
                }
                //Bind text to the 'username' textview
                mHolder.username.setText(user.getDisplayName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext,i+"th card cannot load user info!",Toast.LENGTH_SHORT).show();
            }
        } ;


        databaseUser.addListenerForSingleValueEvent(userListener);
        //Bind textview
        mHolder.time.setText(Common.timeStampToTime(post.timeStamp));
        mHolder.dataText.setText(post.getMeasuredData());
        if (post.getFishingName() == null) {
            mHolder.fishnameText.setText(R.string.fishingname_unknown);
        } else {
            mHolder.fishnameText.setText(post.getFishingName());
        }
        //Load image from post photo url
        Picasso.get().load(post.getOriDownloadUrl()).fit().into(mHolder.fishPhoto);
    }

    //Function of get count of the data list
    @Override
    public int getItemCount() {
        return myPostsData.size();
    }
    //Function of readd all data operation of the data list
    public void refreshItems(List<Post> items) {
        myPostsData.clear();
        myPostsData.addAll(items);
        notifyDataSetChanged();
    }
    //Function of add operation of the data list
    public void addItems(List<Post> items) {
        myPostsData.addAll(items);
    }
    //Function of add operation of the data list
    public void addItem(Post item){
        myPostsData.add(0, item);
        notifyDataSetChanged();
    }
    //Function of delete operation of the data list
    public void deleteItem(int position) {
        myPostsData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, myPostsData.size() - 1);
    }
    //Construct and initial the attributes of the item view of the recycle view
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fishnameText;
        TextView dataText;
        ImageView userAvatar;
        TextView username;
        TextView time;
        ImageView fishPhoto;

        // initial the attributes with corresponded attributes of item view
        public MyViewHolder(View itemView) {
            super(itemView);
            dataText = (TextView) itemView.findViewById(R.id.post_content);
            fishnameText = (TextView) itemView.findViewById(R.id.post_Namecontent);
            username = (TextView) itemView.findViewById(R.id.text_post_username);
            time = (TextView) itemView.findViewById(R.id.text_post_time);
            userAvatar = (ImageView) itemView.findViewById(R.id.imgView_post_avatar);
            fishPhoto = (ImageView) itemView.findViewById(R.id.imgView_post_fish_photo);

            //Click the item to navigate the item's detail page
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,+getLayoutPosition()+"th has been chosen",Toast.LENGTH_SHORT).show();
                    Post selectedPost = myPostsData.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("selectedPost", selectedPost);
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
