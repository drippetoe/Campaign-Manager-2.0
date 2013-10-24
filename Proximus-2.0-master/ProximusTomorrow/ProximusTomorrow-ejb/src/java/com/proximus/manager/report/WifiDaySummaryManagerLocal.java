/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface WifiDaySummaryManagerLocal extends AbstractManagerInterface<WifiDaySummary> {
    
    public List<ViewWifiDaySummary> getViewSummary(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<WifiDaySummary> getInDateRange(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getSuccessfulPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getUniqueUsers(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    
    public List<WifiDaySummary> getAllByCompany(Company company);
    

}
