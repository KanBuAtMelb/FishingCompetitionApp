package com.example.fishingtest.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishingtest.Adapter.EditCompListAdapter;
import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EditCompActivity extends AppCompatActivity {


    // Competition UI
    EditText cName;
    EditText cReward;
    EditText cDate;
    EditText cStartTime;
    EditText cStopTime;
    Spinner cType;  // TODO: using diaglog window to replace spinner
    EditText cWinner;
    EditText cResult;
    EditText cGeo;
    EditText cDescription;
    ListView cListView;

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
    ImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_competition);

        compID = new String();
        image_url = new String();
        cStatus = new String();
        attendants = new ArrayList<>();



        // Firebase
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");

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


        // Set up Spinner
        cType.setSelection(0);

        // Set up List View of Competitions
        compList = new ArrayList<>();
        cListView = (ListView)findViewById(R.id.update_comp_listview);

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


                // Hold these invisible values
                compID = comp.getCompID();
                image_url = comp.getImage_url();
                cStatus = comp.getcStatus();
                if (comp.getAttendants().size() > 0)
                    attendants = comp.getAttendants();
            }
        });



        // Click "Upload Competition Image button"
        cAddImage = (Button) findViewById(R.id.admin_button_addCompImage);


        cAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, COMP_IMAGE_GALLERY);

                clear_textView();
            }
        });



        // Click the "Add Comp" button
        cUpdate = (Button) findViewById(R.id.admin_button_updateComp);

        cUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                update_comp();
                clear_textView();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == COMP_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
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

        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                compList.clear();
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

    }

    private void update_comp() {


        // TODO: FORMAT CHECKS TO BE DONE
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

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(date)||TextUtils.isEmpty(startT)||TextUtils.isEmpty(stopT)){

            Toast.makeText(this, "Please enter a competition name and date and times", Toast.LENGTH_SHORT).show();

        }else{

            Competition comp = new Competition(compID,name,reward,date,startT,stopT,geo,attendants,result,winner,type,description, image_url, cStatus);
            databaseComps.child(compID).setValue(comp);
        }
    }

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

}
