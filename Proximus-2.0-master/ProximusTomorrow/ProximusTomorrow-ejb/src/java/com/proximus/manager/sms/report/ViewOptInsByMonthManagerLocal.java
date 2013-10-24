/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewOptInsByMonth;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;

/**
 *
 * @author Gilberto Gaxiola
 */
public interface ViewOptInsByMonthManagerLocal extends AbstractManagerInterface<ViewOptInsByMonth> {

    public List<ViewOptInsByMonth> getOptInsInYearAndMonth(Company c, int year, int month);
    
    public Long getOptInsInYearAndMonthByProperty(Company company, int year, int month, Property property);
}
