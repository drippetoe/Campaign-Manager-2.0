/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.ShortCode;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface ShortCodeManagerLocal extends AbstractManagerInterface<ShortCode> {
    
    public ShortCode findByCompany(Company company);
    
    public ShortCode findByShortCodeNumber(String codeNumber);
}
