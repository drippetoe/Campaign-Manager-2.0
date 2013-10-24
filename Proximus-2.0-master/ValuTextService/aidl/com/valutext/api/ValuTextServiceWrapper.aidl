package com.valutext.api;

import com.valutext.data.Property;
import com.valutext.api.ValuTextListener;

interface ValuTextServiceWrapper {

	List<Property> updateLocation();
	
	void addListener(ValuTextListener listener);
	
	void removeListener(ValuTextListener listener);
	
	void stopGPS();
	
	void startGPS();


}