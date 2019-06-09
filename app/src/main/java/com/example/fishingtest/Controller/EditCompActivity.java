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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Adapter.EditCompListAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * The controller for the "Edit Competitions" activity for the Administrator.
 */

public class EditCompActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    // Competition UI
    EditText cName;
    EditText cReward;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    Spinner cType;
    EditText cWinner;
    EditText cResult;
    EditText cGeo;
    EditText cDescription;
    ListView cListView;

    ImageButton cImage;
    Button cAddImage;
    Button cUpdate;

    // Firebase
    DatabaseReference databaseComps;
    ArrayList<Competition> compList;
    String compID;
    String image_url;
    String cStatus;
    ArrayList<String> attendants;

    // For Competition Image uploading
    static final int COMP_IMAGE_GALLERY = 22;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comp);

        // Initialize the local variables
        compID = new String();
        image_url = new String();
        cStatus = new String();
        attendants = new ArrayList<>();

        // Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the Comp List filled by last Firebase database change
                compList.clear();
                // Fill the Comp List with the latest database snapshot
                for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    comp.checkArrayList();
                    compList.add(comp);
                }

                EditCompListAdapter compListAdapter = new EditCompListAdapter(EditCompActivity.this, compList);
                cListView.setAdapter(compListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // UI views
        cName = (EditText) findViewById(R.id.update_comp_name);
        cReward = (EditText) findViewById(R.id.update_comp_reward);
        cDate= (EditText) findViewById(R.id.update_comp_date);
        cStartTime= (EditText) findViewById(R.id.update_comp_start_time);
        cStopTime= (EditText) findViewById(R.id.update_comp_stop_time);
        cType = (Spinner) findViewById(R.id.update_comp_type);
        cWinner= (EditText) findViewById(R.id.update_comp_winner);
        cResult= (EditText) findViewById(R.id.update_comp_results);
        cGeo= (EditText) findViewById(R.id.update_comp_geo);
        cDescription = (EditText) findViewById(R.id.update_comp_description);
        cImage = (ImageButton) findViewById(R.id.update_comp_comp_image);

        // Set up Spinner
        cType.setSelection(0);

        // Set up List View of Competitions
        compList = new ArrayList<>();
        cListView = (ListView)findViewById(R.id.update_comp_listview);

        // Fill the view with values from the competition selected in the List View
        cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Competition comp = compList.get(position);

                cName.setText(comp.getCname());
                cReward.setText(Integer.toString(comp.getReward()));
                cDate.setText(comp.getDate());
                cStartTime.setText(comp.getStartTime());
                cStopTime.setText(comp.getStopTime());

                // Spinner
                cType.setSelection(comp.getCompType());
                cResult.setText(comp.getResults());
                cGeo.setText(comp.getGeo_map());
                cDescription.setText(comp.getcDescription());
                cWinner.setText(comp.getWinner());

                if(comp.getImage_url().equals(Common.NA)) {
                    cImage.setImageResource(R.drawable.ic_fish_black);
                    cImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                else{
                    // Set the customised competition image
                    Picasso.get().load(comp.getImage_url()).fit().into(cImage);
                }


                // Hold these invisible values
                compID = comp.getCompID();
                image_url = comp.getImage_url();
                cStatus = comp.getcStatus();

                if (comp.getAttendants().size() > 0)
                    attendants.clear();
                    attendants = comp.getAttendants();
            }
        });


        // Click on the ImageButton
        cImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, COMP_IMAGE_GALLERY);
            }
        });

        // Click Competition Date to select the date on a Calendar
        cDate.setShowSoftInputOnFocus(false);
        cDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        // Click the "Update Comp" button and update the Firebase database
        cUpdate = (Button) findViewById(R.id.admin_button_updateComp);
        cUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                update_comp();
            }
        });
    }

    // Successfully fetch the competition image from the database
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == COMP_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            cImage.setImageURI(imageUri);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("Comp_Images");
            StorageReference fileRef = imagesRef.child(compID);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditCompActivity.this, "Competition image uploaded!", Toast.LENGTH_LONG).show();
                    image_url = taskSnapshot.getDownloadUrl().toString();
                    databaseComps.child(compID).child("image_url").setValue(image_url);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Update the Competition database in Firebase with the new view data
    private void update_comp() {
        String name = cName.getText().toString().trim();
        String date = cDate.getText().toString().trim();
        int reward = Integer.parseInt(cReward.getText().toString().trim());
        String startT = cStartTime.getText().toString().trim();
        String stopT = cStopTime.getText().toString().trim();
        int type = cType.getSelectedItemPosition();
        String result = cResult.getText().toString().trim();
        String winner = cWinner.getText().toString().trim();
        String geo = cGeo.getText().toString().trim();
        String description = cDescription.getText().toString().trim();

        // Check the data before updating to the Firebase
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(date)||TextUtils.isEmpty(startT)||TextUtils.isEmpty(stopT)){
            Toast.makeText(this, "Please enter a competition name and date and times", Toast.LENGTH_SHORT).show();
        }else if(!(Common.verifyTime(startT) && Common.verifyTime(stopT))){
            Toast.makeText(this,"Please ensure the time stamps are in format of \"HH:mm\", applying 24 hrs",Toast.LENGTH_LONG).show();
        }else if(!Common.verifyGeoInfo(geo)){
            Toast.makeText(this,"Please ensure the geo information is in format of \"lat, long, radius\"",Toast.LENGTH_LONG).show();
        }else{

            // Update the Firebase
            Competition comp = new Competition(compID,name,reward,date,startT,stopT,geo,attendants,result,winner,type,description, image_url, cStatus);
            databaseComps.child(compID).setValue(comp);
            Toast.makeText(this,"Competition updated!",Toast.LENGTH_LONG).show();
            clear_textView();
        }
    }

    // Clear all view values
    private void clear_textView() {
        cName.setText(Common.EMPTY);
        cDate.setText(Common.EMPTY);
        cReward.setText(Common.EMPTY);
        cStartTime.setText(Common.EMPTY);
        cStopTime.setText(Common.EMPTY);
        cType.setSelection(Common.EMPTY_SPINNER);
        cResult.setText(Common.EMPTY);
        cWinner.setText(Common.EMPTY);
        cGeo.setText(Common.EMPTY);
        cDescription.setText(Common.EMPTY);
    }

    // Overridden method for Data Picker view after implement DatePickerDialog.OnDateSetListener
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
