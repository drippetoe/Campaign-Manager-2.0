/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Brand;
import com.proximus.data.sms.report.ViewActiveUsers;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewActiveUsersManagerLocal extends AbstractManagerInterface<ViewActiveUsers> {
    
    public List<ViewActiveUsers> getActiveUsers(Brand brand, Date date);
    
    public Long getCountActiveUsers(Brand brand, Date date);

}
