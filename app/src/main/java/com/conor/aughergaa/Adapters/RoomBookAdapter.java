package com.conor.aughergaa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conor.aughergaa.Object.RoomBook;
import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Calendar;

public class RoomBookAdapter extends BaseAdapter {
    ArrayList<RoomBook> roomBooks  = new ArrayList<>();
    Activity activity;

    public RoomBookAdapter(ArrayList<RoomBook> roomBooks, Activity activity) {
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
        View view = li.inflate(R.layout.others_booked_item, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView checkin = (TextView) view.findViewById(R.id.checkin);
        TextView checkout = (TextView) view.findViewById(R.id.checkout);

        name.setText(roomBooks.get(position).getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(roomBooks.get(position).getStart());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(roomBooks.get(position).getEnd());

        checkin.setText("Checkin: "+calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":00");
        checkout.setText("Checkout: "+end.get(Calendar.DAY_OF_MONTH)+"-"+(end.get(Calendar.MONTH)+1)+"-"+end.get(Calendar.YEAR)+" "+end.get(Calendar.HOUR)+":"+end.get(Calendar.MINUTE)+":00");

        return view;
    }
}
