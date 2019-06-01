package com.example.fishingtest.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    TextView fishnameText;
    TextView dataText;
    ImageView userAvatar;
    TextView username;
    TextView time;
    ImageView fishPhoto_ori;
    ImageView fishPhoto_mea;
    Button btn_send_Comment;
    DatabaseReference commentDBRef;
    DatabaseReference database;
    LinearLayout commentLinearLayout;
    final String competitionCategory = "Competitions";
    List<Comment> myCommentsData = new ArrayList<>();
    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        currentPost = (Post) intent.getSerializableExtra("selectedPost");
        database = FirebaseDatabase.getInstance().getReference();
        commentLinearLayout = (LinearLayout) findViewById(R.id.linear_comment);
        fishnameText = (TextView) findViewById(R.id.post_Namecontent);
        dataText = (TextView) findViewById(R.id.post_content);
        username = (TextView) findViewById(R.id.text_post_username);
        time = (TextView) findViewById(R.id.text_post_time);
        userAvatar = (ImageView) findViewById(R.id.imgView_post_avatar);
        fishPhoto_ori = (ImageView) findViewById(R.id.imgView_post_detail_ori);
        fishPhoto_mea = (ImageView) findViewById(R.id.imgView_post_detail_mea);
        btn_send_Comment = (Button) findViewById(R.id.btn_post_detail_send_comment);
        commentDBRef = database.child("Posts").child(competitionCategory).child(currentPost.getCompId()).child(currentPost.postId).child("Comments");

        readPost();

        btn_send_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, EditCommentActivity.class);
                intent.putExtra("selectedPost", currentPost);
                startActivity(intent);
            }
        });

        readComment();
    }

    private void readPost() {
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(currentPost.userId);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (!(user.getImagePath().equals(Common.NA))) {
                    Picasso.get().load(user.getImagePath()).fit().into(userAvatar);
                } else {
                    userAvatar.setImageResource(R.drawable.people);
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

        if (currentPost.getFishingName() == null) {
            fishnameText.setText(R.string.fishingname_unknown);
        } else {
            fishnameText.setText(currentPost.getFishingName());
        }

        dataText.setText(currentPost.getMeasuredData());
        Picasso.get().load(currentPost.getOriDownloadUrl()).fit().into(fishPhoto_ori);
        Picasso.get().load(currentPost.getMeaDownloadUrl()).rotate(90).fit().into(fishPhoto_mea);
    }

    private void readComment() {

        ChildEventListener postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);

                LinearLayout item_view = new LinearLayout(PostDetailActivity.this);
                LinearLayout head_view = new LinearLayout(PostDetailActivity.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout.LayoutParams head_lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                head_lp.setMargins(10, 10,10, 30);

                item_view.setLayoutParams(lp);
                item_view.setOrientation(LinearLayout.VERTICAL);

                head_view.setLayoutParams(head_lp);
                head_view.setOrientation(LinearLayout.HORIZONTAL);

                ImageView img_ava = new ImageView(PostDetailActivity.this);
                TextView txt_user_name = new TextView(PostDetailActivity.this);

                ViewGroup.LayoutParams head_img_lp = new ViewGroup.LayoutParams(Common.dpToPx(PostDetailActivity.this, 60), Common.dpToPx(PostDetailActivity.this, 60));
                ViewGroup.LayoutParams head_name_lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                img_ava.setLayoutParams(head_img_lp);
                txt_user_name.setLayoutParams(head_name_lp);

                img_ava.setPadding(10, 10, 10, 10);

                txt_user_name.setTextSize(18);
                txt_user_name.setTextColor(Color.BLACK);

                head_view.addView(img_ava);
                head_view.addView(txt_user_name);

                TextView content = new TextView(PostDetailActivity.this);

                ViewGroup.LayoutParams content_lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                content.setLayoutParams(content_lp);

                content.setGravity(Gravity.CENTER);

                TextView com_time = new TextView(PostDetailActivity.this);

                com_time.setLayoutParams(content_lp);

                com_time.setGravity(Gravity.RIGHT);

                content.setTextColor(Color.BLACK);
                content.setTextSize(18);
                content.setTypeface(null, Typeface.ITALIC);


                item_view.addView(head_view);
                item_view.addView(content);
                item_view.addView(com_time);


                DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(comment.userId);
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (!(user.getImagePath().equals(Common.NA))){
                            Picasso.get().load(user.getImagePath()).fit().into(img_ava);
                        } else {
                            img_ava.setImageResource(R.drawable.people);
                        }

                        txt_user_name.setText(user.getDisplayName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(PostDetailActivity.this,"Cannot load comment user info!",Toast.LENGTH_SHORT).show();
                    }
                };

                databaseUser.addListenerForSingleValueEvent(userListener);

                content.setText('"' + comment.content + '"');
                com_time.setText(Common.timeStampToTime(comment.timeStamp));

                if (itemCount % 2 == 0) {
                    item_view.setBackgroundColor(Color.parseColor("#E6E6E6"));
                    content.setBackgroundColor(Color.parseColor("#C0C0C0"));
                } else {
                    content.setBackgroundColor(Color.parseColor("#F5F5F5"));
                }

                commentLinearLayout.addView(item_view, 0);

                itemCount++;
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


        commentDBRef.orderByChild("timeStamp").addChildEventListener(postListener);
    }
}
