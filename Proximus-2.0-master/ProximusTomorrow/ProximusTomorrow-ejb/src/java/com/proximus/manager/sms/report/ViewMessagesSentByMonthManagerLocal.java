/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewMessagesSentByMonth;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;

/**
 *
 * @author Gilberto Gaxiola
 */
public interface ViewMessagesSentByMonthManagerLocal extends AbstractManagerInterface<ViewMessagesSentByMonth> {

    public List<ViewMessagesSentByMonth> getSentInYearAndMonth(Company c, int year, int month);
    
    public Long getSentInYearAndMonthByProperty(Company company, int year, int month, Property property);
}
