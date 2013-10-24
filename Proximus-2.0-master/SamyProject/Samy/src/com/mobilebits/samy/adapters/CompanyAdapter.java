package com.mobilebits.samy.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.mobilebits.samy.R;


public class CompanyAdapter extends EndlessAdapter {
    
    String[] values = new String[] {"Nike","Coca-Cola","H&M","BMW","Apple","Google","Versace","Dolce & Gabanna","Facebook","Adidas","AT&T","McDonalds","Hot Dog Factory", "Nabisco", "Kit Kat", "Snickers", "Goldman Sachs", "Marriott", "Ajax", "Oreo", "CradlePoint", "Netflix", "DirectTv", "Comcast", "HBO", "TBS", "CNN", "Big Red", "Pepsi", "New York Times", "Zara", "Bing", "Hersheys", "Harley Davidson", "Campbell's", "Kellogg's", "Disney", "ESPN", "BAYER", "Hallmark", "Fedex", "Wal-mart", "Honda", "MasterCard", "Visa", "Discovery", "American Express", "Ericsson", "Nokia", "HTC", "Motorola", "BlackBerry", "Groupon", "Mazda", "Gilette", "Six Flags", "Publix", "Kroger", "5th 3rd Bank", "Bank of America", "Colgate", "Palmolive", "Barnes & Noble", "Microsoft", "Toyota", "Kodak", "CBS", "Hilton"};
    private RotateAnimation rotate=null;
    private View pendingView=null;
    private int currentIndex = 0;
    
    public CompanyAdapter(Context context, ArrayList<String> list) {
       super(new ArrayAdapter<String>(context,R.layout.row,android.R.id.text1,list));
       rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
       rotate.setDuration(600);
       rotate.setRepeatMode(Animation.RESTART);
       rotate.setRepeatCount(Animation.INFINITE);
       
       
    }
    
    @Override
    protected View getPendingView(ViewGroup parent) {
      View row=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
      
      pendingView=row.findViewById(android.R.id.text1);
      pendingView.setVisibility(View.GONE);
      pendingView=row.findViewById(R.id.throbber);
      pendingView.setVisibility(View.VISIBLE);
      startProgressAnimation();
      
      return(row);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        SystemClock.sleep(2500);       // pretend to do work
        
        return(getWrappedAdapter().getCount()<values.length+5);
    }

    @Override
    protected void appendCachedData() {
        if (getWrappedAdapter().getCount()<values.length+5) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> companies =(ArrayAdapter<String>)getWrappedAdapter();
            for(int i = currentIndex; i < currentIndex+5; i++) {
                companies.add(values[i]);
            }
            currentIndex +=5;
            
          }
    }
    
    public void startProgressAnimation() {
        if (pendingView!=null) {
          pendingView.startAnimation(rotate);
        }
      }

}
