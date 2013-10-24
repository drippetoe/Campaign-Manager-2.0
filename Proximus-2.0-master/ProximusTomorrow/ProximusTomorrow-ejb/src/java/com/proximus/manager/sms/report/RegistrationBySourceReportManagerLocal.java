/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.report.RegistrationSourceSummary;
import com.proximus.data.sms.report.RegistrationSummary;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author angela mercer
 */
@Local
public interface RegistrationBySourceReportManagerLocal extends AbstractManagerInterface<RegistrationSourceSummary> {

    public RegistrationSourceSummary fetchRegistrationSourceSummary(Company company, RegistrationSourceSummary summary);

    public List<RegistrationSourceSummary> fetchRegistrationSourceSummaries(Date start, Date end, Company company);

//    public List<RegistrationSourceSummary> getRegistrationSourceSummariesFromSubscriber(Company company);
    
    public List<RegistrationSourceSummary> getRegistrationSourceSummariesFromSubscriberByCompanyAndKeyword(Company company, Keyword keyword);
    
    
    
    
}
