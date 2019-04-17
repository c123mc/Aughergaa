package com.conor.aughergaa;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Booking extends AppCompatActivity {

    ImageView selected_room_image;
    TextView selected_room_title;
    AppCompatButton btn_create_booking,booking_list;
    int selected_room;

    TextView start_timing,end_timing;
    Calendar startTime;
    Calendar endTime;

    TextView booking_heading,detailed_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

/*        booking_heading = (TextView) findViewById(R.id.booking_heading);
        end_timing = (TextView) findViewById(R.id.end_timing);
        start_timing = (TextView) findViewById(R.id.start_timing);
        start_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);
        end_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);*/



        Intent intent = getIntent();
        selected_room_image = (ImageView) findViewById(R.id.selected_room_image);
        detailed_text = (TextView) findViewById(R.id.detailed_text);
        selected_room_title = (TextView) findViewById(R.id.selected_room_title);
        selected_room_title.setText(intent.getStringExtra("Title"));
        Glide.with(Booking.this).load(intent.getStringExtra("URL")).into(selected_room_image);
        detailed_text.setText(intent.getStringExtra("Description"));
        btn_create_booking = (AppCompatButton) findViewById(R.id.btn_create_booking);
        booking_list = (AppCompatButton) findViewById(R.id.booking_list);

        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
        if (preferences.getString("Email","").equals("admin@aughergaa.com")){
//            btn_create_booking.setVisibility(View.GONE);
//            start_timing.setVisibility(View.GONE);
//            end_timing.setVisibility(View.GONE);
//            booking_heading.setVisibility(View.GONE);
        }

/*        booking_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Booking.this,RoomBooking.class);
                intent.putExtra("room",selected_room);
                startActivity(intent);
            }
        });*/


/*        start_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBooking();
            }
        });

        end_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndingDate();
            }
        });*/
/*
        btn_create_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (end_timing.getText().toString().isEmpty()){
                    Toast.makeText(Booking.this, "Enter Checkout Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (start_timing.getText().toString().isEmpty()){
                    Toast.makeText(Booking.this, "Enter Checkin Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date now = new Date();
                Date checkin = startTime.getTime();
                Date checkout = endTime.getTime();
                if (checkin.before(now)) {
                    Toast.makeText(Booking.this, "You choosed Past Check in time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkout.before(now) || checkout.before(checkin)){
                    Toast.makeText(Booking.this, "You choosed Wrong Check out time", Toast.LENGTH_SHORT).show();
                    return;
                }

                /////////////////////////
                search_for_availablity();

            }
        });*/

    }

    void search_for_availablity(){

        final ProgressDialog dialog = new ProgressDialog(Booking.this,R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Checking for Booking.");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Booking").child("Rooms"+selected_room);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date start_date = startTime.getTime();
                Date end_date = endTime.getTime();
                boolean isFound = false;
                if (dataSnapshot.exists()){
                    for (DataSnapshot snap:dataSnapshot.getChildren()){
                        for (DataSnapshot sn_ap:snap.child("Booking").getChildren()){
                            Calendar start_time = Calendar.getInstance();
                            start_time.setTimeInMillis(Long.parseLong(Objects.requireNonNull(sn_ap.child("start").getValue()).toString()));
                            Calendar end_time = Calendar.getInstance();
                            end_time.setTimeInMillis(Long.parseLong(Objects.requireNonNull(sn_ap.child("end").getValue()).toString()));

                            Date start_upper_date = start_time.getTime();
                            Date end_upper_date = end_time.getTime();

                            if (start_date.before(end_upper_date) || end_date.before(start_upper_date)){
                                isFound = true;
                            }
                        }
                    }
                }

                if (!isFound){
                    String uid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Booking").child("Rooms"+selected_room).child(uid).child("Booking").push();
                    reference.child("start").setValue(startTime.getTimeInMillis());
                    reference.child("end").setValue(endTime.getTimeInMillis());
                    SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
                    FirebaseDatabase.getInstance().getReference().child("Booking").child("Rooms"+selected_room).child(uid).child("Name").setValue(preferences.getString("Name",""));

                    Toast.makeText(Booking.this, "Booking Successful.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Booking.this, "Booking cannot succeed. Time Conflict Occur", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(Booking.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void startBooking(){
        final Calendar c = Calendar.getInstance();


        //////////////////////////////   Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {


                        ////////////////////////////////    Time Picker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Booking.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        startTime = Calendar.getInstance();
                                        startTime.set(year,monthOfYear,dayOfMonth,hourOfDay,minute);
                                        start_timing.setText("Checkin Time: "+dayOfMonth+"-"+(monthOfYear+1)+"-"+year+" "+hourOfDay+":"+minute+":00");

                                    }
                                }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
                        timePickerDialog.show();


                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    void getEndingDate(){
        final Calendar c = Calendar.getInstance();
        //////////////////////////////   Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {


                        ////////////////////////////////    Time Picker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Booking.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        endTime = Calendar.getInstance();
                                        endTime.set(year,monthOfYear,dayOfMonth,hourOfDay,minute);

                                        end_timing.setText("Checkout Time: "+dayOfMonth+"-"+(monthOfYear+1)+"-"+year+" "+hourOfDay+":"+minute+":00");

                                    }
                                }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
                        timePickerDialog.show();


                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
