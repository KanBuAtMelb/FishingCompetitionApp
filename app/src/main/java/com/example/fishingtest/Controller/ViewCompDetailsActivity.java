package com.example.fishingtest.Controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.R;

import org.w3c.dom.Text;

import java.io.Serializable;

import static com.example.fishingtest.Model.Common.currentItem;


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



        if(currentItem != null){
            currentItem.checkArrayList();
            cTitle.setText(currentItem.getCname());
            cReward.setText("Reward: $"+Integer.toString(currentItem.getReward()) + " AUD");
            cDate.setText("Date: " + currentItem.getDate());
            cStartTime.setText("Start Time: " + currentItem.getStartTime());
            cStopTime.setText("Ending Time: " + currentItem.getStopTime());
            cGeo.setText("Geo: " + currentItem.getGeo_map());
            cWinner.setText("Winner: " +currentItem.getWinner());
            cResult.setText("Result: " + currentItem.getResults());
            cDescription.setSingleLine(false);
            cDescription.setText("Description: \n" +currentItem.getcDescription());

            // Spinner item index to text view
            String[] compTypes = getResources().getStringArray(R.array.comp_type);
            if(currentItem.getCompType() != Common.EMPTY_SPINNER)
                cType.setText("Competition Type: "+compTypes[currentItem.getCompType()]);
            else
                cType.setText("Competition Type: " + Common.EMPTY);

            // Convert Attendant list to total number
            cAttendants.setText("Attendant number : " + Integer.toString(currentItem.getAttendants().size()));


            // Click Button to add new post
            btn_newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewCompDetailsActivity.this, ShotPreviewActivity.class);
                    intent.putExtra("currentComp", currentItem);
                    startActivity(intent);
                }
            });

            // Click Button to view posts
            btn_viewPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewCompDetailsActivity.this, ViewPostsActivity.class);
                    intent.putExtra("currentComp", currentItem);
                    startActivity(intent);
                }
            });


            // Button only visible for competition in progress




            // Display Posts
            // Todo: Ziqi to do the view of post recycleview


        }

    }

    @Override
    public void onBackPressed() {
        Intent backHome = new Intent(this, HomePageActivity.class);
        this.finish();
        startActivity(backHome);
//        super.onBackPressed();
    }
}
