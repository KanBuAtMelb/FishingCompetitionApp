package com.example.fishingtest.Service;

import com.example.fishingtest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DebugUtils;
import android.util.Log;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;

import java.util.List;
import java.util.concurrent.Executor;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    DatabaseReference databaseGPS;
    String currentUserID;
    Location currentLoc;
    private static final int PERMISSIONS_REQUEST = 100;


    private final class MyLocationListener implements LocationListener {

        public MyLocationListener() {
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e("KB", "onLocationChanged" + location.toString());
            currentLoc = location;
            databaseGPS.setValue(currentLoc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("KB", "onLocationChanged" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("KB", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("KB", "onProviderDisabled");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        android.os.Debug.waitForDebugger(); // For Debugging


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> list = locationManager.getProviders(true);

        if (list != null) {
            for (String x : list) {
                Log.e("KB", "name: " + x);
            }
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        String mProvider = locationManager.getBestProvider(criteria, true);
        if (mProvider != null) {
            Log.e("KB", "mProvider:" + mProvider);
        }


        MyLocationListener locationListener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 10, locationListener);

        //Firebase
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseGPS = FirebaseDatabase.getInstance().getReference().child("Live_GPS").child(currentUserID);

        Log.e("KB", "onCreate() executed");

    }


    @Override
    public void onDestroy() {

        databaseGPS.removeValue();
        super.onDestroy();
        Log.e("KB","onDestroy() executed");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("KB","onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }



    //TODO: NOT WORKING, MAYBE DELETED
    //Create the persistent notification//
    private void buildNotification() {
        String stop = "stop";

        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification//
        String channelID = "GPS Enabling";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_edit_location_black_24dp);
        startForeground(13145, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            //Stop the Service//
            stopSelf();
        }
    };


    //Initiate the request to track the device's location// TODO: NOT WORKING, MAYBE DELETED
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device’s location//
        request.setInterval(0); //TODO: only for test, to be removed later
        request.setNumUpdates(1); // TODO: only for test, to be removed later

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {

            //then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, so your app can perform read and write operations//
                    Location location = locationResult.getLastLocation();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Locations").child(currentUserID);

                    if (location != null) {
                        //Save the location data to the database//
                        ref.setValue(location);
                    }
                }
            }, null);
        }
    }
}