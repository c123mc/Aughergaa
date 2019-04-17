package com.conor.aughergaa;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Object.MyBook;

import java.util.ArrayList;

public class MyBooking extends AppCompatActivity {


    ListView myBooked_list;
    ArrayList<MyBook> myBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar!=null;

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FABE44")));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setTitle("My Bookings");

        myBooked_list = (ListView) findViewById(R.id.myBooked_list);

        final ProgressDialog progressDialog = new ProgressDialog(MyBooking.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child("Booking").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myBooks = new ArrayList<>();
                for (DataSnapshot room:dataSnapshot.getChildren()){
                    for (DataSnapshot user: room.getChildren()){
                        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
                        if (preferences.getString("Email","").equals("admin@aughergaa.com")){
                            for (DataSnapshot booking : user.child("Booking").getChildren()) {
                                myBooks.add(new MyBook(room.getKey(),booking.getKey(),Long.parseLong(booking.child("start").getValue().toString()),Long.parseLong(booking.child("end").getValue().toString()),user.getKey(),user.child("Name").getValue().toString()));
                            }
                        }else {
                            Log.d("LOGGGGG",user.getKey()+"               "+ FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if (user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                for (DataSnapshot booking : user.child("Booking").getChildren()) {
                                    myBooks.add(new MyBook(room.getKey(), booking.getKey(), Long.parseLong(booking.child("start").getValue().toString()), Long.parseLong(booking.child("end").getValue().toString()),user.getKey(),user.child("Name").getValue().toString()));
                                }
                            }
                        }
                    }
                }

                progressDialog.dismiss();
                myBooked_list.setAdapter(new com.conor.aughergaa.Adapters.MyBooking(myBooks,MyBooking.this,3));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(MyBooking.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
