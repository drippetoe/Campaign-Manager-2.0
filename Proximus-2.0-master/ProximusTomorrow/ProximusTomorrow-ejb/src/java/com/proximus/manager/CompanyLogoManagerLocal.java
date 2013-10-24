/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.CompanyLogo;
import javax.ejb.Local;


/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface CompanyLogoManagerLocal extends AbstractManagerInterface<CompanyLogo> {
    
    public CompanyLogo getByCompany(Company c);

}
