package com.dshaw.locationtester;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener, LocationNotifier {

	public static final String TAG = MainActivity.class.getSimpleName();
	
	private SimpleLocationManager locationMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button enableServiceButton = (Button)findViewById(R.id.enableServiceButton);
		enableServiceButton.setOnClickListener(this);
		
		Button disableServiceButton = (Button)findViewById(R.id.disableServiceButton);
		disableServiceButton.setOnClickListener(this);
		
		locationMgr = new SimpleLocationManager(this);
		locationMgr.addListener(this);
	}
	
	public void logSomething(String text)
	{
		TextView locationValue = (TextView)this.findViewById(R.id.logData);
		locationValue.setText(locationValue.getText() + fmtDate(new Date()) + ": \t" + text + "\n");
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		locationMgr.enableLocationServices();
		logSomething("onResume");
		
		logSomething(System.getProperty("os.name"));
		logSomething(android.os.Build.MANUFACTURER);
		logSomething(android.os.Build.DEVICE);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		locationMgr.disableLocationServices();
	}

	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.enableServiceButton )
		{
			locationMgr.enableLocationServices();
		}
		else if ( v.getId() == R.id.disableServiceButton )
		{
			locationMgr.disableLocationServices();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationMgr.disableLocationServices(); // just to be sure
	}

	private String fmtLL(double ll)
	{
		return String.format("%1$,.5f", ll);
	}
	
	private String fmtDate(Date d)
	{
		SimpleDateFormat fmt = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
		return fmt.format(d);
	}
	
	@Override
	public void newLocationFound(Location lastLocation) {
		String locationText = fmtLL(lastLocation.getLatitude()) +  "," + fmtLL(lastLocation.getLongitude());
        
        TextView locationValue = (TextView)this.findViewById(R.id.locationValue);
        locationValue.setText(locationText);
        
        TextView timestampValue = (TextView)this.findViewById(R.id.timestampValue);
        timestampValue.setText(fmtDate(new Date(lastLocation.getTime())));
        
        logSomething(locationText);
	}

}
