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

        Intent intent = getIntent();
        currentComp = (Competition) intent.getSerializableExtra("currentComp");

        postDBRef = database.child("Posts").child(competitionCategory).child(currentComp.getCompID());

        ChildEventListener postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.addItem(post);
                Log.i("Check Add","add post Measured Data = " + post.getMeasuredData());
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

        postDBRef.addChildEventListener(postListener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
