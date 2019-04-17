package com.conor.aughergaa.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Object.MyBook;
import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MyBooking extends BaseAdapter {

    ArrayList<MyBook> list;
    Activity activity;
    int current=-1;
    // Current 1 for Editing
    // Current 2 for deleting
    // Current 3 for Only Show bookings

    public MyBooking(ArrayList<MyBook> list, Activity activity, int current) {
        this.list = list;
        this.activity = activity;
        this.current = current;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = li.inflate(R.layout.my_booking_row, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView checkin = (TextView) view.findViewById(R.id.checkin);
        TextView checkout = (TextView) view.findViewById(R.id.checkout);
        TextView username = (TextView) view.findViewById(R.id.username);

        name.setText(list.get(position).getRoom());
        username.setText(list.get(position).getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(list.get(position).getStart());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(list.get(position).getEnd());

        final TextView edit_booking = (TextView) view.findViewById(R.id.edit_booking);
        TextView delete_booking = (TextView) view.findViewById(R.id.delete_booking);

        if (current == 3){
            delete_booking.setVisibility(View.GONE);
            edit_booking.setVisibility(View.GONE);
        }else if (current ==1 ){
            delete_booking.setVisibility(View.GONE);
        }else if (current == 2){
            edit_booking.setVisibility(View.GONE);
        }

        edit_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_booking(position);
            }
        });

        delete_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(view.getContext())
                .setMessage("Are you sure you want to delete the booking")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Booking").child(list.get(position).getRoom()).child(list.get(position).getUid()).child("Booking").child(list.get(position).getPath()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                list.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                }).show();

            }
        });


        checkin.setText("Checkin: "+calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":00");
        checkout.setText("Checkout: "+end.get(Calendar.DAY_OF_MONTH)+"-"+(end.get(Calendar.MONTH)+1)+"-"+end.get(Calendar.YEAR)+" "+end.get(Calendar.HOUR)+":"+end.get(Calendar.MINUTE)+":00");

        return view;
    }

    Calendar startTime;
    Calendar endTime;
    Button btn_create_booking;
    TextView start_timing,end_timing;
    AlertDialog alertDialog;
    private void edit_booking(final int position){




        AlertDialog alertDialog;
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.edit_booking_dialog, null);

        end_timing = (TextView) view.findViewById(R.id.end_timing);
        start_timing = (TextView) view.findViewById(R.id.start_timing);
        start_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);
        end_timing.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.booking_, 0, 0, 0);

        btn_create_booking = (AppCompatButton) view.findViewById(R.id.btn_create_booking);
        btn_create_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (end_timing.getText().toString().isEmpty()){
                    Toast.makeText(activity, "Enter Checkout Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (start_timing.getText().toString().isEmpty()){
                    Toast.makeText(activity, "Enter Checkin Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date now = new Date();
                Date checkin = startTime.getTime();
                Date checkout = endTime.getTime();
                if (checkin.before(now)) {
                    Toast.makeText(activity, "The start time has already elapsed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkout.before(now) || checkout.before(checkin)){
                    Toast.makeText(activity, "The end time is before the start time", Toast.LENGTH_SHORT).show();
                    return;
                }

                /////////////////////////
                search_for_availablity(position);
            }
        });

        start_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();


                //////////////////////////////   Date Picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {


                                ////////////////////////////////    Time Picker
                                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
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
        });

        end_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                //////////////////////////////   Date Picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {


                                ////////////////////////////////    Time Picker
                                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
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
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

    }


    private void search_for_availablity(final int position){

        final ProgressDialog dialog = new ProgressDialog(activity,R.style.AppTheme_Dark_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Checking for Booking.");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Booking").child(list.get(position).getRoom());
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
                            if (!sn_ap.getKey().equals(list.get(position).getPath())) {
                                Calendar start_time = Calendar.getInstance();
                                start_time.setTimeInMillis(Long.parseLong(Objects.requireNonNull(sn_ap.child("start").getValue()).toString()));
                                Calendar end_time = Calendar.getInstance();
                                end_time.setTimeInMillis(Long.parseLong(Objects.requireNonNull(sn_ap.child("end").getValue()).toString()));

                                Date start_upper_date = start_time.getTime();
                                Date end_upper_date = end_time.getTime();

                                if (start_date.before(end_upper_date) || end_date.before(start_upper_date)) {
                                    isFound = true;
                                }
                            }
                        }
                    }
                }

                if (!isFound){
                    FirebaseDatabase.getInstance().getReference().child("Booking").child(list.get(position).getRoom()).child(list.get(position).getUid()).child("Booking").child(list.get(position).getPath()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            String uid = list.get(position).getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Booking").child(list.get(position).getRoom()).child(uid).child("Booking").push();
                            reference.child("start").setValue(startTime.getTimeInMillis());
                            reference.child("end").setValue(endTime.getTimeInMillis());
                            SharedPreferences preferences = activity.getSharedPreferences("user",activity.MODE_PRIVATE);
                            //FirebaseDatabase.getInstance().getReference().child("Booking").child(list.get(position).getRoom()).child(uid).child("Name").setValue(preferences.getString("Name",""));

                            Toast.makeText(activity, "Update Successful.", Toast.LENGTH_SHORT).show();
                            list.add(new MyBook(list.get(position).getRoom(),list.get(position).getPath(),startTime.getTimeInMillis(),endTime.getTimeInMillis(),uid,list.get(position).getName()));
                            list.remove(position);
                            notifyDataSetChanged();

                        }
                    });

                }else {
                    Toast.makeText(activity, "Update cannot succeed. Time Conflict Occur", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(activity, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
