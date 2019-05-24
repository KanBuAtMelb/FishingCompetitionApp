package com.example.fishingtest.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.fishingtest.Model.Common.currentCompItem;


public class ViewCompDetailsActivity extends AppCompatActivity {

    TextView cTitle;
    TextView cReward;
    TextView cDate;
    TextView cStartTime;
    TextView cStopTime;
    TextView cGeo;
    TextView cType;
    TextView cWinner;
    TextView cResult;
    TextView cAttendants;
    TextView cDescription;

    FirebaseUser fbUser;

    Button btn_newPost;
    Button btn_viewPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_details);

        cTitle = (TextView) findViewById(R.id.viewcomp_title);
        cReward = (TextView) findViewById(R.id.viewComp_comp_reward);
        cDate= (TextView) findViewById(R.id.viewComp_comp_date);
        cStartTime= (TextView) findViewById(R.id.viewComp_comp_start_time);
        cStopTime= (TextView) findViewById(R.id.viewComp_comp_stop_time);
        cGeo= (TextView) findViewById(R.id.viewComp_comp_geo);
        cType = (TextView) findViewById(R.id.viewComp_comp_type);
        cWinner = (TextView) findViewById(R.id.viewComp_comp_winner);
        cResult = (TextView) findViewById(R.id.viewComp_comp_result);
        cAttendants = (TextView) findViewById(R.id.viewComp_comp_attendants);
        cDescription = (TextView) findViewById(R.id.viewComp_comp_description);
        btn_newPost = (Button)findViewById(R.id.viewComp_PostButton);
        btn_viewPosts = (Button)findViewById(R.id.viewComp_ViewPostButton);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            finish();
        }

        if(currentCompItem != null){
            currentCompItem.checkArrayList();
            cTitle.setText(currentCompItem.getCname());
            cReward.setText("Reward: $"+Integer.toString(currentCompItem.getReward()) + " AUD");
            cDate.setText("Date: " + currentCompItem.getDate());
            cStartTime.setText("Start Time: " + currentCompItem.getStartTime());
            cStopTime.setText("Ending Time: " + currentCompItem.getStopTime());
            cGeo.setText("Geo: " + currentCompItem.getGeo_map());
            cWinner.setText("Winner: " + currentCompItem.getWinner());
            cResult.setText("Result: " + currentCompItem.getResults());
            cDescription.setSingleLine(false);
            cDescription.setText("Description: \n" + currentCompItem.getcDescription());

            // Spinner item index to text view
            String[] compTypes = getResources().getStringArray(R.array.comp_type);
            if(currentCompItem.getCompType() != Common.EMPTY_SPINNER)
                cType.setText("Competition Type: "+compTypes[currentCompItem.getCompType()]);
            else
                cType.setText("Competition Type: " + Common.EMPTY);

            // Convert Attendant list to total number
            cAttendants.setText("Attendant number : " + Integer.toString(currentCompItem.getAttendants().size()));



            if (currentItem.getcStatus().equals("1") && checkWhetherReigstered()) {
                btn_newPost.setVisibility(View.VISIBLE);
            } else {
                btn_newPost.setVisibility(View.INVISIBLE);
            }


            if (currentItem.getcStatus().equals("2") || currentItem.getcStatus().equals("1") || currentItem.getcStatus().equals("3") ) {
                btn_viewPosts.setVisibility(View.VISIBLE);
            } else {
                btn_viewPosts.setVisibility(View.INVISIBLE);
            }

            // Click Button to add new post
            btn_newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewCompDetailsActivity.this, ShotPreviewActivity.class);
                    intent.putExtra("currentComp", currentCompItem);
                    startActivity(intent);
                }
            });

            // Click Button to view posts
            btn_viewPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewCompDetailsActivity.this, ViewPostsActivity.class);
                    intent.putExtra("currentComp", currentCompItem);
                    startActivity(intent);
                }
            });

            // Button only visible for competition in progress
            // Todo: Ziqi to do visible for competition in progress
        }

    }

    @Override
    public void onBackPressed() {
        Intent backHome = new Intent(this, HomePageActivity.class);
        this.finish();
        startActivity(backHome);
//        super.onBackPressed();
    }

    private boolean checkWhetherReigstered() {
        if (currentItem.getAttendants().contains(fbUser.getUid())) {
            return true;
        }
        return false;
    }
}
