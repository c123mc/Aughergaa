package com.conor.aughergaa.Fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.conor.aughergaa.Adapters.FixtureAdapter;
import com.conor.aughergaa.Classes.Utils;
import com.conor.aughergaa.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class FixtureResults extends Fragment {


    public FixtureResults() {
        // Required empty public constructor
    }

    ListView list_;
    Button button;
    AlertDialog dialog[] = new AlertDialog[1];
    String name_1,name_2;
    Calendar selected_calender;
    EditText team_1_name;
    EditText team_2_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fixture_results, container, false);
        list_ = (ListView) view.findViewById(R.id.list_);
        button = (Button) view.findViewById(R.id.button);
        SharedPreferences preferences = getActivity().getSharedPreferences("user",MODE_PRIVATE);
        if (!preferences.getString("Email","").equals("admin@aughergaa.com")){
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_Dialog();
            }
        });
        list_.setAdapter(new FixtureAdapter(Utils.fixtures,getActivity()));

        return view;
    }


    void show_Dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_fixtures,null);

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        team_1_name = (EditText) view.findViewById(R.id.team_1_name);
        team_2_name = (EditText) view.findViewById(R.id.team_2_name);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
            }
        });

        final Button select_time = (Button) view.findViewById(R.id.select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar current_calender = Calendar.getInstance();
                new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selected_calender = Calendar.getInstance();
                                selected_calender.set(year,month,dayOfMonth,hourOfDay,minute);
                            }
                        },current_calender.get(Calendar.HOUR_OF_DAY),current_calender.get(Calendar.MINUTE),false).show();
                    }
                }, current_calender.get(Calendar.YEAR), current_calender.get(Calendar.MONTH), current_calender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Button add_match = (Button) view.findViewById(R.id.add_match);
        add_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected_calender == null){
                    Toast.makeText(getActivity(), "Please select Match Time", Toast.LENGTH_SHORT).show();
                }else if (team_1_name.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Team 1 Name", Toast.LENGTH_SHORT).show();
                }else if (team_2_name.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Team 2 Name", Toast.LENGTH_SHORT).show();
                }else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Fixture").push();
                    reference.child("name_team_1").setValue(team_1_name.getText().toString());
                    reference.child("name_team_2").setValue(team_2_name.getText().toString());
                    reference.child("result").setValue("");
                    reference.child("times_stamp").setValue(selected_calender.getTimeInMillis());
                    dialog[0].dismiss();
                }
            }
        });

        builder.setView(view);
        dialog[0] = builder.create();
        dialog[0].show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
