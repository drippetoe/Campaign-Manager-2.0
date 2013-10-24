/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.report.ViewWifiPageViews;
import com.proximus.data.report.ViewWifiSuccessfulPages;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewWifiPageManagerLocal extends AbstractManagerInterface<ViewWifiPageViews>  {
    
    
    public List<ViewWifiDaySummary> getWifiDaySummary(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<ViewWifiPageViews> getPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<ViewWifiSuccessfulPages> getSuccessfulPageViews(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<String> getUniqueUsers(Company company, Campaign c, Device d, Date startDate, Date endDate);
    
    

}
