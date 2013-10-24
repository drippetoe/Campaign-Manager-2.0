/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms.report;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.sms.report.MobileOfferSendLogSummary;
import com.proximus.manager.AbstractManagerInterface;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface MobileOfferSendLogReportManagerLocal extends AbstractManagerInterface<MobileOfferSendLogSummary> {

    public List<DMA> findAllDmas(Company company);

    public List<Retailer> findAllRetailers(Company company);

    public MobileOfferSendLogSummary fetchMobileOfferSendLogSummaryByCompanyAndSendLogSummary(Company company, MobileOfferSendLogSummary sendLogSummary);

    public MobileOfferSendLogSummary fetchMobileOfferSendLogSummary(Company company, DMA dma, Property property, Retailer retailer, MobileOffer offer);

    public List<MobileOfferSendLogSummary> fetchMobileOfferSendLogSummaries(Date start, Date end, Company company, DMA dma, Property property, Retailer retailer);

    public List<MobileOfferSendLogSummary> getSummaryFromSendLog(Company company, Date deliveryDate);
}
