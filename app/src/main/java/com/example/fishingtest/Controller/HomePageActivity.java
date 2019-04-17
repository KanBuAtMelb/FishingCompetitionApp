package com.example.fishingtest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fishingtest.Model.User;
import com.example.fishingtest.Adapter.ViewPagerAdapter;
import com.example.fishingtest.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Set tag to the view??
    private static  final String TAG = "MainPageActivity";

    // Toolbar
    Toolbar mToolbar;

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

    // Posts time
    Timestamp currStamp;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        // Toolbar
        mToolbar =  findViewById(R.id.main_top_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("HOME");

        // NavigationView setup
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // TabLayout and Viewpager
        //TabLayout and ViewPager
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
        myDrawerlayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, myDrawerlayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    // Toolbar items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_toolbar,menu);
        return true;
    }
    // Toolbar item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.toolbar_notification:
                //TODO: a Pop-up window for new coming event?? Any Firebase service to be used?

                return true;

            default:
                // If the user's action was not recognized
                // Invoke the superclass to handle it
                return super.onOptionsItemSelected(item);
        }
    }


    // Side Navigation bar Item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.side_nav_profile) {
            //Transfer to EditUserActivity
//            Intent profileIntent = new Intent(this, EditUserActivity.class);
//            startActivity(profileIntent);
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);

        } else if (id == R.id.side_nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
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

}

