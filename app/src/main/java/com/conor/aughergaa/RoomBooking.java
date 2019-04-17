package com.conor.aughergaa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Adapters.RoomBookAdapter;
import com.conor.aughergaa.Object.RoomBook;

import java.util.ArrayList;

public class RoomBooking extends AppCompatActivity {

    ListView booked_list;
    ArrayList<RoomBook> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar!=null;

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E43F3F")));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        booked_list = (ListView) findViewById(R.id.booked_list);

        final ProgressDialog dialog = new ProgressDialog(RoomBooking.this,R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Checking for Booking.");
        dialog.show();

        Intent intent = getIntent();

        int selected_room = intent.getIntExtra("room",-1);

        if (selected_room == -1){
            finish();
        }

        mActionBar.setTitle("Room # "+selected_room);

        FirebaseDatabase.getInstance().getReference().child("Booking").child("Rooms"+selected_room).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    for (DataSnapshot snap_1:snap.child("Booking").getChildren()){
                        if (snap_1.exists()){
                            list.add(new RoomBook(snap.child("Name").getValue().toString(),Long.parseLong(snap_1.child("start").getValue().toString()),Long.parseLong(snap_1.child("end").getValue().toString())));
                        }
                    }
                }
                booked_list.setAdapter(new RoomBookAdapter(list,RoomBooking.this));
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(RoomBooking.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
