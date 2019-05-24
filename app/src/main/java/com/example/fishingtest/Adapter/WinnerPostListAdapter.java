package com.example.fishingtest.Adapter;

import android.content.Context;
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

public class WinnerPostListAdapter extends RecyclerView.Adapter<WinnerPostListAdapter.CompViewHolder>{

    // New class addressing each "Competition" item view in the list
    class CompViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView compImage;
        TextView author;
        TextView postTime;
        TextView compResult;

        ItemClickListener itemClickListener;

        public CompViewHolder(View itemView){
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
    ArrayList<Post> posts;
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
    public WinnerPostListAdapter.CompViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_select_comp_results_comp_item,viewGroup,false);
        WinnerPostListAdapter.CompViewHolder imageViewHolder = new WinnerPostListAdapter.CompViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WinnerPostListAdapter.CompViewHolder viewHolder, int position) {

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
        c.setTimeInMillis(Long.getLong(post.getTimeStamp()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        postTime = formatter.format(c.getTime());

        viewHolder.postTime.setText("Post Published Time: "+ postTime);


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

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addPost(Post post){
        posts.add(post);
//        notifyDataSetChanged();  //TODO: WHY NEEDED HERE?
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

    public String getuName() {
        return uName;
    }
}
