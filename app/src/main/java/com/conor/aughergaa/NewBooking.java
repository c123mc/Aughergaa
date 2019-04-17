package com.conor.aughergaa;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NewBooking extends AppCompatActivity {

    AppCompatButton btn_create_booking,booking_list;

    TextView start_timing,end_timing;
    Calendar startTime;
    Calendar endTime;

    TextView detailed_text;
    Spinner booking_heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);


        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar!=null;

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FABE44")));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setTitle("Create Booking");

        ArrayList<String> list = new ArrayList<>();
        list.add("Select Facility");
        list.add("Main Pitch");
        list.add("Training Pitch");
        list.add("Dressing Room 1");
        list.add("Dressing Room 2");
        list.add("Committee Room");
        list.add("Man Shed");
        list.add("Kitchen");

        booking_heading = (Spinner) findViewById(R.id.booking_heading);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        booking_heading.setAdapter(spinnerArrayAdapter);

        end_timing = (TextView) findViewById(R.id.end_timing);
        start_timing = (TextView) findViewById(R.id.start_timing);
        detailed_text = (TextView) findViewById(R.id.detailed_text);
        start_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);
        end_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);

        btn_create_booking = (AppCompatButton) findViewById(R.id.btn_create_booking);

        SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
        if (preferences.getString("Email","").equals("admin@aughergaa.com")){
            btn_create_booking.setVisibility(View.GONE);
            start_timing.setVisibility(View.GONE);
            end_timing.setVisibility(View.GONE);
            booking_heading.setVisibility(View.GONE);
        }


        start_timing.setOnClickListener(new View.OnClickListener() {
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
        });

        btn_create_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (booking_heading.getSelectedItem().toString().equals("Select Facility")){
                    Toast.makeText(NewBooking.this, "Select Facility", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (end_timing.getText().toString().isEmpty()){
                    Toast.makeText(NewBooking.this, "Enter End Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (start_timing.getText().toString().isEmpty()){
                    Toast.makeText(NewBooking.this, "Enter Start Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date now = new Date();
                Date checkin = startTime.getTime();
                Date checkout = endTime.getTime();
                if (checkin.before(now)) {
                    Toast.makeText(NewBooking.this, "You chose and elapsed Start Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkout.before(now) || checkout.before(checkin)){
                    Toast.makeText(NewBooking.this, "You chose an elapsed End time", Toast.LENGTH_SHORT).show();
                    return;
                }

                /////////////////////////
                search_for_availablity();

            }
        });

    }

    void search_for_availablity(){

        final ProgressDialog dialog = new ProgressDialog(NewBooking.this,R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Checking for Booking.");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Booking").child(booking_heading.getSelectedItem().toString());
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
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Booking").child(booking_heading.getSelectedItem().toString()).child(uid).child("Booking").push();
                    reference.child("start").setValue(startTime.getTimeInMillis());
                    reference.child("end").setValue(endTime.getTimeInMillis());
                    SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
                    FirebaseDatabase.getInstance().getReference().child("Booking").child(booking_heading.getSelectedItem().toString()).child(uid).child("Name").setValue(preferences.getString("Name",""));

                    Toast.makeText(NewBooking.this, "Booking Successful.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NewBooking.this, "Booking cannot succeed. Time Conflict Occur", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(NewBooking.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(NewBooking.this,
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(NewBooking.this,
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