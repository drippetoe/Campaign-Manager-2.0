/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.report.RegistrationSummary;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface RegistrationReportManagerLocal extends AbstractManagerInterface<RegistrationSummary> {

    public RegistrationSummary fetchRegistrationSummaryByCompanyAndRegistrationSummary(Company company, RegistrationSummary summary);

    public RegistrationSummary fetchRegistrationSummary(Date start, Date end, Company company);

    public List<RegistrationSummary> fetchRegistrationSummaries(Date start, Date end, Company company);

    public List<RegistrationSummary> getRegistrationsFromSubscriber(Company company);

    public List<RegistrationSummary> getRegistrationsFromSubscriberByKeyword(Company company, Keyword keyword);

    public List<RegistrationSummary> fetchRegistrationSummariesByDMA(Date startDate, Date endDate, Company company);

    public RegistrationSummary fetchRegistrationSummaryForDma(Company company, RegistrationSummary summary);
}
