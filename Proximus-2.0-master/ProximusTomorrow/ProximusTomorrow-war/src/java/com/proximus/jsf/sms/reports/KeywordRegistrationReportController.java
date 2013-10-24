/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.sms.report.ViewKeywordSummaryStats;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.manager.sms.LocaleManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.SourceTypeManagerLocal;
import com.proximus.manager.sms.report.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author Gilberto Gaxiola
 * @author Eric Johansson
 */
@ManagedBean(name = "keywordRegistrationReportController")
@SessionScoped
public class KeywordRegistrationReportController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(KeywordRegistrationReportController.class.getName());
    private static final long serialVersionUID = 1;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    KeywordManagerLocal keywordMgr;
    @EJB
    SourceTypeManagerLocal sourceTypeMgr;
    @EJB
    ViewActiveUsersByKeywordManagerLocal activeUsersByKeywordMgr;
    @EJB
    ViewKeywordTotalOptInsManagerLocal keywordTotalOptInsMgr;
    @EJB
    ViewKeywordTotalOptInsPendingManagerLocal keywordTotalOptInsPendingMgr;
    @EJB
    ViewKeywordTotalOptOutsManagerLocal keywordTotalOptOutsMgr;
    @EJB
    LocaleManagerLocal localeMgr;
    private List<ViewKeywordSummaryStats> keywordSummaryStatsList;
    private CartesianChartModel optInsChart;
    private long maxChartValue = 0;
    private int top;
    private List<Integer> selectableTops;
    private long totalOptIns;
    private long totalOptInsPending;
    private long totalOptOuts;
    private float totalMarketShare;
    private final DecimalFormat formatter = new DecimalFormat("#.##");
    private Keyword importedKeyword;
    private com.proximus.data.sms.Locale selectedLocale;
    private List<String> localeList;

    public long getMaxChartValue() {
        return maxChartValue;
    }

    public void setMaxChartValue(long maxChartValue) {
        this.maxChartValue = maxChartValue;
    }

    public CartesianChartModel getOptInsChart() {
        if (optInsChart == null) {
            optInsChart = new CartesianChartModel();
        }
        return optInsChart;
    }

    public void setOptInsChart(CartesianChartModel optInsChart) {
        this.optInsChart = optInsChart;
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

    @Override
    public Date getStartDate() {
        if (startDate == null) {
            startDate = DateUtil.getFirstDayOfMonth(new Date());
        }
        return startDate;
    }

    @Override
    public void setStartDate(Date d) {
        if (d == null) {
            startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        } else {
            this.startDate = d;
        }
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForChart(getStartDate());
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForChart(getEndDate());
    }

    public com.proximus.data.sms.Locale getSelectedLocale() {
        if(selectedLocale==null){
            selectedLocale= new com.proximus.data.sms.Locale();
            selectedLocale.setName(null);
        }
        return selectedLocale;
    }

    public void setSelectedLocale(com.proximus.data.sms.Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }

    public List<String> getLocaleList() {
        if (localeList == null) {
            List<com.proximus.data.sms.Locale> list = localeMgr.getAllSortedByKeyword();
            localeList = new ArrayList<String>();
            for (com.proximus.data.sms.Locale l : list) {
                localeList.add(l.getName());
            }
        }
        return localeList;
    }

    public void setLocaleList(List<String> localeList) {
        this.localeList = localeList;
    }

    private void prepareVars() {
        this.startDate = DateUtil.getFirstDayOfMonth(new Date());
        this.endDate = new Date();
        this.company = companyMgr.find(this.getCompanyIdFromSession());
        this.keywordSummaryStatsList = new ArrayList<ViewKeywordSummaryStats>();
        this.top = 5;
        importedKeyword = keywordMgr.getKeywordByKeywordString("IMPORTED");
        selectedLocale = null;
        handleOptInChart();
        populateKeywordSummaryStats();
    }

    private void handleOptInChart() {
        maxChartValue = 0;
        optInsChart = new CartesianChartModel();
        int days = Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
        if (days == 0) {
            days = 1;
        }
        int interval = days;
        if (days > 10) {
            interval = 10;
        }
        Calendar c = Calendar.getInstance();

        com.proximus.data.sms.Locale realLocale = null;
        if (getSelectedLocale().getName() != null) {
            realLocale = localeMgr.getLocaleByName(getSelectedLocale().getName());
        }

        List<Keyword> keywords;
        if (realLocale == null) {
            keywords = activeUsersByKeywordMgr.getTopKeywordsByActiveUsers(this.getBrandFromSession(), endDate, top);
        } else {
            keywords = activeUsersByKeywordMgr.getTopKeywordsByActiveUsersAndLocale(this.getBrandFromSession(), realLocale, endDate, top);
        }
        for (Keyword series : keywords) {
            ChartSeries optIns = new ChartSeries();
            optIns.setLabel(series.getKeyword());
            Long activeUsersRunningTotal = activeUsersByKeywordMgr.getCountActiveUsersByKeyword(this.getBrandFromSession(), startDate, series);
            c.setTime(DateUtil.getStartOfDay(startDate));
            while (c.getTime().before(DateUtil.getEndOfDay(endDate))) {
                String dateStr = DateUtil.formatDateForChart(c.getTime());
                activeUsersRunningTotal = activeUsersByKeywordMgr.getCountActiveUsersByKeyword(this.getBrandFromSession(), c.getTime(), series);

                if (maxChartValue < activeUsersRunningTotal) {
                    maxChartValue = activeUsersRunningTotal;
                }
                optIns.set(dateStr, activeUsersRunningTotal);
                c.add(Calendar.DAY_OF_MONTH, days / interval);
            }

            optInsChart.addSeries(optIns);
        }
        if (maxChartValue % 6 != 0) {
            maxChartValue += (6 - (maxChartValue % 6));
        } else {
            maxChartValue += 6;
        }
        if (maxChartValue < 12) {
            maxChartValue = 12;
        }
    }

    public String updateInterface() {
        this.keywordSummaryStatsList = null;
        handleOptInChart();
        populateKeywordSummaryStats();
        return "/geo-reports/KeywordRegistration?faces-redirect=true";
    }

    public void populateKeywordSummaryStats() {
        Map<Keyword, ViewKeywordSummaryStats> summaryStats = new HashMap<Keyword, ViewKeywordSummaryStats>();

        this.totalOptIns = 0L;
        this.totalOptInsPending = 0L;
        this.totalOptOuts = 0L;
        com.proximus.data.sms.Locale realLocale = null;
        if (selectedLocale.getName() != null) {
            realLocale = localeMgr.getLocaleByName(getSelectedLocale().getName());
        }

        //Setting OptIns
        Map<Keyword, Long> optIns;
        if (realLocale == null) {
            optIns = keywordTotalOptInsMgr.getTotalPerKeywordByCompanyAndDate(this.getCompanyFromSession(), startDate, endDate);
        } else {
            optIns = keywordTotalOptInsMgr.getTotalPerKeywordByCompanyAndLocaleAndDate(this.getCompanyFromSession(), realLocale, startDate, endDate);
        }
        Set<Entry<Keyword, Long>> optInsSet = optIns.entrySet();
        for (Entry<Keyword, Long> entry : optInsSet) {
            if (!summaryStats.containsKey(entry.getKey())) {
                summaryStats.put(entry.getKey(), new ViewKeywordSummaryStats(entry.getKey()));
            }
            summaryStats.get(entry.getKey()).setTotalOptIns(entry.getValue());
            this.totalOptIns += entry.getValue();
        }

        //Settings OptInsPending
        Map<Keyword, Long> optInsPending;
        if (realLocale == null) {
            optInsPending = keywordTotalOptInsPendingMgr.getTotalPerKeywordByCompanyAndDate(this.getCompanyFromSession(), startDate, endDate);
        } else {
            optInsPending = keywordTotalOptInsPendingMgr.getTotalPerKeywordByCompanyAndLocaleAndDate(this.getCompanyFromSession(), realLocale, startDate, endDate);
        }
        Set<Entry<Keyword, Long>> optInsPendingSet = optInsPending.entrySet();
        for (Entry<Keyword, Long> entry : optInsPendingSet) {
            if (!summaryStats.containsKey(entry.getKey())) {
                summaryStats.put(entry.getKey(), new ViewKeywordSummaryStats(entry.getKey()));
            }
            summaryStats.get(entry.getKey()).setTotalOptInsPending(entry.getValue());
            this.totalOptInsPending += entry.getValue();
        }

        //Setting OptOuts
        Map<Keyword, Long> optOuts;
        if (realLocale == null) {
            optOuts = keywordTotalOptOutsMgr.getTotalPerKeywordByCompanyAndDate(this.getCompanyFromSession(), startDate, endDate);
        } else {
            optOuts = keywordTotalOptOutsMgr.getTotalPerKeywordByCompanyAndLocaleAndDate(this.getCompanyFromSession(), realLocale, startDate, endDate);
        }
        Set<Entry<Keyword, Long>> optOutsSet = optOuts.entrySet();
        for (Entry<Keyword, Long> entry : optOutsSet) {
            if (!summaryStats.containsKey(entry.getKey())) {
                summaryStats.put(entry.getKey(), new ViewKeywordSummaryStats(entry.getKey()));
            }
            summaryStats.get(entry.getKey()).setTotalOptOuts(entry.getValue());
            this.totalOptOuts += entry.getValue();
        }

        if (importedKeyword != null) {
            summaryStats.remove(importedKeyword);
        }


        this.keywordSummaryStatsList = new ArrayList<ViewKeywordSummaryStats>(summaryStats.values());
        this.totalMarketShare = 0f;
        if (this.keywordSummaryStatsList != null) {
            for (ViewKeywordSummaryStats v : keywordSummaryStatsList) {
                v.calculateMarketShare(this.totalOptIns);
                this.totalMarketShare += v.getMarketShare();
            }
        }
    }

    public float getTotalMarketShare() {
        return totalMarketShare;
    }

    public void setTotalMarketShare(float totalMarketShare) {
        this.totalMarketShare = totalMarketShare;
    }

    public String getFormattedTotalMarketShare() {

        String percentage = formatter.format(this.totalMarketShare * 100);

        return percentage + "%";
    }

    public long getTotalOptIns() {
        return totalOptIns;
    }

    public void setTotalOptIns(long totalOptIns) {
        this.totalOptIns = totalOptIns;
    }

    public long getTotalOptInsPending() {
        return totalOptInsPending;
    }

    public void setTotalOptInsPending(long totalOptInsPending) {
        this.totalOptInsPending = totalOptInsPending;
    }

    public long getTotalOptOuts() {
        return totalOptOuts;
    }

    public void setTotalOptOuts(long totalOptOuts) {
        this.totalOptOuts = totalOptOuts;
    }

    public void populateselectableTops() {
        this.selectableTops = new ArrayList<Integer>();
        this.selectableTops.add(3);
        this.selectableTops.add(5);
        this.selectableTops.add(10);
        this.selectableTops.add(15);
    }

    public List<Integer> getSelectableTops() {
        if (selectableTops == null) {
            populateselectableTops();
        }
        return selectableTops;
    }

    public void setSelectableTops(List<Integer> selectableTops) {
        this.selectableTops = selectableTops;
    }

    public int getTop() {

        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public List<ViewKeywordSummaryStats> getKeywordSummaryStatsList() {
        if (keywordSummaryStatsList == null) {
            this.keywordSummaryStatsList = new ArrayList<ViewKeywordSummaryStats>();
        }
        return keywordSummaryStatsList;
    }

    public void setKeywordSummaryStatsList(List<ViewKeywordSummaryStats> keywordSummaryStatsList) {
        this.keywordSummaryStatsList = keywordSummaryStatsList;
    }

    public String prepareKeywordRegistrationReport() {
        this.prepareVars();
        return "/geo-reports/KeywordRegistration?faces-redirect=true";
    }
}
