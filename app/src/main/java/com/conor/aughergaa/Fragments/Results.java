package com.conor.aughergaa.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.conor.aughergaa.Adapters.ResultsAdapter;
import com.conor.aughergaa.Classes.Utils;
import com.conor.aughergaa.Object.MatchResultObjects;
import com.conor.aughergaa.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Results extends Fragment {


    public Results() {
        // Required empty public constructor
    }

    ListView list_;
    ArrayList<String> ref = new ArrayList<>();
    ArrayList<MatchResultObjects> res=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        list_ = (ListView) view.findViewById(R.id.list_);
        list_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences preferences = getActivity().getSharedPreferences("user",MODE_PRIVATE);
                if (preferences.getString("Email","").equals("admin@aughergaa.com"))
                    update_result(position);
            }
        });



        FirebaseDatabase.getInstance().getReference().child("Result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref = new ArrayList<>();
                res = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    ref.add(snap.getKey());
                    res.add(new MatchResultObjects(snap.child("team1first").getValue().toString(),
                            snap.child("team1second").getValue().toString(),
                            snap.child("team2first").getValue().toString(),
                            snap.child("team2second").getValue().toString()));
                }

                list_.setAdapter(new ResultsAdapter(ref,res,getActivity()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    void update_result(final int i){
        final AlertDialog dialog[] = new AlertDialog[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.add_result, null);
        TextView team1name = (TextView) view.findViewById(R.id.team_1_name);
        team1name.setText(Utils.results.get(i).getName_team_1());
        TextView team_2_name = (TextView) view.findViewById(R.id.team_2_name);
        team_2_name.setText(Utils.results.get(i).getName_team_2());
        Button add_match = (Button) view.findViewById(R.id.add_match);
        TextView cancel= (TextView) view.findViewById(R.id.cancel);

        final EditText team_1_first_value = (EditText) view.findViewById(R.id.team_1_first_value);
        final EditText team_1_second_value = (EditText) view.findViewById(R.id.team_1_second_value);
        final EditText team_2_first_value = (EditText) view.findViewById(R.id.team_2_first_value);
        final EditText team_2_second_value = (EditText) view.findViewById(R.id.team_2_second_value);
        add_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team_1_first_value.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please fill the result", Toast.LENGTH_SHORT).show();
                }else if (team_1_second_value.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please fill the result", Toast.LENGTH_SHORT).show();
                }else if (team_2_first_value.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please fill the result", Toast.LENGTH_SHORT).show();
                }else if (team_2_second_value.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please fill the result", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Result").child(Utils.results.get(i).getRef()).setValue(new MatchResultObjects(team_1_first_value.getText().toString()
                    ,team_1_second_value.getText().toString(),team_2_first_value.getText().toString(),team_2_second_value.getText().toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog[0].dismiss();
                        }
                    });

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
            }
        });
        builder.setView(view);
        dialog[0] = builder.create();
        dialog[0].show();


    }

}
