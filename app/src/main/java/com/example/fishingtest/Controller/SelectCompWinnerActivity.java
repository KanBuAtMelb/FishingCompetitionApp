package com.example.fishingtest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Adapter.WinnerPostListAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * The controller for the "Select Competition Winner" activity for the Administrator.
 */

public class SelectCompWinnerActivity extends AppCompatActivity {

    // Local variables
    final static public String TAG = "Select Comp Winner";

    // UI views
    TextView cName;
    RecyclerView recyclerView;
    Button btnAddCompResult;

    ArrayList<Post> posts;
    WinnerPostListAdapter pAdapter;
    DatabaseReference databasePost;
    DatabaseReference databaseUser;
    DatabaseReference databaseComp;

    String compID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_competition_winner);

        // UI
        cName = (TextView) findViewById(R.id.view_comp_results_comp_name);
        recyclerView = (RecyclerView)findViewById(R.id.view_comp_results_post_list);
        btnAddCompResult = (Button) findViewById(R.id.admin_button_add_comp_result);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // Create an adapter
        posts = new ArrayList<>();
        pAdapter = new WinnerPostListAdapter(posts, this);

        // Set adaptor
        recyclerView.setAdapter(pAdapter);

        // Get the winner information from the adapter
        cName.setText(getIntent().getStringExtra(Common.COMPNAME));

        // Get the Firebase compID
        compID = getIntent().getStringExtra(Common.COMPID);
        databaseComp = FirebaseDatabase.getInstance().getReference("Competitions").child(compID);

        // Get Post list from Firebase
        databasePost = FirebaseDatabase.getInstance().getReference("Posts").child("Competitions").child(compID);
        databasePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pAdapter.clearPostList();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Post post = postSnapshot.getValue(Post.class);
                    pAdapter.addPost(post);
                }
                // Sort the Comp List in the adapter and notify the changes on sorting
                pAdapter.sortByTime();
                pAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: write something here??
            }
        });

        // Click button to confirm result
        btnAddCompResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentPostItem !=null){ // check if any post selected
                    Post post = Common.currentPostItem;
                    userID = Common.currentPostItem.getUserId();

                    // Update Users database
                    databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User temp = dataSnapshot.getValue(User.class);
                            temp.checkArrayList();

                            //Update user wonCompList
                            if(!temp.getComps_won().contains(compID)){
                                temp.addWonComp(compID);
                            }
                            databaseUser.child("comps_won").setValue(temp.getComps_won());
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

                            if (temp.getWinner().equals(Common.NA))
                                temp.setWinner(userID);

                            if (temp.getResults().equals(Common.NA))
                                temp.setResults(post.getMeasuredData());

                            temp.setResults(post.getMeasuredData());
                            databaseComp.child("results").setValue(temp.getResults());
                            databaseComp.child("winner").setValue(temp.getWinner());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    // Automatically transfer to the parent activity after successful Firebase update
                    Intent selectCompIntent = new Intent(SelectCompWinnerActivity.this, AddCompResultsActivity.class);
                    startActivity(selectCompIntent);
                    finish();
                }else{
                    Toast.makeText(SelectCompWinnerActivity.this, "Please select a post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
