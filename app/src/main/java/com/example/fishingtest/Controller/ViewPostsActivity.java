package com.example.fishingtest.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.fishingtest.Adapter.PostsAdapter;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Project: Fishing Competition
 * Author: Ziqi Zhang
 * Date: 8/06/2019
 * The activity can view list of posts of given competition
 *
 */

public class ViewPostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PostsAdapter adapter;
    Competition currentComp;
    DatabaseReference database;
    DatabaseReference postDBRef;
    final String competitionCategory = "Competitions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);
        database = FirebaseDatabase.getInstance().getReference();

        adapter = new PostsAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.post_Recycler);

        layoutManager = new LinearLayoutManager(this);

        //get the competition data from parent activity
        Intent intent = getIntent();
        currentComp = (Competition) intent.getSerializableExtra("currentComp");

        postDBRef = database.child("Posts").child(competitionCategory).child(currentComp.getCompID());

        // read posts of the current competition from database then check whether it is not post exist now
        database.child("Posts").child(competitionCategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.hasChild(currentComp.getCompID()))) {
                    Toast.makeText(ViewPostsActivity.this, "No one catch fish now! Go to be the first person!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // build the listener to read posts of the current competition from database dynamically
        ChildEventListener postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    Post post = dataSnapshot.getValue(Post.class);
                    // if a new post has been read then add to item list in adapter to show on the activity
                    adapter.addItem(post);
                    Log.i("Check Add","add post Measured Data = " + post.getMeasuredData());
                } else {
                    Toast.makeText(ViewPostsActivity.this, "No one catch fish now! Go to be the first person!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewPostsActivity.this, "Get Posts Failed! \n Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        } ;

        // invoke the listener with the current competition database reference and order by post time
        postDBRef.orderByChild("timeStamp").addChildEventListener(postListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
