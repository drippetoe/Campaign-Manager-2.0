/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.report.ViewTotalMessagesSent;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ViewTotalMessagesSentManagerLocal extends AbstractManagerInterface<ViewTotalMessagesSent> {
    
    public List<ViewTotalMessagesSent> getTotalMessagesSent(Company company, Date startDate, Date endDate);
    
    public Long getTotalMessagesSentBefore(Company company, Date date);

}
