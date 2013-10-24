/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoLocationSettings;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface GeoLocationSettingsManagerLocal extends AbstractManagerInterface<GeoLocationSettings> {
    
    public GeoLocationSettings findByBrand(Brand brand);

}
