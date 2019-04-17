package com.conor.aughergaa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conor.aughergaa.Classes.Utils;
import com.conor.aughergaa.Object.MatchResultObjects;
import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ResultsAdapter extends BaseAdapter {

    ArrayList<String> ref;
    ArrayList<MatchResultObjects> res;
    Activity activity;

    public ResultsAdapter(ArrayList<String> ref, ArrayList<MatchResultObjects> res, Activity activity) {
        this.ref = ref;
        this.res = res;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Utils.results.size();
    }

    @Override
    public Object getItem(int position) {
        return Utils.results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.result_list_item, null);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView team_1_res = (TextView) view.findViewById(R.id.team_1_res);
        TextView team_2_res = (TextView) view.findViewById(R.id.team_2_res);
        TextView team_2_name = (TextView) view.findViewById(R.id.team_2_name);
        TextView team_1_name = (TextView) view.findViewById(R.id.team_1_name);

        team_1_name.setText(Utils.results.get(position).getName_team_1());
        team_2_name.setText(Utils.results.get(position).getName_team_2());

        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(Utils.results.get(position).getTimes_stamp());

        date.setText(calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR));

        for (int i=0;i<ref.size();i++){
            if (Utils.results.get(position).getRef().equals(ref.get(i))){
                team_1_res.setText("("+res.get(i).getTeam1first()+"-"+res.get(i).getTeam1second()+")");
                team_2_res.setText("("+res.get(i).getTeam2first()+"-"+res.get(i).getTeam2second()+")");
            }
        }

        return view;
    }
}
