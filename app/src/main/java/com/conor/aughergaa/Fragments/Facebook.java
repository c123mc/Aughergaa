package com.conor.aughergaa.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.conor.aughergaa.R;

public class Facebook extends Fragment {

    public Facebook() {
    }

    WebView _facebook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        _facebook = (WebView) view.findViewById(R.id._facebook);
        _facebook.setWebViewClient(new WebViewClient());
        _facebook.loadUrl("https://en-gb.facebook.com/Aughergaa/posts/?ref=page_internal");
        return view;
    }
}
