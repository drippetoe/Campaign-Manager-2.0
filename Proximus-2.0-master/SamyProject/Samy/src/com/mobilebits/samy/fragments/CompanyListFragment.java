package com.mobilebits.samy.fragments;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockListFragment;
import com.mobilebits.samy.R;
import com.mobilebits.samy.adapters.CompanyAdapter;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CompanyListFragment extends SherlockListFragment {
    
    
    CompanyAdapter adapter = null;
    String[] values = new String[] { "L'Oreal", "The Home Depot", "Boeing", "Dell", "Exxon Mobile", "Sara Lee", "Gap" };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      setRetainInstance(true);
      if (adapter==null) {
        ArrayList<String> items=new ArrayList<String>();
        for (int i=0;i<values.length;i++) {
            items.add(values[i]); 
         }
        
        adapter=new CompanyAdapter(getActivity(), items);
      }
      else {
        adapter.startProgressAnimation();
      }
      
      setListAdapter(adapter);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.uglylist, container, false); 
        return view;
    }
    
}
