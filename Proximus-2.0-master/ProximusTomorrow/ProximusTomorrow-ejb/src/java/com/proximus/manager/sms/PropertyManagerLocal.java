/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface PropertyManagerLocal extends AbstractManagerInterface<Property> {

    public List<Property> getPropertiesByCompany(Company company);

    public Property getPropertyByCompanyAndName(Company c, String name);

    public List<Property> getPropertiesByCompanyDMA(Company company, DMA dma);

    public List<Long> getDMAIdsByCompany(Company company);

    public long countPropertiesByCompany(Company c);

    public List<Property> getPropertiesByCompanyAndRetailer(Company company, Retailer retailer);

    public long countNewPropertiesByCompanyBetweenDates(Date startDate, Date endDate, Company company);

    public long countPropertiesByCompanyBeforeDate(Company company, Date stopDate);

    public Property getPropertyByWebHash(String webHash);
    
    public boolean propertiesAssociatedToDma(DMA dma);
}
