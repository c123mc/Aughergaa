package com.conor.aughergaa.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.conor.aughergaa.DeleteBooking;
import com.conor.aughergaa.EditBooking;
import com.conor.aughergaa.NewBooking;
import com.conor.aughergaa.R;

public class MyBooking extends Fragment {

    Button new_,edit_,delete_,my_;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_booking, container, false);

        new_ = (Button) view.findViewById(R.id.new__);
        edit_ = (Button) view.findViewById(R.id.edit__);
        delete_ = (Button) view.findViewById(R.id.delete__);
        my_ = (Button) view.findViewById(R.id.my_booking);


        SharedPreferences preferences = getActivity().getSharedPreferences("user",getActivity().MODE_PRIVATE);
        if (preferences.getString("Email","").equals("admin@aughergaa.com")){
            new_.setVisibility(View.GONE);
            my_.setVisibility(View.GONE);
        }

        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewBooking.class));
            }
        });

        my_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.conor.aughergaa.MyBooking.class));
            }
        });
        delete_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DeleteBooking.class));
            }
        });
        edit_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditBooking.class));
            }
        });
        return view;
    }

    @Override
    public void onStop() {
                super.onStop();
    }
}
