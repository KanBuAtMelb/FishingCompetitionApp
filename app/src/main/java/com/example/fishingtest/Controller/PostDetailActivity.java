package com.example.fishingtest.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Comment;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    Post currentPost;
    TextView dataText;
    ImageView userAvatar;
    TextView username;
    TextView time;
    ImageView fishPhoto_ori;
    ImageView fishPhoto_mea;
    Button btn_send_Comment;
    DatabaseReference commentDBRef;
    DatabaseReference database;
    final String competitionCategory = "Competitions";
    List<Comment> myCommentsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        currentPost = (Post) intent.getSerializableExtra("selectedPost");
        database = FirebaseDatabase.getInstance().getReference();
        dataText = (TextView) findViewById(R.id.post_content);
        username = (TextView) findViewById(R.id.text_post_username);
        time = (TextView) findViewById(R.id.text_post_time);
        userAvatar = (ImageView) findViewById(R.id.imgView_post_avatar);
        fishPhoto_ori = (ImageView) findViewById(R.id.imgView_post_detail_ori);
        fishPhoto_mea = (ImageView) findViewById(R.id.imgView_post_detail_mea);
        btn_send_Comment = (Button) findViewById(R.id.btn_post_detail_send_comment);
        commentDBRef = database.child("Posts").child(competitionCategory).child(currentPost.getCompId()).child("Comments");
        readPost();
        btn_send_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, EditCommentActivity.class);
                intent.putExtra("selectedPost", currentPost);
                startActivity(intent);
            }
        });
    }

    private void readPost() {
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(currentPost.userId);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImagePath() != Common.NA){
                    Picasso.get().load(user.getImagePath()).fit().into(userAvatar);
                }

                username.setText(user.getDisplayName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this,"Cannot load user info!",Toast.LENGTH_SHORT).show();

            }
        };

        databaseUser.addListenerForSingleValueEvent(userListener);

        time.setText(Common.timeStampToTime(currentPost.timeStamp));
        dataText.setText(currentPost.getMeasuredData());
        Picasso.get().load(currentPost.getOriDownloadUrl()).fit().into(fishPhoto_ori);
        Picasso.get().load(currentPost.getMeaDownloadUrl()).rotate(90).fit().into(fishPhoto_mea);
    }

    private void readComment() {

        ChildEventListener postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                myCommentsData.add(comment);
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
                Toast.makeText(PostDetailActivity.this, "Get Comments Failed! \n Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        } ;

        commentDBRef.addChildEventListener(postListener);
    }
}
