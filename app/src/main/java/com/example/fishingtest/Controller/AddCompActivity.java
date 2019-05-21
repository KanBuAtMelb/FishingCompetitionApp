package com.example.fishingtest.Controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddCompActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{


    // Competition UI
    EditText cName;
    EditText cReward;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    EditText cGeo;
    Spinner cType;
    EditText cDescription;
    Button cAdd;

    // Firebase
    DatabaseReference databaseComps;
    ArrayList<Competition> compList;
    ArrayList<String> attendants = new ArrayList<>();
    ArrayList<String> winners = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_competition);

        ArrayList<String> winners = new ArrayList<>();


        // Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");

        cName = (EditText) findViewById(R.id.add_comp_name);
        cReward = (EditText) findViewById(R.id.add_comp_reward);
        cDate= (EditText) findViewById(R.id.add_comp_date);
        cStartTime= (EditText) findViewById(R.id.add_comp_start_time);
        cStopTime= (EditText) findViewById(R.id.add_comp_stop_time);
        cType = (Spinner) findViewById(R.id.add_comp_type);
        cGeo= (EditText) findViewById(R.id.add_comp_geo);
        cDescription = (EditText) findViewById(R.id.add_comp_description);


        // Click Competition Date to select the date on a Calendar
        cDate.setShowSoftInputOnFocus(false);
        cDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        cAdd = (Button) findViewById(R.id.admin_button_addComp);

        // Click the "Add Comp" button
        cAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Add a new competition to Firebase
                add_comp();
            }
        });
    }


    private void add_comp() {

        // TODO: Check input format, espeically date and time, dollar value and geo info
        String name = cName.getText().toString().trim();
        String date = cDate.getText().toString().trim();
        int reward = 0;
        int type = 0;
        try{
            reward = Integer.parseInt(cReward.getText().toString().trim());
            type = cType.getSelectedItemPosition();
        }catch(Exception e){
            Toast.makeText(this, "Please enter a competition reward (as an integer) and type", Toast.LENGTH_SHORT).show();
        }

        String startT = cStartTime.getText().toString().trim();
        String stopT = cStopTime.getText().toString().trim();

        String geo = cGeo.getText().toString().trim();
        String description = cDescription.getText().toString().trim();


        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(date)||TextUtils.isEmpty(startT)||TextUtils.isEmpty(stopT)){

            Toast.makeText(this, "Please enter a competition name and date and times", Toast.LENGTH_SHORT).show();

        }else if(!(Common.verifyTime(startT) && Common.verifyTime(stopT))){
            Toast.makeText(this,"Please ensure the time stamps are in format of \"HH:mm\", applying 24 hrs",Toast.LENGTH_LONG).show();
        }else if(!Common.verifyGeoInfo(geo)){
            Toast.makeText(this,"Please ensure the geo information is in format of \"lat, long, radius\"",Toast.LENGTH_LONG).show();
        }else{
            String id = databaseComps.push().getKey();

            Competition comp = new Competition(id,name,reward,date,startT,stopT,geo,type,description);
            databaseComps.child(id).setValue(comp);
            Toast.makeText(this,"Competition updated!",Toast.LENGTH_LONG).show();
            clear_textView();
        }
    }


    private void clear_textView(){
        cName.setText(Common.EMPTY);
        cDate.setText(Common.EMPTY);
        cReward.setText(Common.EMPTY);
        cStartTime.setText(Common.EMPTY);
        cStopTime.setText(Common.EMPTY);
        cType.setSelection(Common.EMPTY_SPINNER);
        cGeo.setText(Common.EMPTY);
        cDescription.setText(Common.EMPTY);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String compDate = format.format(c.getTime());

        cDate.setText(compDate);
    }
}
