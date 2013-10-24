/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.UserProfileSummary;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface UserProfileSummaryManagerLocal extends AbstractManagerInterface<UserProfileSummary> {

    public UserProfileSummary fetchUserProfileSummary(String macAddress, Company company, Device device);

    public List<UserProfileSummary> fetchUserProfileSummaries(Date start, Date end, Company company, Device device);
}