/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Country;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewPropertySummaryStats;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewPropertySummaryStatsManagerLocal extends AbstractManagerInterface<ViewPropertySummaryStats> {

    public List<ViewPropertySummaryStats> getByBrand(Brand brand);

    public List<ViewPropertySummaryStats> getByCompany(Company company);

    public List<ViewPropertySummaryStats> getByProperties(List<Property> properties);

    public List<ViewPropertySummaryStats> getByBrandAndCountry(Brand brand, Country country);

    public List<ViewPropertySummaryStats> getByCompanyAndCountry(Company company, Country country);
}
