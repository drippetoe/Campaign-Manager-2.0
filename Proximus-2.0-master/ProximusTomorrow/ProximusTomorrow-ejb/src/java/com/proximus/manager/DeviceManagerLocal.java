/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.DeviceInLimbo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface DeviceManagerLocal extends AbstractManagerInterface<Device> {

    public List<Device> findDevicesByCompanyAndModifiedDateLike(Company company, String keyword);

    public Device getDeviceByMacAddress(String macAddress);

    public Device getDeviceByToken(String token);

    public Device getDeviceByTokenAndMac(String token, String mac);

    public List<Device> getDeviceByMacOrSerial(String input);

    public List<Device> getDeviceByCompany(Company co);
    
    public List<Device> getActiveDeviceByCompany(Company co);

    public Device getDevicebyName(String name);

    //Devices in Limbo
    public DeviceInLimbo getDeviceInLimboByMacAdress(String macAddress);

    public DeviceInLimbo getDeviceInLimboByToken(String token);

    public DeviceInLimbo getDeviceInLimboByMacOrSerial(String input);

    public void createDeviceInLimbo(DeviceInLimbo dl);

    public void updateDeviceInLimbo(DeviceInLimbo dl);

    public void deleteDeviceInLimbo(DeviceInLimbo dl);

    public DeviceInLimbo findDeviceInLimbo(Object id);
    
    public List<DeviceInLimbo> findAllInLimbo();

    public List<Device> findAllActive(Company co);

    public List<Device> findAllInactive(Company co);
}
