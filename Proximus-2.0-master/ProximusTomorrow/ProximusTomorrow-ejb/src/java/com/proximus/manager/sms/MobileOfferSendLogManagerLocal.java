/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.*;
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
public interface MobileOfferSendLogManagerLocal extends AbstractManagerInterface<MobileOfferSendLog> {

    public List<MobileOfferSendLog> findByMonthAndCompanyAndSubscriber(Company company, Date date, Subscriber subscriber);

    public MobileOfferSendLog findMostRecentByCompanyAndSubscriber(Company company, Subscriber subscriber);

    public List<MobileOfferSendLog> findByMonthAndSubscriber(Date date, Subscriber subscriber);

    public MobileOfferSendLog findMostRecentBySubscriber(Subscriber subscriber);

    public Long getTotalMessagesSentByCompanyAndSubscriber(Company company, Date date, Subscriber subscriber);

    public Long getTotalMessagesSentBySubscriber(Date date, Subscriber subscriber);

    public Long getTotalMessagesSentByCompanyAndDateRange(Company company, Date start, Date end);

    public Long getTotalMessagesSentByDateRangeAndStatusAndCompany(Date startDate, Date endDate, Company company, String status);

    public Long getTotalMessagesSentByDateRangeAndStatusAndBrand(Date startDate, Date endDate, Brand brand, String status);

    public List<MobileOfferSendLog> getMobileOffersInRange(Company company, Subscriber subscriber, Date startDate, Date endDate);

    public List<MobileOfferSendLog> fetchMobileOfferSendLog(Date start, Date end, Company company, DMA dma, Property property, Retailer retailer);

    public Long findSendLogsByCompanyAndDate(Company company, Date eventDate, Date endOfDay);

    public Map<String, Number> getSendLogMap(Company company, Date startDate, Date endDate);

    public Long findSendLogsByCompany(Company company);

    public Long findTotalSendLogsByCompanyAndDate(Company company, Date eventDate);

    public List<Date> findSendLogDatesByCompanyAndDate(Company company, Date eventDate, Date endOfDay);

    public Long getDistinctSubscriberCount(Company company, Date startDate, Date endDate);

    public Long countMessagesSentByOffer(MobileOffer offer, Date start, Date end, Property property);

    public MobileOfferSendLog findMostRecentDeliveredByCompany(Company company);
}
