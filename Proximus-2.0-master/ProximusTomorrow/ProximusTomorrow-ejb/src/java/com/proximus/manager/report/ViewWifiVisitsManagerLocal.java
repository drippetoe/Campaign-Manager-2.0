/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewWifiVisits;
import com.proximus.data.report.WifiVisits;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gilberto Gaxiola
 */
public interface ViewWifiVisitsManagerLocal extends AbstractManagerInterface<ViewWifiVisits>  {
    
    
    public List<ViewWifiVisits> getRawWifiVisits(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<WifiVisits> getWifiVisits(Company company, Campaign campaign, Device device, Date startDate, Date endDate);


}
