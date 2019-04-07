package com.example.fishingtest.Controller;

import android.annotation.SuppressLint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.R;

import org.w3c.dom.Text;


public class RegisterCompActivity extends AppCompatActivity {

    TextView cName;
    TextView cDate;
    TextView cStartTime;
    TextView cStopTime;
    TextView cStatus;
    TextView cGeo;
    FloatingActionButton cAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_comp);


        cName = (TextView) findViewById(R.id.register_comp_name);
        cDate= (TextView) findViewById(R.id.register_comp_date);
        cStartTime= (TextView) findViewById(R.id.register_comp_start_time);
        cStopTime= (TextView) findViewById(R.id.register_comp_stop_time);
        cStatus = (TextView) findViewById(R.id.register_comp_status);
        cGeo= (TextView) findViewById(R.id.register_comp_geo);
        cAdd = (FloatingActionButton) findViewById(R.id.floating_button_comp);

        cName.setText(Common.currentItem.getCname());

    }
}
