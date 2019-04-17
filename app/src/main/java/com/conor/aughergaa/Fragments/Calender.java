package com.conor.aughergaa.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.conor.aughergaa.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Calender extends Fragment {

    View view;
    List<Date> selectedDates;
    Date start, end;
    LinearLayout layoutCalender;
    View custom_view;
    Date initialDate, lastDate;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calender, container, false);
        WebView vistaWeb = (WebView) view.findViewById(R.id.web);
        vistaWeb.setWebChromeClient(new WebChromeClient());
        vistaWeb.setWebViewClient(new WebViewClient());
        vistaWeb.clearCache(true);
        vistaWeb.clearHistory();
        vistaWeb.getSettings().setJavaScriptEnabled(true);
        vistaWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        vistaWeb.loadUrl("https://calendar.google.com/calendar/embed?src=477hcnel09mkpi5sddklv67kls%40group.calendar.google.com&ctz=Europe%2FLondon");
        return view;
    }
}
