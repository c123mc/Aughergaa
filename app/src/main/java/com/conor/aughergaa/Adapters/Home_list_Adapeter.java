package com.conor.aughergaa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conor.aughergaa.Object.home_list_objecr;
import com.conor.aughergaa.R;

import java.util.ArrayList;

public class Home_list_Adapeter extends BaseAdapter {
    ArrayList<home_list_objecr> roomBooks  = new ArrayList<>();
    Activity activity;

    public Home_list_Adapeter(ArrayList<home_list_objecr> roomBooks, Activity activity) {
        this.roomBooks = roomBooks;
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return roomBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return roomBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.home_list_row, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView desc = (TextView) view.findViewById(R.id.desc);
        ImageView image= (ImageView) view.findViewById(R.id.image);

        title.setText(roomBooks.get(position).getTitle());
        desc.setText(roomBooks.get(position).getDescription());
        Glide.with(activity).load(roomBooks.get(position).getUrl()).into(image);


        return view;
    }
}
