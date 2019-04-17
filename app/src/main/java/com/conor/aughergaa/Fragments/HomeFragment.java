package com.conor.aughergaa.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Adapters.Home_list_Adapeter;
import com.conor.aughergaa.AddBlogActivity;
import com.conor.aughergaa.Booking;
import com.conor.aughergaa.Object.home_list_objecr;
import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView add_blog;
    ListView list;
    ArrayList<home_list_objecr> list_objects = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        add_blog = (TextView) view.findViewById(R.id.add_blog);
        list = (ListView) view.findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                move_to_detail(position);
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("user",getActivity().MODE_PRIVATE);
        if (!preferences.getString("Email","").equals("admin@aughergaa.com")){
            add_blog.setVisibility(View.GONE);
        }

        add_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddBlogActivity.class));
            }
        });

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child("Blogs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    if (snap.child("Title").exists()&&snap.child("Description").exists()&&snap.child("Image").exists())
                    list_objects.add(new home_list_objecr(snap.child("Title").getValue().toString(),snap.child("Description").getValue().toString(),snap.child("Image").getValue().toString()));
                }
                if (list_objects.size()>0)
                Collections.reverse(list_objects);
                dialog.dismiss();
                list.setAdapter(new Home_list_Adapeter(list_objects,getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    void move_to_detail(int a){
        Intent intent = new Intent(getActivity(), Booking.class);
        intent.putExtra("Title",list_objects.get(a).getTitle());
        intent.putExtra("Description",list_objects.get(a).getDescription());
        intent.putExtra("URL",list_objects.get(a).getUrl());
        startActivity(intent);
    }

    @Override
    public void onStop() {
        add_blog.setVisibility(View.GONE);
        super.onStop();
    }
}
