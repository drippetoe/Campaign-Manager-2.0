/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.*;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface CompanyManagerLocal extends AbstractManagerInterface<Company> {

    public Company getCompanybyName(String name);

    public List<Company> findCompanyLike(String keyword);
    
    public List<Company> findCompaniesWithLicense(String license);
    
    public List<Company> findCompaniesWithLicenseInBrand(String license, Brand b);

    public List<Company> findAllCompanies();
    
    public List<Company> findAllCompaniesOfBrand(Brand b);

    public Company getCompanybyId(Long id);

    public List<User> getUsersFromCompany(Long id);
    
    public List<License> getAllLicenses();
    
    public License getLicenseByText(String licenseText);
    
    public PricingModel getPricingModelByCompany(Company c);
    
    
}
