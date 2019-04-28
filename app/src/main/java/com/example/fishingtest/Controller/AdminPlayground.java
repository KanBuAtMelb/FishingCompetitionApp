package com.example.fishingtest.Controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.fishingtest.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminPlayground extends AppCompatActivity {

    DatabaseReference databaseGPS;
    FirebaseDatabase user_gps;
    String currentUserID;
    Location currentLoc;
    private static final int PERMISSIONS_REQUEST = 100;

    TextView lat;
    TextView lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_playground);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lat =(TextView) findViewById(R.id.admin_playground_lat);
        lon =(TextView) findViewById(R.id.admin_playground_lon);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> list = locationManager.getProviders(true);

        if (list != null) {
            for (String x : list) {
                Log.e("gzq", "name: " + x);
            }
        }

        LocationProvider lpGps = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        LocationProvider lpNet = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
        LocationProvider lpPsv = locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);


        Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);

        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        String mProvider = locationManager.getBestProvider(criteria, true);
        if (mProvider != null) {
            Log.e("gzq", "mProvider:" + mProvider);
        }

        MyLocationListener locationListener = new MyLocationListener();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //If the app doesn’t currently have access to the user’s location, then request access
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 10, locationListener);
        }



        //Firebase
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseGPS = FirebaseDatabase.getInstance().getReference().child("Live_GPS").child(currentUserID);

    }

    @Override
    public void onBackPressed() {

        databaseGPS.removeValue();
        finish();
        super.onBackPressed();
    }

    private final class MyLocationListener implements LocationListener {

        public MyLocationListener(){
        }


        @Override
        public void onLocationChanged(Location location) {
            Log.e("gzq","onLocationChanged" + location.toString());
            lat.setText("Latitude: " +Double.toString(location.getLatitude()));
            lon.setText("Longitude: " + Double.toString(location.getLongitude()));

            databaseGPS.setValue(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("gzq","onLocationChanged" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("gzq","onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("gzq","onProviderDisabled");
        }
    }

}

