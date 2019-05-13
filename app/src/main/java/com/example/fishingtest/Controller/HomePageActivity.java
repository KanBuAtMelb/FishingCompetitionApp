package com.example.fishingtest.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.Model.User;
import com.example.fishingtest.Adapter.ViewPagerAdapter;
import com.example.fishingtest.R;
import com.example.fishingtest.Service.TrackingService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Set tag to the view??
    private static  final String TAG = "MainPageActivity";
    private static final int PERMISSIONS_REQUEST = 100;



    // Toolbar
    Toolbar mToolbar;
    SearchView searchView;
    SearchView.SearchAutoComplete searchAutoComplete;
    ArrayAdapter<String> searchAdapter;

    // NavigationView setup
    NavigationView navigationView;

    //TabLayout and ViewPager
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    //Set up Drawer layout
    // DrawerLayout
    DrawerLayout myDrawerlayout;
    ActionBarDrawerToggle toggle;

    // User status
    public String currUserName;
    private String currentUserID;
    public User user;
    public boolean isLogin = false;

    // User's Firebase info
    DatabaseReference loginedUser;  // why no public or private??
    private FirebaseAuth myAuth;

    // Competition Firebase info
    DatabaseReference databaseComps;
    ArrayList<Competition> compList;
    List<String> nameList;


    // Posts time
    Timestamp currStamp;

    // GPS
    LocationManager locationManager;
    DatabaseReference databaseGPS;
    MyLocationListener locationListener;


    private final class MyLocationListener implements LocationListener {

        public MyLocationListener(){
        }


        @Override
        public void onLocationChanged(Location location) {
            Log.e("KB_Home","onLocationChanged" + location.toString());
            databaseGPS.setValue(location);
            Common.curLat = location.getLatitude();
            Common.curLon = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("KB_Home","onLocationChanged" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("KB_Home","onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("KB_Home","onProviderDisabled");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Toolbar
        mToolbar =  findViewById(R.id.main_top_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("HOME");
        // SearchView in Toolbar in onCreateOptionsMenu??
        nameList = new ArrayList<>();


        // Firebase
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseGPS = FirebaseDatabase.getInstance().getReference().child("Live_GPS").child(currentUserID);


        // -- Get current user
        loginedUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        loginedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    hideItem();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        compList = new ArrayList<>();
        databaseComps = FirebaseDatabase.getInstance().getReference("Competitions");
        databaseComps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                compList.clear();
                for(DataSnapshot compSnapshot : dataSnapshot.getChildren()){
                    Competition comp = compSnapshot.getValue(Competition.class);
                    compList.add(comp);
                }

                nameList = compList.stream()
                        .map(Competition::getCname)
                        .collect(Collectors.toList());

//                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: Do something here?
            }
        });



        // NavigationView setup
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // TabLayout and Viewpager
        tabLayout = findViewById(R.id.main_tablayout);
        viewPager = findViewById(R.id.main_viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Add fragments
        viewPagerAdapter.AddFragment(new DiscoveryFragment(),"Discovery");
        viewPagerAdapter.AddFragment(new MyCompetitionsFragment(), "My Competitions");
        // Set up adapter
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //Set up Drawer layout
        // DrawerLayout
//        hideItem();
        myDrawerlayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, myDrawerlayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> list = locationManager.getProviders(true);

        if (list != null) {
            for (String x : list) {
                Log.e("KB_Home", "name: " + x);
            }
        }

        Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        String mProvider = locationManager.getBestProvider(criteria, true);
        if (mProvider != null) {
            Log.e("KB_Home", "mProvider:" + mProvider);
        }

       locationListener = new MyLocationListener();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //If the app doesn’t currently have access to the user’s location, then request access
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,2,locationListener);
        }
    }

    private void hideItem()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        if(user.getAccessLevel().equals(Common.user_member)){
            nav_Menu.findItem(R.id.side_nav_admin).setVisible(false);
            nav_Menu.findItem(R.id.side_nav_admin_add_comp).setVisible(false);
            nav_Menu.findItem(R.id.side_nav_admin_update_comp).setVisible(false);
        }
    }


    // GPS request result
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //If the permission has been granted...//
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//
//            startTrackerService();


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener);
        } else {

            //If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }

    }

    // Toolbar items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_toolbar,menu);

        // Get the Search menu
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        // Get the searchview object
        searchView = (SearchView) searchItem.getActionView();
        searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(R.color.white);
        searchAdapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,nameList);
        searchAutoComplete.setAdapter(searchAdapter);

        // Listen to search view item on click event
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString = (String)parent.getItemAtPosition(position);
                searchAutoComplete.setText(queryString);
            }
        });

        // When submitting search query, find the competition for full detail display
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                for(int i = 0; i < compList.size(); i++){
                    if(compList.get(i).getCname().equals(s)){
                        Common.currentItem = compList.get(i);
                        startActivity(new Intent(HomePageActivity.this, ViewCompDetailsActivity.class));
                        finish();
                        break;
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return true;
    }




    // Side Navigation bar Item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.side_nav_profile) {
            //Transfer to EditUserActivity
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);

        } else if (id == R.id.side_nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // stop GPS service
//                            stopTrackerService();
                            locationManager.removeUpdates(locationListener);



                            // delete GPS Live data in Firebase
                            databaseGPS.removeValue();


                            // user is now signed out
                            startActivity(new Intent(HomePageActivity.this, LogInActivity.class));
                            finish();
                        }
                    });
        } else if(id == R.id.side_nav_admin_add_comp){
            Intent addCompIntent = new Intent(this, AddCompActivity.class);
            startActivity(addCompIntent);

        } else if(id == R.id.side_nav_admin_update_comp){
            Intent updateCompIntent = new Intent(this,UpdateCompActivity.class);
            startActivity(updateCompIntent);
        } else if(id == R.id.side_nav_admin_playground){
            Intent playGroundIntent = new Intent(this, AdminPlayground.class);
            startActivity(playGroundIntent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        android.support.v4.widget.DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    //Start the TrackerService//
    private void startTrackerService() {

        Intent startIntent=new Intent(this, TrackingService.class);
        startIntent.putExtra("UserID", currentUserID);
        this.startService(startIntent);

        //Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

    }

    //Stop the Tracakerservice
    private void stopTrackerService(){

        Intent stopIntent =new Intent(this, TrackingService.class);
        this.stopService(stopIntent);

        //Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking disabled", Toast.LENGTH_SHORT).show();
    }

}

