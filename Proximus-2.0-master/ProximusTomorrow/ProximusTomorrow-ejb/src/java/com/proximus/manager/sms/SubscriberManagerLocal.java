/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface SubscriberManagerLocal extends AbstractManagerInterface<Subscriber> {

    public List<Subscriber> findAllByCompany(Company company);

    public Long findOptedInCountByCompany(Company company);

    public Long findOptedInCountByBrand(Brand brand);

    public Long findOptedInCountByCompanyAndDate(Company company, Date eventDate, Date endOfDay);

    public Long findOptedInCountByBrandAndDate(Brand brand, Date eventDate, Date endOfDay);

    public Subscriber findByMsisdn(String msisdn);

    public List<Subscriber> findAllByBrandAndOptIn(Brand brand);

    public List<Subscriber> findAllByCompanyAndOptIn(Company company);

    public List<Subscriber> findSubscribersNeedingLocation(Brand brand);

    public List<Subscriber> findAllSubscribersByKeywordAndStatus(Company company, Keyword keyword, String status);

    public Long countAllByCompany(Company company);

    public List<Subscriber> findAllByCompanyStatusNotFailed(com.proximus.data.Company company);

    public List<Subscriber> findAllByCompanyAndStatus(com.proximus.data.Company company, java.lang.String status);

    public List<Subscriber> findAllSubscribersByKeywordAndOptOutDate(com.proximus.data.Company company, Keyword keyword, Date smsOptOutDate);

    public List<Subscriber> findAllSubscribersByKeywordAndStatusAndOptInDate(Company company, Keyword keyword, Date smsOptInDate, String status);

    public Map<String, Number> getSupportedCarrierSubscriberMap(Company company);

    public Map<String, Number> getUnSupportedCarrierSubscriberMap(Company company);

    public long countedActiveSubscribersBetweenDatesByCompany(Date startDate, Date endDate, Company c);

    public long countOptedOutByCompanyBetweenDates(Company c, Date startDate, Date endDate);

    public List<Subscriber> findAllUnsynchedSubscribers();

    public List<Subscriber> findActiveSubscribersWithLateLookups(Date minimumLookup);

    public Long findTotalOptedInCountByCompanyAndDate(Company company, Date eventDate);

    public Long getNrOfSupportedCarriers(Company company);

    public Long getNrOfUnSupportedCarriers(Company company);

    public Long findTotalOptedInCountByBrandAndDate(Brand brand, Date eventDate);

    public Double getAverageMessagesSentByBrandAndDate(Brand brand, Date startDate, Date endDate);

    public Double getAverageDaysBetweenMessagesByBrandAndDate(Brand brand, Date startDate, Date endDate);

    public long countOptInsForOneDay(Company company, Date day);

    public long countOptInsForMonth(Company company, Date month);

    public Map<String, Number> getSubscriberStatuses(Company company);

}
