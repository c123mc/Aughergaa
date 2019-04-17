package com.conor.aughergaa.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.conor.aughergaa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Twitter extends Fragment {


    public Twitter() {
        // Required empty public constructor
    }

    WebView _twitter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        _twitter = (WebView) view.findViewById(R.id._twitter);
        _twitter.setWebViewClient(new WebViewClient());
        _twitter.loadUrl("https://twitter.com/aneochair?lang=en");
        return view;
    }

}
