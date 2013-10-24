/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothDwell;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface BluetoothDwellReportManagerLocal {

    public Long totalDwellSessions(Date startDate, Date endDate, Company company, Campaign campaign, Device device);

    public Long totalDwellTime(Date startDate, Date endDate, Company company, Campaign campaign, Device device);

    public Double averageDwellTime(Date startDate, Date endDate, Company company, Campaign campaign, Device device);

    public List<Device> getDevicesWithBluetoothDwell(Company company);

    public List<String> getDeviceMacAddresses(Company company);

    public String getDeviceFriendlyNames(String macAddress, Company company);

    public Long getMacAddressCount(String macAddress, Company company);

    public List<BluetoothDwell> getBluetoothDwellRecordsFrom(Date startDate, Date endDate, String macAddress, Company company, Device device);
    
    public List<BluetoothDwell> getAllBluetoothDwellRecordsFrom(String macAddress, Company company, Device device);
}
