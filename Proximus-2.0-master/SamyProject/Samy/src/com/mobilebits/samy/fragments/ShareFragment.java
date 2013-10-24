package com.mobilebits.samy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.mobilebits.samy.R;

public class ShareFragment extends SherlockFragment {
    
 private View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        if(view == null) {
            view = inflater.inflate(R.layout.offer, container, false);
        } else {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        return view;
    }

}
