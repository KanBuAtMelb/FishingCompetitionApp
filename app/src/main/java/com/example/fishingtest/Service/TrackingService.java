package com.example.fishingtest.Service;

import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.concurrent.Executor;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();
    private String currentUserID;

    @Override
    public IBinder onBind(Intent intent) {
        currentUserID = (String)intent.getExtras().get("UserID");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        android.os.Debug.waitForDebugger(); // For Debugging
//        buildNotification();
//        requestLocationUpdates();

//        loginToFirebase();  // ???
    }

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
        startForeground(88, builder.build());
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

    private void loginToFirebase() {
        //Authenticate with Firebase, using the email and password we created earlier
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        //Call OnCompleteListener if the user is signed in successfully
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                //If the user has been authenticated...
                if (task.isSuccessful()) {

                    //...then call requestLocationUpdates
                    requestLocationUpdates();
                } else {

                    //If sign in fails, then log the error//
                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
    }

    //Initiate the request to track the device's location//
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
            client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            double wayLatitude = location.getLatitude();
                            double wayLongitude = location.getLongitude();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Listener failed");
                    }
                }

            );







//            //then request location updates//
//            client.requestLocationUpdates(request, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//
//                    //Get a reference to the database, so your app can perform read and write operations//
//                    Location location = locationResult.getLastLocation();
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Locations").child(currentUserID);
//
//                    if (location != null) {
//                        //Save the location data to the database//
//                        ref.setValue(location);
//                    }
//                }
//            }, null);
        }
    }
}