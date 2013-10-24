/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.BluetoothDwell;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.UserProfileDwell;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface BluetoothDwellManagerLocal extends AbstractManagerInterface<BluetoothDwell>{
    
    public List<UserProfileDwell> getTotalDevicesSeen(Company company, Device device, Date startDate, Date endDate);
    
    


}
