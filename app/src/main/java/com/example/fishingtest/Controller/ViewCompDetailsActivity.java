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

        if(currentItem != null){
            currentItem.checkArrayList();
            cTitle.setText(currentItem.getCname());
            cReward.setText(currentItem.getReward());
            cDate.setText(currentItem.getDate());
            cStartTime.setText(currentItem.getStartTime());
            cStopTime.setText(currentItem.getStopTime());
            cGeo.setText(currentItem.getGeo_map());
            cWinner.setText(currentItem.getWinner());
            cResult.setText(currentItem.getResults());
            cDescription.setText(currentItem.getcDescription());
            // Spinner item index to text view
            String[] compTypes = getResources().getStringArray(R.array.comp_type);
            if(currentItem.getCompType() != Common.EMPTY_SPINNER)
                cType.setText(compTypes[currentItem.getCompType()]);
            else
                cType.setText(Common.EMPTY);

            // Convert Attendant list to total number
            cAttendants.setText(Integer.toString(currentItem.getAttendants().size()));


            // Display Posts

        }

    }
}
