/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.*;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface WifiRegistrationManagerLocal extends AbstractManagerInterface<WifiRegistration> {

    public void createRegistration(WifiRegistration registration);

    public void createRegistrationList(List<WifiRegistration> registrations);

    public void deleteRegistration(WifiRegistration registration);

    public void deleteRegistrationList(List<WifiRegistration> registrations);

    public Long getRegistrationCount(Date start, Date end, Company company, Campaign campaign, Device device);

    public List<WifiRegistration> getWifiRegistrationInRangeByCampaignAndDevice(Date start, Date end, Company company, Campaign campaign, Device device);
}