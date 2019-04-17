package com.conor.aughergaa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.conor.aughergaa.Fragments.Calender;
import com.conor.aughergaa.Fragments.Checkout;
import com.conor.aughergaa.Fragments.HomeFragment;
import com.conor.aughergaa.Fragments.Lotto;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    RelativeLayout maps_layout,other_frams;
    Button send_query;
    int current_fragment = 0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        setSupportActionBar(toolbar);

        maps_layout = (RelativeLayout) findViewById(R.id.maps_layout);
        send_query = (Button) findViewById(R.id.start_contact);
        other_frams = (RelativeLayout) findViewById(R.id.other_frams);
        maps_layout.setVisibility(View.GONE);

        send_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this,Contact.class));
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(LandingActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView name = (TextView) headerView.findViewById(R.id.name);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
        name.setText(preferences.getString("Name",""));
        email.setText(preferences.getString("Email",""));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (current_fragment!=0){
                maps_layout.setVisibility(View.GONE);
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram, new HomeFragment());
                fragmentTransaction.commit();
                current_fragment = 0;
            }else
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.landing, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        current_fragment = 1;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            getSupportActionBar().setTitle("Home");
            maps_layout.setVisibility(View.GONE);
            other_frams.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram, new HomeFragment());
            fragmentTransaction.commit();
            current_fragment = 0;

        } else if (id == R.id.nav_gallery) {

            getSupportActionBar().setTitle("Booking");

            other_frams.setVisibility(View.VISIBLE);
            maps_layout.setVisibility(View.GONE);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram, new com.conor.aughergaa.Fragments.MyBooking());
            fragmentTransaction.commit();

        } else if (id == R.id.nav_slideshow) {


            maps_layout.setVisibility(View.GONE);
            startActivity(new Intent(LandingActivity.this,Profile.class));

        } else if (id == R.id.nav_manage) {


            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(LandingActivity.this,LoginActivity.class));
            finish();
        }else if (id == R.id.location){
            getSupportActionBar().setTitle("Contact Us");
            maps_layout.setVisibility(View.VISIBLE);
            other_frams.setVisibility(View.GONE);

        }else if (id == R.id.social){
            startActivity(new Intent(LandingActivity.this,SocialMedia.class));
        }else if (id == R.id.calender){

            getSupportActionBar().setTitle("Calender");

            other_frams.setVisibility(View.VISIBLE);
            maps_layout.setVisibility(View.GONE);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram, new Calender());
            fragmentTransaction.commit();

        }
        else if (id == R.id.checkout){
            getSupportActionBar().setTitle("Checkout");

            other_frams.setVisibility(View.VISIBLE);
            maps_layout.setVisibility(View.GONE);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram, new Checkout());
            fragmentTransaction.commit();

        }else if (id == R.id.lotto){
            getSupportActionBar().setTitle("Club Lotto");

            other_frams.setVisibility(View.VISIBLE);
            maps_layout.setVisibility(View.GONE);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram, new Lotto());
            fragmentTransaction.commit();
        }else if (id == R.id.fixandres){
            startActivity(new Intent(LandingActivity.this,FixturesResults.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng Augher = new LatLng(54.437981, -7.134183);
        googleMap.addMarker(new MarkerOptions().position(Augher).title("Augher St. Macartan's"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Augher));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.437981, -7.134183), 12.0f));
    }


}
