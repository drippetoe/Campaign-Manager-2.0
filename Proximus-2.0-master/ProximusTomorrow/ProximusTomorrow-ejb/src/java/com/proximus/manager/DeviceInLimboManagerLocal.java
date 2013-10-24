/*
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.DeviceInLimbo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author eric
 */
@Local
public interface DeviceInLimboManagerLocal extends AbstractManagerInterface<DeviceInLimbo> {

    //Devices in Limbo
    public DeviceInLimbo getDeviceInLimboByMacAdress(String macAddress);

    public DeviceInLimbo getDeviceInLimboByToken(String token);

    public DeviceInLimbo getDeviceInLimboByMacOrSerial(String input);

    public List<DeviceInLimbo> findAllInLimbo();

    public void truncateDevicesInLimbo();
}
