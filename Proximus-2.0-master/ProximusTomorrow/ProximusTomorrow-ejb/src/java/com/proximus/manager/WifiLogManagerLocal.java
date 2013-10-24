/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.WifiLog;
import com.proximus.data.report.WifiDaySummary;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface WifiLogManagerLocal extends AbstractManagerInterface<WifiLog> {

    public void createWifiLog(WifiLog log);

    public void createListWifiLogs(List<WifiLog> logs);

    public void deleteWifiLog(WifiLog log);

    public void deleteListWifiLogs(List<WifiLog> logs);

    public List<WifiLog> getWifiLogInRangeByCampaignAndDevice(Date start, Date end, Company company, Campaign campaign, Device device);

    public Long getUniqueUserCount(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getTotalRequests(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getTotalPageViews(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getSuccessfulPageViews(Company company, Date eventDate, Campaign campaign, Device device);

    public void createWifiDaySummary(WifiDaySummary entry);

    public void updateWifiDaySummary(WifiDaySummary entry);

    public WifiDaySummary fetchWifiDaySummary(Date eventDate, Company company, Campaign campaign, Device device);

    public List<WifiDaySummary> fetchWifiDaySummaries(Date start, Date end, Company company, Campaign campaign, Device device);

    public void createOrUpdateWifiDaySummary(WifiDaySummary summary);

    public List<WifiLog> getMostPopularServersFromWifiLog(Date start, Date end, Company company, Campaign campaign, Device device);
}
