/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.ZipcodeToTimezone;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ZipcodeToTimezoneManagerLocal extends AbstractManagerInterface<ZipcodeToTimezone> {
    
    public ZipcodeToTimezone getByZipcode(String zipcode);
        
    
}
