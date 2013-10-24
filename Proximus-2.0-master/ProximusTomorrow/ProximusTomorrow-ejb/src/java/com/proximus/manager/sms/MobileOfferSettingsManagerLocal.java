/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface MobileOfferSettingsManagerLocal extends AbstractManagerInterface<MobileOfferSettings> {
    
    public MobileOfferSettings findByBrand(Brand brand);
    
}
