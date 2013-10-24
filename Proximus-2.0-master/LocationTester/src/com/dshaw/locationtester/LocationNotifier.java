package com.dshaw.locationtester;

import android.location.Location;

public interface LocationNotifier {
	public void newLocationFound(Location location);
}
