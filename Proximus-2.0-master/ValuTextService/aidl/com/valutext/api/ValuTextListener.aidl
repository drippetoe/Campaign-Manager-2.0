package com.valutext.api;

import com.valutext.data.Property;


interface ValuTextListener {

	void handleLocationUpdate(in List<Property> response);
	
}