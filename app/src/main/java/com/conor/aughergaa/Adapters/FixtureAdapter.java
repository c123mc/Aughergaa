package com.conor.aughergaa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conor.aughergaa.Classes.MyMatchClass;
import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FixtureAdapter extends BaseAdapter {

    ArrayList<MyMatchClass> arrayList = new ArrayList<>();
    Activity activity;

    public FixtureAdapter(ArrayList<MyMatchClass> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.fixtures_item, null);
        ImageView image_1 = (ImageView) view.findViewById(R.id.team_1_image);
        ImageView image_2 = (ImageView) view.findViewById(R.id.team_2_image);
        TextView name_1 = (TextView) view.findViewById(R.id.team_name_1);
        TextView name_2 = (TextView) view.findViewById(R.id.team_name_2);
        TextView timing = (TextView) view.findViewById(R.id.timing);

        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(arrayList.get(position).getTimes_stamp());

        String min = calender.get(Calendar.MINUTE)+"";
        if (min.length() == 1){
            min = "0"+min;
        }

        String hor = calender.get(Calendar.HOUR_OF_DAY)+"";
        if (hor.length() == 1){
            hor = "0"+hor;
        }

        timing.setText(calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)
        +"\n"+hor+":"+min);

        //Glide.with(activity).load(arrayList.get(position).getImage_team_1()).fitCenter().into(image_1);
        //Glide.with(activity).load(arrayList.get(position).getImage_team_2()).fitCenter().into(image_2);

        name_2.setText(arrayList.get(position).getName_team_1());
        name_1.setText(arrayList.get(position).getName_team_2());

        return view;
    }
}
