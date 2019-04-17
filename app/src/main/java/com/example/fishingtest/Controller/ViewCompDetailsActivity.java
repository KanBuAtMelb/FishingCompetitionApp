package com.example.fishingtest.Controller;

import android.annotation.SuppressLint;
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
    RecyclerView cPosts;


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

        cPosts = (RecyclerView) findViewById(R.id.viewComp_posts);

        if(Common.currentItem != null){
            Common.currentItem.checkArrayList();
            cTitle.setText(Common.currentItem.getCname());
            cReward.setText(Common.currentItem.getReward());
            cDate.setText(Common.currentItem.getDate());
            cStartTime.setText(Common.currentItem.getStartTime());
            cStopTime.setText(Common.currentItem.getStopTime());
            cGeo.setText(Common.currentItem.getGeo_map());
            cType.setText(Common.currentItem.getCompType());
            cWinner.setText(Common.currentItem.getWinner());
            cResult.setText(Common.currentItem.getResults());
            // Convert Integer to String
            cAttendants.setText(Integer.toString(Common.currentItem.getAttendants().size()));
            cDescription.setText(Common.currentItem.getcDescription());

            // Display Posts

        }

    }
}
