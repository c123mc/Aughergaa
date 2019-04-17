package com.conor.aughergaa;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Adapters.TabAdapter;
import com.conor.aughergaa.Classes.MyMatchClass;
import com.conor.aughergaa.Classes.Utils;
import com.conor.aughergaa.Fragments.FixtureResults;
import com.conor.aughergaa.Fragments.Results;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FixturesResults extends AppCompatActivity {


    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures_results);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Fixtures And Results");


        final ProgressDialog dialog = new ProgressDialog(FixturesResults.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child("Fixture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                Utils.fixtures = new ArrayList<>();
                Utils.results = new ArrayList<>();
                if (dataSnap.exists()) {
                    for (DataSnapshot dataSnapshot : dataSnap.getChildren()) {
                        if (dataSnapshot.child("name_team_1").exists() && dataSnapshot.child("name_team_2").exists() &&
                                dataSnapshot.child("times_stamp").exists()) {

                            if (!isBefore((dataSnapshot.child("times_stamp").getValue().toString()))) {
                                Utils.results.add(new MyMatchClass(dataSnapshot.child("name_team_1").getValue().toString(),
                                        dataSnapshot.child("name_team_2").getValue().toString(),
                                        Long.parseLong(dataSnapshot.child("times_stamp").getValue().toString()),dataSnapshot.getKey()));
                            } else {
                                Utils.fixtures.add(new MyMatchClass(dataSnapshot.child("name_team_1").getValue().toString(),
                                        dataSnapshot.child("name_team_2").getValue().toString(),
                                        Long.parseLong(dataSnapshot.child("times_stamp").getValue().toString()),dataSnapshot.getKey()));
                            }
                        }
                    }
                }
                viewPager = (ViewPager) findViewById(R.id.viewPager);
                tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                adapter = new TabAdapter(getSupportFragmentManager());
                adapter.addFragment(new FixtureResults(), "Fixtures");
                adapter.addFragment(new Results(), "Results");
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(FixturesResults.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    boolean isBefore(String s){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(s));
        Log.d("time_____________",c.getTime()+"");
        return c.getTime().after(new Date());
    }
}
