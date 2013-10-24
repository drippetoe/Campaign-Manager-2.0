/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.bean.sms.DmaRegistrationSummaryReport;
import com.proximus.bean.sms.DmaRegistrationSummaryRow;
import com.proximus.bean.sms.RegistrationsBySourceSummaryReport;
import com.proximus.bean.sms.RegistrationsBySourceSummaryRow;
import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.sms.report.RegistrationSourceSummary;
import com.proximus.data.sms.report.RegistrationSummary;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.DMAManagerLocal;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.RetailerManagerLocal;
import com.proximus.manager.sms.report.RegistrationBySourceReportManagerLocal;
import com.proximus.manager.sms.report.RegistrationReportManagerLocal;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "registrationReportController")
@SessionScoped
public class RegistrationReportController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(RegistrationReportController.class.getName());
    private static final long serialVersionUID = 1;
    private long maxChartHeight = 0L;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    DMAManagerLocal dmaMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    RetailerManagerLocal retailerMgr;
    @EJB
    RegistrationReportManagerLocal registrationMgr;
    @EJB
    RegistrationBySourceReportManagerLocal registrationSourceMgr;
    @EJB
    KeywordManagerLocal keywordMgr;
    private RegistrationSummary registrationSummary;
    private List<RegistrationSummary> registrationSummaries;
    private CartesianChartModel registrationChartModel;
    private CartesianChartModel topFiveDmaChartModel;
    private CartesianChartModel topFiveSourceChartModel;
    private DmaRegistrationSummaryReport dmaRegistrationSummaryReport;
    private RegistrationsBySourceSummaryReport registrationBySourceSummaryReport;

    public RegistrationReportController() {
    }

    public RegistrationsBySourceSummaryReport getRegistrationBySourceSummaryReport() {
        if (registrationBySourceSummaryReport == null) {
            createRegistrationsBySourceSummaryReport();
        }
        return registrationBySourceSummaryReport;
    }

    public void setRegistrationBySourceSummaryReport(RegistrationsBySourceSummaryReport registrationBySourceSummaryReport) {
        this.registrationBySourceSummaryReport = registrationBySourceSummaryReport;
    }

    public RegistrationSummary getRegistrationSummary() {
        updateRegistrationSummary();
        return registrationSummary;
    }

    public void setRegistrationSummary(RegistrationSummary registrationSummary) {
        this.registrationSummary = registrationSummary;
    }

    public List<RegistrationSummary> getRegistrationSummaries() {
        return registrationSummaries;
    }

    public void setRegistrationSummaries(List<RegistrationSummary> registrationSummaries) {
        this.registrationSummaries = registrationSummaries;
    }

    @Override
    public Company getCompany() {
        if (company == null) {
            company = companyMgr.find(getCompanyIdFromSession());
        }
        return company;
    }

    @Override
    public void setCompany(Company c) {
        this.company = c;
    }

    @Override
    public Property getSelectedProperty() {
        if (selectedProperty == null) {
            selectedProperty = new Property();
        }
        return selectedProperty;
    }

    @Override
    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    @Override
    public DMA getSelectedDMA() {
        if (selectedDMA == null) {
            selectedDMA = new DMA();
        }
        return selectedDMA;
    }

    @Override
    public void setSelectedDMA(DMA selectedDMA) {
        this.selectedDMA = selectedDMA;
    }

    @Override
    public Retailer getSelectedRetailer() {
        if (selectedRetailer == null) {
            selectedRetailer = new Retailer();
        }
        return selectedRetailer;
    }

    @Override
    public void setSelectedRetailer(Retailer selectedRetailer) {
        this.selectedRetailer = selectedRetailer;
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    public CartesianChartModel getRegistrationChartModel() {
        if (registrationChartModel == null) {
            createRegistrationChartModel();
        }
        return registrationChartModel;
    }

    public void setRegistrationChartModel(CartesianChartModel registrationChartModel) {
        this.registrationChartModel = registrationChartModel;
    }

    public CartesianChartModel getTopFiveDmaChartModel() {
        if (topFiveDmaChartModel == null) {
            createTopFiveRegistrationsByDMAReport();
        }
        return topFiveDmaChartModel;
    }

    public void setTopFiveDmaChartModel(CartesianChartModel topFiveDmaChartModel) {
        this.topFiveDmaChartModel = topFiveDmaChartModel;
    }

    public CartesianChartModel getTopFiveSourceChartModel() {
        if ((topFiveSourceChartModel == null)) {
            createRegistrationsBySourceSummaryReport();
        }
        return topFiveSourceChartModel;
    }

    public void setTopFiveSourceChartModel(CartesianChartModel topFiveSourceChartModel) {
        this.topFiveSourceChartModel = topFiveSourceChartModel;
    }

    public DmaRegistrationSummaryReport getDmaRegistrationSummaryReport() {
        if (dmaRegistrationSummaryReport == null) {
            createDmaRegistrationSummaryReport();
        }
        return dmaRegistrationSummaryReport;
    }

    public void setDmaRegistrationSummaryReport(DmaRegistrationSummaryReport dmaRegistrationSummaryReport) {
        this.dmaRegistrationSummaryReport = dmaRegistrationSummaryReport;
    }

    private Long calculateChartMax(long topFiveNumber) {
        return Math.round(topFiveNumber * 1.1);
    }

    public long getMaxChartHeight() {
        return maxChartHeight;
    }

    public void setMaxChartHeight(long maxChartHeight) {
        this.maxChartHeight = maxChartHeight;
    }

    public void reset() {
        registrationSummary = null;
    }

    public void updateRegistrationSummary() {
        registrationSummary = registrationMgr.fetchRegistrationSummary(startDate, endDate, company);
        if (registrationSummary == null) {
            registrationSummary = new RegistrationSummary();
            registrationSummary.setTotalRegistrations(0L);
            registrationSummary.setTotalActiveSubscribers(0L);
            registrationSummary.setTotalOptOuts(0L);
        }

    }

    public void updateInterface() {
        System.err.println("updateInterface() startDate:"+startDate);
        System.err.println("updateInterface() endDate:"+endDate);
        this.prepareReportCriteria(companyMgr, dmaMgr, propertyMgr, retailerMgr);   
        System.err.println("after updateInterface() startDate:"+startDate);
        System.err.println("after updateInterface() endDate:"+endDate);
        updateRegistrationSummary();
        createRegistrationChartModel();
        createDmaRegistrationSummaryReport();
        createTopFiveRegistrationsByDMAReport();
        
    }

    public void updateDmaInterface() {
        this.prepareReportCriteria(companyMgr, dmaMgr, propertyMgr, retailerMgr);
        createDmaRegistrationSummaryReport();
        createTopFiveRegistrationsByDMAReport();
    }

    public void prepareVars() {
        today = new Date();
        dmaRegistrationSummaryReport = null;
        selectedDMA = null;
        selectedProperty = null;
        company = null;
        topFiveDmaChartModel = null;
        registrationChartModel = null;
        this.prepareReportCriteria(companyMgr, dmaMgr, propertyMgr, retailerMgr);
    }

    public String prepareList() {
        prepareVars();
        return "/geo-reports/Registration?faces-redirect=true";
    }

    public String prepareDMAReport() {
        prepareVars();
        return "/geo-reports/DmaRegistrations?faces-redirect=true";
    }

    public String prepareSourceReport() {
        prepareVars();
        createRegistrationsBySourceSummaryReport();
        return "/geo-reports/SourceRegistrations?faces-redirect=true";
    }

    private Long getMaxTotalRegistration(Collection<Long> maxValue) {
        return Collections.max(maxValue);
    }

    public Long getRegistrationBarChartMax() {
        LinkedHashMap<Object, Number> totalRegistrationsMap = new LinkedHashMap<Object, Number>();

        Collection<Long> values = new ArrayList<Long>();
        long maxValue = 0L;
        registrationSummaries = registrationMgr.fetchRegistrationSummaries(startDate, endDate, getCompany());
        if (registrationSummaries != null) {
            for (RegistrationSummary summary : registrationSummaries) {
                String dateStr = DateUtil.formatDateForChart(summary.getEventDate());
                totalRegistrationsMap.put(dateStr, summary.getTotalRegistrations());
            }
            for (Number number : totalRegistrationsMap.values()) {
                values.add(number.longValue());
            }
            maxValue = getMaxTotalRegistration(values);
        }
        return Math.round(maxValue * 1.1);
    }

    private void createRegistrationChartModel() {
        registrationChartModel = new CartesianChartModel();

        ChartSeries totalRegistrations = new ChartSeries();
        totalRegistrations.setLabel("Total Subscribers");

        ChartSeries activeUsers = new ChartSeries();
        activeUsers.setLabel("Active Subscribers");

        ChartSeries optOuts = new ChartSeries();
        optOuts.setLabel("In-active Subscribers");

        LinkedHashMap<Object, Number> activeUsersMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> optOutsMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalRegistrationsMap = new LinkedHashMap<Object, Number>();
        SortedMap sortedRegistrationsMap = new TreeMap();
        SortedMap sortedActiveUserMap = new TreeMap();
        SortedMap sortedOptOutsMap = new TreeMap();

        registrationSummaries = registrationMgr.fetchRegistrationSummaries(startDate, endDate, getCompany());
        if (registrationSummaries != null) {
            for (RegistrationSummary summary : registrationSummaries) {
                String dateStr = DateUtil.formatDateForChart(summary.getEventDate());
                activeUsersMap.put(dateStr, summary.getTotalActiveSubscribers());
                optOutsMap.put(dateStr, summary.getTotalOptOuts());
                totalRegistrationsMap.put(dateStr, summary.getTotalRegistrations());
            }
        } else {
            String dateStr = DateUtil.formatDateForChart(startDate);
            activeUsersMap.put(dateStr, 0L);
            optOutsMap.put(dateStr, 0L);
            totalRegistrationsMap.put(dateStr, 0L);
        }

        sortedRegistrationsMap.putAll(totalRegistrationsMap);
        sortedActiveUserMap.putAll(activeUsersMap);
        sortedOptOutsMap.putAll(optOutsMap);

        totalRegistrations.setData(sortedRegistrationsMap);
        activeUsers.setData(sortedActiveUserMap);
        optOuts.setData(sortedOptOutsMap);

        registrationChartModel.addSeries(totalRegistrations);
        registrationChartModel.addSeries(activeUsers);
        registrationChartModel.addSeries(optOuts);
    }

    private void createTopFiveRegistrationsBySourceReport(List<RegistrationsBySourceSummaryRow> summaryRowList, Date current, Date oneMonth, Date twoMonth, Date threeMonth) {
        if (summaryRowList.isEmpty()) {
            return;
        }
        String currentMonth = DateUtil.formatDateForChart(current);
        String pastOneMonth = DateUtil.formatDateForReport(oneMonth);
        String pastTwoMonths = DateUtil.formatDateForReport(twoMonth);
        String pastThreeMonths = DateUtil.formatDateForReport(threeMonth);

        topFiveSourceChartModel = new CartesianChartModel();
        ChartSeries topFiveSourceOne = new ChartSeries();
        ChartSeries topFiveSourceTwo = new ChartSeries();
        ChartSeries topFiveSourceThree = new ChartSeries();
        ChartSeries topFiveSourceFour = new ChartSeries();
        ChartSeries topFiveSourceFive = new ChartSeries();

        Collections.sort(summaryRowList);
        if (summaryRowList.isEmpty()) {
            return;
        }
        for (RegistrationsBySourceSummaryRow summaryRow : summaryRowList) {
            maxChartHeight = calculateChartMax(summaryRow.getTotalRegistrationsCurrentMonth());

            topFiveSourceOne.setLabel(summaryRow.getKeyword().getKeyword());
            topFiveSourceOne.set(currentMonth, summaryRowList.get(0).getTotalRegistrationsCurrentMonth());
            topFiveSourceOne.set(pastOneMonth, summaryRowList.get(0).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceOne.set(pastTwoMonths, summaryRowList.get(0).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceOne.set(pastThreeMonths, summaryRowList.get(0).getTotalRegistrationsPreviousThreeMonths());
        }
        if (summaryRowList.size() < 5) {
            if (summaryRowList.size() == 1) {
                maxChartHeight = calculateChartMax(summaryRowList.get(0).getTotalRegistrationsCurrentMonth());

                topFiveSourceOne.setLabel(summaryRowList.get(0).getKeyword().getKeyword());
                topFiveSourceOne.set(currentMonth, summaryRowList.get(0).getTotalRegistrationsCurrentMonth());
                topFiveSourceOne.set(pastOneMonth, summaryRowList.get(0).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceOne.set(pastTwoMonths, summaryRowList.get(0).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceOne.set(pastThreeMonths, summaryRowList.get(0).getTotalRegistrationsPreviousThreeMonths());
            }
            if (summaryRowList.size() == 2) {
                maxChartHeight = calculateChartMax(summaryRowList.get(1).getTotalRegistrationsCurrentMonth());

                topFiveSourceOne.setLabel(summaryRowList.get(0).getKeyword().getKeyword());
                topFiveSourceOne.set(currentMonth, summaryRowList.get(0).getTotalRegistrationsCurrentMonth());
                topFiveSourceOne.set(pastOneMonth, summaryRowList.get(0).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceOne.set(pastTwoMonths, summaryRowList.get(0).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceOne.set(pastThreeMonths, summaryRowList.get(0).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceTwo.setLabel(summaryRowList.get(1).getKeyword().getKeyword());
                topFiveSourceTwo.set(currentMonth, summaryRowList.get(1).getTotalRegistrationsCurrentMonth());
                topFiveSourceTwo.set(pastOneMonth, summaryRowList.get(1).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceTwo.set(pastTwoMonths, summaryRowList.get(1).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceTwo.set(pastThreeMonths, summaryRowList.get(1).getTotalRegistrationsPreviousThreeMonths());
            }
            if (summaryRowList.size() == 3) {
                maxChartHeight = calculateChartMax(summaryRowList.get(2).getTotalRegistrationsCurrentMonth());

                topFiveSourceOne.setLabel(summaryRowList.get(0).getKeyword().getKeyword());
                topFiveSourceOne.set(currentMonth, summaryRowList.get(0).getTotalRegistrationsCurrentMonth());
                topFiveSourceOne.set(pastOneMonth, summaryRowList.get(0).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceOne.set(pastTwoMonths, summaryRowList.get(0).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceOne.set(pastThreeMonths, summaryRowList.get(0).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceTwo.setLabel(summaryRowList.get(1).getKeyword().getKeyword());
                topFiveSourceTwo.set(currentMonth, summaryRowList.get(1).getTotalRegistrationsCurrentMonth());
                topFiveSourceTwo.set(pastOneMonth, summaryRowList.get(1).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceTwo.set(pastTwoMonths, summaryRowList.get(1).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceTwo.set(pastThreeMonths, summaryRowList.get(1).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceThree.setLabel(summaryRowList.get(2).getKeyword().getKeyword());
                topFiveSourceThree.set(currentMonth, summaryRowList.get(2).getTotalRegistrationsCurrentMonth());
                topFiveSourceThree.set(pastOneMonth, summaryRowList.get(2).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceThree.set(pastTwoMonths, summaryRowList.get(2).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceThree.set(pastThreeMonths, summaryRowList.get(2).getTotalRegistrationsPreviousThreeMonths());
            }
            if (summaryRowList.size() == 4) {
                maxChartHeight = calculateChartMax(summaryRowList.get(3).getTotalRegistrationsCurrentMonth());

                topFiveSourceOne.setLabel(summaryRowList.get(0).getKeyword().getKeyword());
                topFiveSourceOne.set(currentMonth, summaryRowList.get(0).getTotalRegistrationsCurrentMonth());
                topFiveSourceOne.set(pastOneMonth, summaryRowList.get(0).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceOne.set(pastTwoMonths, summaryRowList.get(0).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceOne.set(pastThreeMonths, summaryRowList.get(0).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceTwo.setLabel(summaryRowList.get(1).getKeyword().getKeyword());
                topFiveSourceTwo.set(currentMonth, summaryRowList.get(1).getTotalRegistrationsCurrentMonth());
                topFiveSourceTwo.set(pastOneMonth, summaryRowList.get(1).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceTwo.set(pastTwoMonths, summaryRowList.get(1).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceTwo.set(pastThreeMonths, summaryRowList.get(1).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceThree.setLabel(summaryRowList.get(2).getKeyword().getKeyword());
                topFiveSourceThree.set(currentMonth, summaryRowList.get(2).getTotalRegistrationsCurrentMonth());
                topFiveSourceThree.set(pastOneMonth, summaryRowList.get(2).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceThree.set(pastTwoMonths, summaryRowList.get(2).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceThree.set(pastThreeMonths, summaryRowList.get(2).getTotalRegistrationsPreviousThreeMonths());

                topFiveSourceFour.setLabel(summaryRowList.get(3).getKeyword().getKeyword());
                topFiveSourceFour.set(currentMonth, summaryRowList.get(3).getTotalRegistrationsCurrentMonth());
                topFiveSourceFour.set(pastOneMonth, summaryRowList.get(3).getTotalRegistrationsPreviousOneMonth());
                topFiveSourceFour.set(pastTwoMonths, summaryRowList.get(3).getTotalRegistrationsPreviousTwoMonths());
                topFiveSourceFour.set(pastThreeMonths, summaryRowList.get(3).getTotalRegistrationsPreviousThreeMonths());
            }
        } else {
            List<RegistrationsBySourceSummaryRow> sourceSubList = summaryRowList.subList(0, 5);
            maxChartHeight = calculateChartMax(sourceSubList.get(4).getTotalRegistrationsCurrentMonth());

            topFiveSourceOne.setLabel(sourceSubList.get(0).getKeyword().getKeyword());
            topFiveSourceOne.set(currentMonth, sourceSubList.get(0).getTotalRegistrationsCurrentMonth());
            topFiveSourceOne.set(pastOneMonth, sourceSubList.get(0).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceOne.set(pastTwoMonths, sourceSubList.get(0).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceOne.set(pastThreeMonths, sourceSubList.get(0).getTotalRegistrationsPreviousThreeMonths());

            topFiveSourceTwo.setLabel(summaryRowList.get(1).getKeyword().getKeyword());
            topFiveSourceTwo.set(currentMonth, sourceSubList.get(1).getTotalRegistrationsCurrentMonth());
            topFiveSourceTwo.set(pastOneMonth, sourceSubList.get(1).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceTwo.set(pastTwoMonths, sourceSubList.get(1).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceTwo.set(pastThreeMonths, sourceSubList.get(1).getTotalRegistrationsPreviousThreeMonths());

            topFiveSourceThree.setLabel(summaryRowList.get(2).getKeyword().getKeyword());
            topFiveSourceThree.set(currentMonth, sourceSubList.get(2).getTotalRegistrationsCurrentMonth());
            topFiveSourceThree.set(pastOneMonth, sourceSubList.get(2).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceThree.set(pastTwoMonths, sourceSubList.get(2).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceThree.set(pastThreeMonths, sourceSubList.get(2).getTotalRegistrationsPreviousThreeMonths());

            topFiveSourceFour.setLabel(summaryRowList.get(3).getKeyword().getKeyword());
            topFiveSourceFour.set(currentMonth, sourceSubList.get(3).getTotalRegistrationsCurrentMonth());
            topFiveSourceFour.set(pastOneMonth, sourceSubList.get(3).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceFour.set(pastTwoMonths, sourceSubList.get(3).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceFour.set(pastThreeMonths, sourceSubList.get(3).getTotalRegistrationsPreviousThreeMonths());

            topFiveSourceFive.setLabel(sourceSubList.get(4).getKeyword().getKeyword());
            topFiveSourceFive.set(currentMonth, sourceSubList.get(4).getTotalRegistrationsCurrentMonth());
            topFiveSourceFive.set(pastOneMonth, sourceSubList.get(4).getTotalRegistrationsPreviousOneMonth());
            topFiveSourceFive.set(pastTwoMonths, sourceSubList.get(4).getTotalRegistrationsPreviousTwoMonths());
            topFiveSourceFive.set(pastThreeMonths, sourceSubList.get(4).getTotalRegistrationsPreviousThreeMonths());
        }
        if (topFiveSourceOne != null) {
            topFiveSourceChartModel.addSeries(topFiveSourceOne);
        }
        if (topFiveSourceTwo != null) {
            topFiveSourceChartModel.addSeries(topFiveSourceTwo);
        }
        if (topFiveSourceThree != null) {
            topFiveSourceChartModel.addSeries(topFiveSourceThree);
        }
        if (topFiveSourceFour != null) {
            topFiveSourceChartModel.addSeries(topFiveSourceFour);
        }
        if (topFiveSourceFive != null) {
            topFiveSourceChartModel.addSeries(topFiveSourceFive);
        }


    }

    private void createTopFiveRegistrationsByDMAReport() {
        //create objects to hold values
        LinkedHashMap<Object, Number> activeForDmaMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> optoutForDmaMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalForDmaMap = new LinkedHashMap<Object, Number>();
        TreeMap<Long, RegistrationSummary> totalForDmaTreeMap = new TreeMap<Long, RegistrationSummary>();

        topFiveDmaChartModel = new CartesianChartModel();

        ChartSeries totalRegistrations = new ChartSeries();
        totalRegistrations.setLabel("Total Registrations");

        ChartSeries activeUsers = new ChartSeries();
        activeUsers.setLabel("Active Users");

        ChartSeries optOuts = new ChartSeries();
        optOuts.setLabel("Opt-outs");



        List<RegistrationSummary> summaries = registrationMgr.fetchRegistrationSummariesByDMA(startDate, endDate, company);

        //add all of the active subscribers,opt-outs, and totals by DMA
        if (summaries != null) {
            List<DMA> myDmaList = new ArrayList<DMA>();
            for (RegistrationSummary summary : summaries) {
                //save a list of DMA's
                DMA dma = summary.getDma();
                if (!myDmaList.contains(dma)) {
                    myDmaList.add(dma);
                }
                if (activeForDmaMap.containsKey(dma)) {
                    long newVal = (Long) activeForDmaMap.get(dma) + summary.getTotalActiveSubscribers();
                    activeForDmaMap.put(dma, newVal);

                } else {
                    activeForDmaMap.put(dma, summary.getTotalActiveSubscribers());
                }
                if (optoutForDmaMap.containsKey(dma)) {
                    long newVal = (Long) optoutForDmaMap.get(dma) + summary.getTotalOptOuts();
                    optoutForDmaMap.put(dma, newVal);
                } else {
                    optoutForDmaMap.put(dma, summary.getTotalOptOuts());
                }
                if (totalForDmaMap.containsKey(dma)) {
                    long newVal = (Long) totalForDmaMap.get(dma) + summary.getTotalRegistrations();
                    totalForDmaMap.put(dma, newVal);
                } else {
                    totalForDmaMap.put(dma, summary.getTotalRegistrations());
                }

            }

            //for each DMA create a registration summary and add to tree map for sorting by key: total registrations
            for (DMA dma : myDmaList) {
                RegistrationSummary topFiveSummary = new RegistrationSummary();
                Long totalVal = (Long) totalForDmaMap.get(dma);
                Long activeVal = (Long) activeForDmaMap.get(dma);
                Long optoutVal = (Long) optoutForDmaMap.get(dma);
                topFiveSummary.setDma(dma);
                topFiveSummary.setTotalRegistrations(totalVal);
                topFiveSummary.setTotalActiveSubscribers(activeVal);
                topFiveSummary.setTotalOptOuts(optoutVal);
                totalForDmaTreeMap.put(totalVal, topFiveSummary);
                activeUsers.setData(activeForDmaMap);
                System.out.println("set active users");

                optOuts.setData(optoutForDmaMap);
                System.out.println("set optouts");

            }

            //loop through; add to chart
            Iterator<Long> total = totalForDmaTreeMap.descendingKeySet().iterator();
            if (totalForDmaTreeMap.size() <= 5) {
                Long longKey = total.next();
                totalRegistrations.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalRegistrations());
                activeUsers.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalActiveSubscribers());
                optOuts.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalOptOuts());
            } else {
                for (int i = 0; i < 5; i++) {
                    Long longKey = total.next();
                    totalRegistrations.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalRegistrations());
                    activeUsers.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalActiveSubscribers());
                    optOuts.set(totalForDmaTreeMap.get(longKey).getDma().getName(), totalForDmaTreeMap.get(longKey).getTotalOptOuts());
                }
            }
            //get first value for max chart height for a pretty UI
            maxChartHeight = calculateChartMax(totalForDmaTreeMap.descendingKeySet().first());

            topFiveDmaChartModel.addSeries(totalRegistrations);
            topFiveDmaChartModel.addSeries(activeUsers);
            topFiveDmaChartModel.addSeries(optOuts);
        } else {
            String dateStr = DateUtil.formatDateForChart(startDate);
            activeForDmaMap.put(dateStr, 0L);
            optoutForDmaMap.put(dateStr, 0L);
            totalForDmaMap.put(dateStr, 0L);
            activeUsers.setData(activeForDmaMap);
            optOuts.setData(optoutForDmaMap);
            totalRegistrations.setData(totalForDmaMap);
            topFiveDmaChartModel.addSeries(totalRegistrations);
            topFiveDmaChartModel.addSeries(activeUsers);
            topFiveDmaChartModel.addSeries(optOuts);
        }
    }

    private void createDmaRegistrationSummaryReport() {
        
        //create objects to hold values
        HashMap<Long, DmaRegistrationSummaryRow> summaryMap = new HashMap<Long, DmaRegistrationSummaryRow>();
        LinkedHashMap<Object, Number> activeForDmaMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> optoutForDmaMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalForDmaMap = new LinkedHashMap<Object, Number>();

        List<DMA> dmaList = new ArrayList<DMA>();

        //get the list of summaries by dma
        List<RegistrationSummary> summaries = registrationMgr.fetchRegistrationSummariesByDMA(getStartDate(), getEndDate(), company);

        //add all of the active subscribers,opt-outs, and totals by DMA
        if (summaries != null) {

            logger.info("Very first set of summaries: " + summaries.size());
            for (RegistrationSummary summary : summaries) {
                DMA dma = summary.getDma();
                logger.info("Dma id: " + dma.getId() + " dma name: " + dma.getName());
                if (!dmaList.contains(dma)) {
                    dmaList.add(dma);
                }
                if (!activeForDmaMap.containsKey(dma)) {
                    activeForDmaMap.put(dma, summary.getTotalActiveSubscribers());
                }
                if (!optoutForDmaMap.containsKey(dma)) {
                    optoutForDmaMap.put(dma, summary.getTotalOptOuts());
                }
                if (!totalForDmaMap.containsKey(dma)) {
                    totalForDmaMap.put(dma, summary.getTotalRegistrations());
                }

            }

            //create final list of registration summaries that contain totals by DMA
            dmaRegistrationSummaryReport = new DmaRegistrationSummaryReport();
            summaryMap = new HashMap<Long, DmaRegistrationSummaryRow>();
            List<DmaRegistrationSummaryRow> summaryRowList = new ArrayList<DmaRegistrationSummaryRow>();
            Long finalTotalRegistrations = 0L;
            Long finalTotalOptouts = 0L;
            Long finalTotalActiveSubscribers = 0L;

            for (DMA myDma : dmaList) {

                logger.info("Total set -- dma id: " + myDma.getId() + " dma name: " + myDma.getName());
                DmaRegistrationSummaryRow summaryRow = new DmaRegistrationSummaryRow();
                summaryRow.setDma(myDma);
                if (totalForDmaMap.containsKey(myDma)) {
                    summaryRow.setTotalRegistrations((Long) totalForDmaMap.get(myDma));
                }
                if (activeForDmaMap.containsKey(myDma)) {
                    summaryRow.setTotalActiveSubscribers((Long) activeForDmaMap.get(myDma));
                }
                if (optoutForDmaMap.containsKey(myDma)) {
                    summaryRow.setTotalOptOuts((Long) optoutForDmaMap.get(myDma));
                }

                summaryRowList.add(summaryRow);
                finalTotalRegistrations += summaryRow.getTotalRegistrations();
                finalTotalOptouts += summaryRow.getTotalOptOuts();
                finalTotalActiveSubscribers += summaryRow.getTotalActiveSubscribers();

            }
            dmaRegistrationSummaryReport.setFinalCountOptOut(finalTotalOptouts);
            dmaRegistrationSummaryReport.setFinalCountRegistrations(finalTotalRegistrations);
            dmaRegistrationSummaryReport.setFinalCountSubscribers(finalTotalActiveSubscribers);
            for (DmaRegistrationSummaryRow percentRow : summaryRowList) {
                //calculate market percent
                double percentDouble = ((double) percentRow.getTotalActiveSubscribers() / finalTotalActiveSubscribers * 100);
                String marketPercent = formatDoubleToDecimal(percentDouble);

                System.out.println("market percent: " + marketPercent);
                percentRow.setMarketPercent(marketPercent);

                summaryMap.put(percentRow.getDma().getId(), percentRow);

            }            

        }else{
            dmaRegistrationSummaryReport = new DmaRegistrationSummaryReport();
            summaryMap = new HashMap<Long, DmaRegistrationSummaryRow>();
        }
        dmaRegistrationSummaryReport.setDmaSummaries(summaryMap);
    }

    public void createRegistrationsBySourceSummaryReport() {

        //create objects to hold total registrations by month
        LinkedHashMap<Object, Number> totalCurrentMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalOneMonthPreviousMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalTwoMonthsPreviousMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalThreeMonthsPreviousMap = new LinkedHashMap<Object, Number>();

        //get current month's date
        Date currentMonthStartDate = DateUtil.getFirstDayOfMonth(today);
        Date currentMonthEndDate = DateUtil.getLastDayOfMonth(today);

        //get one month previous start date
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        Date oneMonthPreviousStartDate = DateUtil.getFirstDayOfMonth(c.getTime());
        Date oneMonthPreviousEndDate = DateUtil.getLastDayOfMonth(c.getTime());

        //get two months previous start date
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, -2);
        Date twoMonthsPreviousStartDate = DateUtil.getFirstDayOfMonth(c2.getTime());
        Date twoMonthsPreviousEndDate = DateUtil.getLastDayOfMonth(c2.getTime());

        //get three months previous start date
        Calendar c3 = Calendar.getInstance();
        c3.add(Calendar.MONTH, -3);
        Date threeMonthsPreviousStartDate = DateUtil.getFirstDayOfMonth(c3.getTime());
        Date threeMonthsPreviousEndDate = DateUtil.getLastDayOfMonth(c3.getTime());

        //get summaries by month
        List<RegistrationSourceSummary> currentMonthSummaries = registrationSourceMgr.fetchRegistrationSourceSummaries(currentMonthStartDate, currentMonthEndDate, company);
        List<RegistrationSourceSummary> oneMonthPreviousSummaries = registrationSourceMgr.fetchRegistrationSourceSummaries(oneMonthPreviousStartDate, oneMonthPreviousEndDate, company);
        List<RegistrationSourceSummary> twoMonthsPreviousSummaries = registrationSourceMgr.fetchRegistrationSourceSummaries(twoMonthsPreviousStartDate, twoMonthsPreviousEndDate, company);
        List<RegistrationSourceSummary> threeMonthsPreviousSummaries = registrationSourceMgr.fetchRegistrationSourceSummaries(threeMonthsPreviousStartDate, threeMonthsPreviousEndDate, company);

        registrationBySourceSummaryReport = new RegistrationsBySourceSummaryReport();
        registrationBySourceSummaryReport.setCurrentMonthHeader(DateUtil.formatDateForReport(currentMonthStartDate));
        registrationBySourceSummaryReport.setPreviousMonthHeader(DateUtil.formatDateForReport(oneMonthPreviousStartDate));
        registrationBySourceSummaryReport.setPreviousTwoMonthsHeader(DateUtil.formatDateForReport(twoMonthsPreviousStartDate));
        registrationBySourceSummaryReport.setPreviousThreeMonthsHeader(DateUtil.formatDateForReport(threeMonthsPreviousStartDate));

        HashMap summaryMap = new HashMap<Long, RegistrationsBySourceSummaryRow>();
        List<RegistrationsBySourceSummaryRow> summaryRowList = new ArrayList<RegistrationsBySourceSummaryRow>();
        Long finalTotalCurrent = 0L;
        Long finalTotalOneMonthPrevious = 0L;
        Long finalTotalTwoMonthsPrevious = 0L;
        Long finalTotalThreeMonthsPrevious = 0L;

        //get company keywords and add to a list
        List<Keyword> keywordList = keywordMgr.findAllByCompany(company);
        if (keywordList != null) {
            System.out.println("keyword list size is " + keywordList.size());
            for (Keyword keyword : keywordList) {
                System.out.println("keyword is " + keyword.getKeyword());

                //add all of the registrations by keyword for current month
                if (currentMonthSummaries != null) {
                    for (RegistrationSourceSummary summary : currentMonthSummaries) {
                        if (summary.getKeyword().getKeyword().equals(keyword.getKeyword())) {
                            if (totalCurrentMap.containsKey(keyword)) {
                                long newVal = (Long) totalCurrentMap.get(keyword) + summary.getTotalRegistrations();
                                totalCurrentMap.put(keyword, newVal);
                            } else {
                                totalCurrentMap.put(keyword, summary.getTotalRegistrations());
                            }

                        }
                    }
                }

                //add all of the registrations by keyword for one month previous
                if (oneMonthPreviousSummaries != null) {
                    for (RegistrationSourceSummary oneSummary : oneMonthPreviousSummaries) {
                        if (oneSummary.getKeyword().getKeyword().equals(keyword.getKeyword())) {
                            if (totalOneMonthPreviousMap.containsKey(keyword)) {
                                long newVal = (Long) totalOneMonthPreviousMap.get(keyword) + oneSummary.getTotalRegistrations();
                                totalOneMonthPreviousMap.put(keyword, newVal);
                            } else {
                                totalOneMonthPreviousMap.put(keyword, oneSummary.getTotalRegistrations());
                            }

                        }
                    }
                }

                //add all of the registrations by keyword for two months previous
                if (twoMonthsPreviousSummaries != null) {
                    for (RegistrationSourceSummary twoSummary : twoMonthsPreviousSummaries) {
                        if (twoSummary.getKeyword().getKeyword().equals(keyword.getKeyword())) {
                            if (totalTwoMonthsPreviousMap.containsKey(keyword)) {
                                long newVal = (Long) totalTwoMonthsPreviousMap.get(keyword) + twoSummary.getTotalRegistrations();
                                totalTwoMonthsPreviousMap.put(keyword, newVal);
                            } else {
                                totalTwoMonthsPreviousMap.put(keyword, twoSummary.getTotalRegistrations());
                            }

                        }
                    }
                }
                //add all of the registrations by keyword for three months previous
                if (threeMonthsPreviousSummaries != null) {
                    for (RegistrationSourceSummary threeSummary : threeMonthsPreviousSummaries) {
                        if (threeSummary.getKeyword().getKeyword().equals(keyword.getKeyword())) {
                            if (totalThreeMonthsPreviousMap.containsKey(keyword)) {
                                long newVal = (Long) totalThreeMonthsPreviousMap.get(keyword) + threeSummary.getTotalRegistrations();
                                totalThreeMonthsPreviousMap.put(keyword, newVal);
                            } else {
                                totalThreeMonthsPreviousMap.put(keyword, threeSummary.getTotalRegistrations());
                            }

                        }
                    }
                }


                RegistrationsBySourceSummaryRow summaryRow = new RegistrationsBySourceSummaryRow();
                summaryRow.setKeyword(keyword);
                if (totalCurrentMap.containsKey(keyword)) {
                    summaryRow.setTotalRegistrationsCurrentMonth((Long) totalCurrentMap.get(keyword));
                } else {
                    summaryRow.setTotalRegistrationsCurrentMonth(0L);
                }
                if (totalOneMonthPreviousMap.containsKey(keyword)) {
                    summaryRow.setTotalRegistrationsPreviousOneMonth((Long) totalOneMonthPreviousMap.get(keyword));
                } else {
                    summaryRow.setTotalRegistrationsPreviousOneMonth(0L);
                }
                if (totalTwoMonthsPreviousMap.containsKey(keyword)) {
                    summaryRow.setTotalRegistrationsPreviousTwoMonths((Long) totalTwoMonthsPreviousMap.get(keyword));

                } else {
                    summaryRow.setTotalRegistrationsPreviousTwoMonths(0L);
                }
                if (totalThreeMonthsPreviousMap.containsKey(keyword)) {
                    summaryRow.setTotalRegistrationsPreviousThreeMonths((Long) totalThreeMonthsPreviousMap.get(keyword));
                } else {
                    summaryRow.setTotalRegistrationsPreviousThreeMonths(0L);
                }
                summaryRowList.add(summaryRow);
                if (summaryRow.getTotalRegistrationsCurrentMonth() != null) {
                    finalTotalCurrent += summaryRow.getTotalRegistrationsCurrentMonth();
                }
                if (summaryRow.getTotalRegistrationsPreviousOneMonth() != null) {
                    finalTotalOneMonthPrevious += summaryRow.getTotalRegistrationsPreviousOneMonth();
                }
                if (summaryRow.getTotalRegistrationsPreviousTwoMonths() != null) {
                    finalTotalTwoMonthsPrevious += summaryRow.getTotalRegistrationsPreviousTwoMonths();
                }
                if (summaryRow.getTotalRegistrationsPreviousThreeMonths() != null) {
                    finalTotalThreeMonthsPrevious += summaryRow.getTotalRegistrationsPreviousThreeMonths();

                }



            }
            registrationBySourceSummaryReport.setFinalCountRegistrationsCurrentMonth(finalTotalCurrent);
            registrationBySourceSummaryReport.setFinalCountRegistrationsPreviousOneMonth(finalTotalOneMonthPrevious);
            registrationBySourceSummaryReport.setFinalCountRegistrationsPreviousTwoMonths(finalTotalTwoMonthsPrevious);
            registrationBySourceSummaryReport.setFinalCountRegistrationsPreviousThreeMonths(finalTotalThreeMonthsPrevious);

            createTopFiveRegistrationsBySourceReport(summaryRowList, currentMonthStartDate, oneMonthPreviousStartDate, twoMonthsPreviousStartDate, threeMonthsPreviousStartDate);

            for (RegistrationsBySourceSummaryRow percentRow : summaryRowList) {

                //calculate percent change
                String percentChange = "";
                if (percentRow.getTotalRegistrationsCurrentMonth() != null
                        && percentRow.getTotalRegistrationsPreviousOneMonth() != null) {
                    double percentDouble = ((double) (percentRow.getTotalRegistrationsCurrentMonth()
                            - percentRow.getTotalRegistrationsPreviousOneMonth()) / percentRow.getTotalRegistrationsCurrentMonth()) * 100;
                    percentChange = formatDoubleToDecimal(percentDouble);
                }

                percentRow.setPercentChange(percentChange);

                summaryMap.put(percentRow.getKeyword().getId(), percentRow);


            }
            registrationBySourceSummaryReport.setSourceSummaries(summaryMap);





        }


    }

    private String formatDoubleToDecimal(double aNumber) {
        String formattedNumber = null;
        try {
            DecimalFormat myFormatter = new DecimalFormat("##.##");
            formattedNumber = myFormatter.format(aNumber);
        } catch (NumberFormatException ex) {
            return formattedNumber;
        }
        return formattedNumber;
    }
}
