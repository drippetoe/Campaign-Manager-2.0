/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothSend;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewBluetoothDaySummary;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface BluetoothSendManagerLocal extends AbstractManagerInterface<BluetoothSend>{
    
    public Long getTotalDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getUniqueDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getUniqueDevicesSupportingBluetooth(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getUniqueDevicesAcceptingPush(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getUniqueDevicesDownloadingContent(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public Long getTotalContentDownloads(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<ViewBluetoothDaySummary> getBluetoothDaySummaryForDevicesSeen(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<ViewBluetoothDaySummary> getBluetoothDaySummaryForContentDownload(Company company, Campaign campaign, Device device, Date startDate, Date endDate);
    
    public List<BluetoothSend> getBluetoothSendInRangeByCampaignAndDevice(Company company, Campaign Campaign, Device device, Date startDate, Date endDate);


}
