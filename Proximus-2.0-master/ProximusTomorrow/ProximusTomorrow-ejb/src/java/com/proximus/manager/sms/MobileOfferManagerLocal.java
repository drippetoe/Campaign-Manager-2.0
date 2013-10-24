/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface MobileOfferManagerLocal extends AbstractManagerInterface<MobileOffer> {

    public MobileOffer getMobileOfferbyId(Long id);

    public List<MobileOffer> findAllByCompany(Company company);

    public List<MobileOffer> findByCompanyNotDeleted(Company company);

    public List<MobileOffer> findByCompanyExpired(Company company);
    
    public List<MobileOffer> findByCompanyPendingApproval(Company company);
    
    public List<MobileOffer> findByCompanyNotExpired(Company company);

    public List<MobileOffer> findActiveAndApprovedByBrand(Brand brand);

    public List<MobileOffer> findActiveAndApprovedByCompany(Company company);

    public List<MobileOffer> findActiveByCompany(Company company);

    public List<MobileOffer> findActiveByRetailerOnly(Company company, Retailer retailer);

    public List<MobileOffer> findActiveByPropertyOnly(Property property);

    public List<MobileOffer> findActiveOffersForProperty(Property property);

    public List<MobileOffer> findActiveByCompanies(List<Company> companies);

    public List<MobileOffer> findExpiredByCompanies(List<Company> companies);
    
    public List<MobileOffer> findPendingApprovalByCompanies(List<Company> companies);
    
    public List<MobileOffer> findNotExpiredByCompanies(List<Company> companies);

    public List<MobileOffer> findNotDeletedByCompanies(List<Company> companies);

    public List<MobileOffer> findRetailWideActiveByBrand(Brand b);

    public List<MobileOffer> findRetailWideExpiredByBrand(Brand b);
    
    public List<MobileOffer> findRetailWidePendingApprovalByBrand(Brand b);
    
    public List<MobileOffer> findRetailWideNotExpiredByBrand(Brand b);
    
    public List<MobileOffer> findRetailWideNotDeletedByBrand(Brand b);

    public List<MobileOffer> findRetailWideActiveAndApproved(Brand b);

    public List<MobileOffer> findActiveByProperties(List<Property> properties);

    public List<MobileOffer> findExpiredByProperties(List<Property> properties);
    
    public List<MobileOffer> findPendingApprovalByProperties(List<Property> properties);
    
    public List<MobileOffer> findNotExpiredByProperties(List<Property> properties);

    public List<MobileOffer> findNotDeletedByProperties(List<Property> properties);

    public List<MobileOffer> findRetailWideActiveInProperties(List<Property> properties);

    public List<MobileOffer> findRetailWideExpiredInProperties(List<Property> properties);
    
    public List<MobileOffer> findRetailWidePendingApprovalInProperties(List<Property> properties);

    public List<MobileOffer> findRetailWideNotDeletedInProperties(List<Property> properties);
    
    public List<MobileOffer> findRetailWideNotExpiredInProperties(List<Property> properties);

    public java.util.List<com.proximus.data.sms.MobileOffer> findByCompanyExpiringAtDate(com.proximus.data.Company company, java.util.Date date);
}
