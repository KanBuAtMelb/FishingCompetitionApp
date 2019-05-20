package com.example.fishingtest.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fishingtest.Model.Comment;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Post;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditCommentActivity extends AppCompatActivity {

    EditText edit_comment;
    Button btn_send;
    Post currentPost;
    DatabaseReference commentDBRef;
    DatabaseReference database;
    FirebaseUser fbUser;
    final String competitionCategory = "Competitions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);

        Intent intent = getIntent();
        currentPost = (Post) intent.getSerializableExtra("selectedPost");

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        database = FirebaseDatabase.getInstance().getReference();

        edit_comment = (EditText) findViewById(R.id.txt_comment);
        btn_send = (Button) findViewById(R.id.btn_send);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis()/1000;
                String timestamp = tsLong.toString();
                String commentId = timestamp + "_" + currentPost.getPostId();
                Comment comment = new Comment(commentId, currentPost.compId, currentPost.postId, fbUser.getUid(), edit_comment.getText().toString(), timestamp);
                commentDBRef = database.child("Posts").child(competitionCategory).child(currentPost.getCompId()).child(currentPost.getPostId()).child("Comments").child(commentId);
                Common.commentToDB(EditCommentActivity.this, commentDBRef, comment);
                finish();
            }
        });
    }
}
