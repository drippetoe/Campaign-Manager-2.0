/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Company;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Country;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.sms.report.PropertyStatsAgreggator;
import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.data.sms.report.ViewOptInsByMonth;
import com.proximus.data.sms.report.ViewPropertySummaryStats;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.LoginController;
import com.proximus.jsf.datamodel.ViewPropertyStatisticsDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.CountryManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.report.*;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "propertyStatisticsController")
@SessionScoped
public class PropertyStatisticsController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(PropertyStatisticsController.class.getName());
    private static final long serialVersionUID = 1;
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private PropertyManagerLocal propertyMgr;
    @EJB
    private ViewPropertySummaryStatsManagerLocal statsMgr;
    @EJB
    private MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    @EJB
    private ViewActiveOffersManagerLocal activeOffersMgr;
    @EJB
    private ViewActiveUsersManagerLocal activeUsersMgr;
    @EJB
    private ViewTotalMessagesSentManagerLocal totalMessagesSentMgr;
    @EJB
    private ViewMessagesSentByMonthManagerLocal messagesByMonthMgr;
    @EJB
    private ViewOptInsByMonthManagerLocal optsInsByMonthMgr;
    @EJB
    private CountryManagerLocal countryMgr;
    private ViewPropertyStatisticsDataModel propertyStatsDataModel;
    private List<PropertyStatsAgreggator> filteredAgreggators;
    private PropertyStatsAgreggator selectedStat;
    private List<ViewActiveOffers> activeOffers;
    private List<ViewActiveOffers> filteredActiveOffers;
    private int activeTab;
    //Category Chart
    private CartesianChartModel categoryChart;
    private long maxChartValue = 0;
    private ArrayList<Map.Entry<String, Long>> categoryList;
    //Total Offers Sent
    private CartesianChartModel offersChart;
    private long maxChartValueOffers = 0;
    private ResourceBundle bundle;
    //  private ResourceBundle messages;
    private final String NO_CATEGORY = " N/A";
    private long totalMessagesSent = 0;
    private long totalCurrMonthMessagesSent = 0;
    private long totalPrevMonthMessagesSent = 0;
    private long totalRetailers = 0;
    private long totalActiveOffers = 0;
    private long totalRetailersWithOffers = 0;
    private long totalOptIns = 0;
    private long totalMessagesSentInProperty = 0;
    private long totalCurrMonthMessagesSentInProperty = 0;
    private long totalPrevMonthMessagesSentInProperty = 0;
    private long totalCurrMonthOptIns = 0;
    private long totalPrevMonthOptIns = 0;
    private ResourceBundle messages;
    private boolean showContent = false;
    private Country selectedCountry;
    private List<String> countryList;

    public PropertyStatisticsController() {
        bundle = this.getHttpSession().getBundle();
        messages = this.getHttpSession().getMessages();
    }

    public void prepareVars() {
        company = null;
        selectedProperty = null;
        selectedDMA = null;
        selectedRetailer = null;
        selectedCountry = null;
        startDate = DateUtil.getFirstDayOfMonth(new Date());
        endDate = new Date();
        propertyStatsDataModel = null;
        totalMessagesSent = 0;
        totalCurrMonthMessagesSent = 0;
        totalPrevMonthMessagesSent = 0;
        totalRetailers = 0;
        totalActiveOffers = 0;
        totalRetailersWithOffers = 0;
        totalOptIns = 0;
        totalMessagesSentInProperty = 0;
        totalCurrMonthMessagesSentInProperty = 0;
        totalPrevMonthMessagesSentInProperty = 0;
        totalCurrMonthOptIns = 0;
        totalPrevMonthOptIns = 0;
        showContent = false;

        activeTab = 0;
    }

    public String prepareList() {
        prepareVars();
        return "/geo-reports/PropertyStatistics?faces-redirect=true";
    }

    public String prepareMessagesSentAndActiveUsers() {
        prepareVars();
        updateInterface();
        return "/geo-reports/MessagesSentAndActiveUsers?faces-redirect=true";

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

    public Country getSelectedCountry() {
        if (selectedCountry == null) {
            selectedCountry = new Country();
        }
        return selectedCountry;
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public List<String> getCountryList() {
        if (countryList == null) {
            List<Country> list = countryMgr.findAllSortedCountriesByProperty();
            countryList = new ArrayList<String>();
            for (Country c : list) {
                countryList.add(c.getName());
            }
        }
        return countryList;
    }

    public void setCountryList(List<String> countryList) {
        this.countryList = countryList;
    }

    public ViewPropertyStatisticsDataModel getPropertyStatsDataModel() {
        if (propertyStatsDataModel == null) {
            propertyStatsDataModel = new ViewPropertyStatisticsDataModel();
        }
        return propertyStatsDataModel;
    }

    public void setPropertyStatsDataModel(ViewPropertyStatisticsDataModel propertyStatsDataModel) {
        this.propertyStatsDataModel = propertyStatsDataModel;
    }

    public List<PropertyStatsAgreggator> getFilteredAgreggators() {
        return filteredAgreggators;
    }

    public void setFilteredAgreggators(List<PropertyStatsAgreggator> filteredAgreggators) {
        this.filteredAgreggators = filteredAgreggators;
    }

    public void updateInterface() {
        handleOfferChart();
    }

    public void populateSummaryStats() {

//        //NOT POPULATING SINCE IS ALREADY DONE, must use prepareVars() in order to re-populate
//        if (this.propertyStatsDataModel != null && this.propertyStatsDataModel.getViewPropertyData() != null && !this.propertyStatsDataModel.getViewPropertyData().isEmpty()) {
//            return;
//        }

        Country realCountry = null;
        if (getSelectedCountry().getName() != null) {
            realCountry = countryMgr.findByName(getSelectedCountry().getName());
        }
        LoginController lc = this.getHttpSession();
        List<ViewPropertySummaryStats> propStats;
        if (lc.getPrivileges().contains(bundle.getString("BrandAccess"))) {
            propStats = statsMgr.getByBrandAndCountry(this.getBrandFromSession(), realCountry);
        } else if (lc.getPrivileges().contains(bundle.getString("CompanyMobileOffer"))) {
            propStats = statsMgr.getByCompanyAndCountry(this.getCompanyFromSession(), realCountry);
        } else {
            List<Property> propertyList = new ArrayList<Property>();
            List<Property> props = lc.getCurrUser().getProperties();
            if (props != null && !props.isEmpty()) {
                if (realCountry == null) {
                    propStats = statsMgr.getByProperties(lc.getCurrUser().getProperties());
                } else {
                    for (Property property : props) {
                        if (property.getCountry().equals(realCountry)) {
                            propertyList.add(property);
                        }
                    }
                    if (!propertyList.isEmpty()) {
                        propStats = statsMgr.getByProperties(propertyList);
                    } else {
                        this.propertyStatsDataModel = null;
                        return;
                    }
                }
            } else {
                this.propertyStatsDataModel = null;
                return;
            }
        }

        List<PropertyStatsAgreggator> statsAggregator = new ArrayList<PropertyStatsAgreggator>();

        //Setting up the Timings for Current and Previous months
        DateTime currDate = new DateTime();
        int currMonth = currDate.getMonthOfYear();
        int prevMonth = currDate.minusMonths(1).getMonthOfYear();
        int currYear = currDate.getYear();
        int yearOfPreviousMonth = (prevMonth == 12) ? (currYear - 1) : currYear;
        Map<Property, ViewOptInsByMonth> currMonthMap = new HashMap<Property, ViewOptInsByMonth>();

        List<ViewOptInsByMonth> currMonthOptIns = optsInsByMonthMgr.getOptInsInYearAndMonth(this.getCompanyFromSession(), currYear, currMonth);

        if (currMonthOptIns == null) {
            currMonthOptIns = new ArrayList<ViewOptInsByMonth>();
        }

        for (ViewOptInsByMonth viewOptInsByMonth : currMonthOptIns) {
            currMonthMap.put(viewOptInsByMonth.getProperty(), viewOptInsByMonth);
        }

        Map<Property, ViewOptInsByMonth> prevMonthMap = new HashMap<Property, ViewOptInsByMonth>();
        List<ViewOptInsByMonth> prevMonthOptIns = optsInsByMonthMgr.getOptInsInYearAndMonth(this.getCompanyFromSession(), yearOfPreviousMonth, prevMonth);
        if (prevMonthOptIns == null) {
            prevMonthOptIns = new ArrayList<ViewOptInsByMonth>();
        }
        for (ViewOptInsByMonth viewOptInsByMonth : prevMonthOptIns) {
            prevMonthMap.put(viewOptInsByMonth.getProperty(), viewOptInsByMonth);
        }


        for (ViewPropertySummaryStats vps : propStats) {
            PropertyStatsAgreggator agg = new PropertyStatsAgreggator();
            vps.setProperty(propertyMgr.find(vps.getPropertyId()));
            agg.setPropStats(vps);

            agg.setMessagesSentCurrMonth(messagesByMonthMgr.getSentInYearAndMonthByProperty(vps.getProperty().getCompany(), currYear, currMonth, vps.getProperty()));
            agg.setMessagesSentPrevMonth(messagesByMonthMgr.getSentInYearAndMonthByProperty(vps.getProperty().getCompany(), yearOfPreviousMonth, prevMonth, vps.getProperty()));

            ViewOptInsByMonth currOpt = currMonthMap.get(vps.getProperty());
            ViewOptInsByMonth prevOpt = prevMonthMap.get(vps.getProperty());
            agg.setOptInsCurrMonth((currOpt != null) ? currOpt.getOptIns() : 0L);
            agg.setOptInsPrevMonth((prevOpt != null) ? prevOpt.getOptIns() : 0L);

            statsAggregator.add(agg);
        }

        this.propertyStatsDataModel = new ViewPropertyStatisticsDataModel(statsAggregator);

        filteredAgreggators = new ArrayList<PropertyStatsAgreggator>(statsAggregator);

        totalMessagesSent = 0;
        totalRetailers = 0;
        totalActiveOffers = 0;
        totalRetailersWithOffers = 0;
        totalOptIns = 0;

        totalCurrMonthMessagesSent = 0;
        totalPrevMonthMessagesSent = 0;

        totalCurrMonthOptIns = 0;
        totalPrevMonthOptIns = 0;

        for (PropertyStatsAgreggator v : this.propertyStatsDataModel.getViewPropertyData()) {

            totalMessagesSent += v.getPropStats().getMessagesSent();
            totalCurrMonthMessagesSent += v.getMessagesSentCurrMonth();
            totalPrevMonthMessagesSent += v.getMessagesSentPrevMonth();
            totalCurrMonthOptIns += v.getOptInsCurrMonth();
            totalPrevMonthOptIns += v.getOptInsPrevMonth();
            totalRetailers += v.getPropStats().getRetailers();
            totalActiveOffers += v.getPropStats().getActiveOffers();
            totalRetailersWithOffers += v.getPropStats().getRetailersWithOffers();
            totalOptIns += v.getPropStats().getOptIns();

        }
        showContent = true;
        this.activeTab = 0;
    }

    public long getTotalMessagesSent() {
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(long totalMessagesSent) {
        this.totalMessagesSent = totalMessagesSent;
    }

    public long getTotalActiveOffers() {
        return totalActiveOffers;
    }

    public void setTotalActiveOffers(long totalActiveOffers) {
        this.totalActiveOffers = totalActiveOffers;
    }

    public long getTotalRetailersWithOffers() {
        return totalRetailersWithOffers;
    }

    public void setTotalRetailersWithOffers(long totalRetailersWithOffers) {
        this.totalRetailersWithOffers = totalRetailersWithOffers;
    }

    public long getTotalOptIns() {
        return totalOptIns;
    }

    public void setTotalOptIns(long totalOptIns) {
        this.totalOptIns = totalOptIns;
    }

    public long getTotalRetailers() {
        return totalRetailers;
    }

    public void setTotalRetailers(long totalRetailers) {
        this.totalRetailers = totalRetailers;
    }

    public PropertyStatsAgreggator getSelectedStat() {
        return selectedStat;
    }

    public void setSelectedStat(PropertyStatsAgreggator selectedStat) {
        this.selectedStat = selectedStat;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public void onPropertyRowSelect(SelectEvent event) {
        PropertyStatsAgreggator ev = (PropertyStatsAgreggator) event.getObject();
        totalMessagesSentInProperty = 0;
        totalPrevMonthMessagesSentInProperty = 0;
        totalCurrMonthMessagesSentInProperty = 0;


        if (ev != null) {
            selectedStat = ev;
            activeOffers = activeOffersMgr.getByProperty(ev.getProperty());


            //Setting up the Timings for Current and Previous months
            DateTime currDate = new DateTime();
            DateTime prevMonthDate = currDate.minusMonths(1);


            Date startCurrMonth = DateUtil.getFirstDayOfMonth(currDate.toDate());
            Date endCurrMonth = DateUtil.getLastDayOfMonth(currDate.toDate());

            Date startPrevMonth = DateUtil.getFirstDayOfMonth(prevMonthDate.toDate());
            Date endPrevMonth = DateUtil.getLastDayOfMonth(prevMonthDate.toDate());


            long messagesCount = 0;
            long currMonthCount = 0;
            long prevMonthCount = 0;

            if (activeOffers != null) {
                filteredActiveOffers = new ArrayList<ViewActiveOffers>(activeOffers);
                for (ViewActiveOffers ao : activeOffers) {
                    messagesCount = mobileOfferSendLogMgr.countMessagesSentByOffer(ao.getWebOffer().getMobileOffer(), null, null, ev.getProperty());
                    currMonthCount = mobileOfferSendLogMgr.countMessagesSentByOffer(ao.getWebOffer().getMobileOffer(), startCurrMonth, endCurrMonth, ev.getProperty());
                    prevMonthCount = mobileOfferSendLogMgr.countMessagesSentByOffer(ao.getWebOffer().getMobileOffer(), startPrevMonth, endPrevMonth, ev.getProperty());

                    totalMessagesSentInProperty += messagesCount;
                    totalCurrMonthMessagesSentInProperty += currMonthCount;
                    totalPrevMonthMessagesSentInProperty += prevMonthCount;

                    ao.setMessagesSent(messagesCount);
                    ao.setCurrMonthMessagesSent(currMonthCount);
                    ao.setPrevMonthMessagesSent(prevMonthCount);
                }
            }
            handleCategoryChart();
            this.activeTab = 1;
        }
    }

    public void onTabChange(TabChangeEvent event) {
        TabView tv = (TabView) event.getComponent();
        this.activeTab = tv.getActiveIndex();
    }

    public List<ViewActiveOffers> getActiveOffers() {
        if (activeOffers == null) {
            activeOffers = new ArrayList<ViewActiveOffers>();
        }
        return activeOffers;
    }

    public void setActiveOffers(List<ViewActiveOffers> activeOffers) {
        this.activeOffers = activeOffers;
    }

    public List<ViewActiveOffers> getFilteredActiveOffers() {
        return filteredActiveOffers;
    }

    public void setFilteredActiveOffers(List<ViewActiveOffers> filteredActiveOffers) {
        this.filteredActiveOffers = filteredActiveOffers;
    }

    private void createCategoryMap(List<ViewActiveOffers> activeOffers) {

        if (activeOffers != null) {
            Map<String, Long> map = new HashMap<String, Long>();
            //init the No Category 
            map.put(NO_CATEGORY, 0L);
            for (ViewActiveOffers ao : activeOffers) {
                if (ao.getWebOffer().getCategories() != null && !ao.getWebOffer().getCategories().isEmpty()) {
                    for (Category c : ao.getWebOffer().getCategories()) {
                        if (map.containsKey(c.getName())) {
                            map.put(c.getName(), map.get(c.getName()) + 1);
                        } else {
                            map.put(c.getName(), 1L);
                        }
                    }
                } else {
                    map.put(NO_CATEGORY, map.get(NO_CATEGORY) + 1);
                }
            }
            categoryList = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
            Collections.sort(categoryList, new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

        }

    }

    private void handleCategoryChart() {
        maxChartValue = 0;
        categoryChart = new CartesianChartModel();
        ChartSeries categories = new ChartSeries();
        categories.setLabel(messages.getString("activeOffersByCategory"));
        if (activeOffers != null) {
            createCategoryMap(activeOffers);

            for (Map.Entry<String, Long> entry : categoryList) {
                categories.set(entry.getKey(), entry.getValue());
                maxChartValue = (entry.getValue() > maxChartValue) ? entry.getValue() : maxChartValue;
            }
            categoryChart.addSeries(categories);

            if (maxChartValue % 6 != 0) {
                maxChartValue += (6 - (maxChartValue % 6));
            } else {
                maxChartValue += 6;
            }

            if (maxChartValue < 12) {
                maxChartValue = 12;
            }
        }
    }

    private void handleOfferChart() {
        maxChartValueOffers = 0;
        offersChart = new CartesianChartModel();
        ChartSeries messagesSent = new ChartSeries();

        messagesSent.setLabel(messages.getString("totalMessagesSent"));
        ChartSeries activeUsers = new ChartSeries();
        activeUsers.setLabel(messages.getString("activeUsers"));

        Long messageRunningTotal = 0L;
        Long activeUsersRunningTotal = 0L;
        int days = Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
        int interval = days;
        if (days > 10) {
            interval = 10;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.getStartOfDay(startDate));
        while (c.getTime().before(DateUtil.getEndOfDay(endDate))) {
            String dateStr = DateUtil.formatDateForChart(c.getTime());
            messageRunningTotal = totalMessagesSentMgr.getTotalMessagesSentBefore(this.getCompanyFromSession(), c.getTime());
            activeUsersRunningTotal = activeUsersMgr.getCountActiveUsers(this.getBrandFromSession(), c.getTime());
            long maxVal = (messageRunningTotal > activeUsersRunningTotal ? messageRunningTotal : activeUsersRunningTotal);

            if (maxChartValueOffers < maxVal) {
                maxChartValueOffers = maxVal;
            }
            messagesSent.set(dateStr, messageRunningTotal);
            activeUsers.set(dateStr, activeUsersRunningTotal);
            if (interval == 0) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                c.add(Calendar.DAY_OF_MONTH, days / interval);
            }
        }

        offersChart.addSeries(messagesSent);
        offersChart.addSeries(activeUsers);

        if (maxChartValueOffers % 6 != 0) {
            maxChartValueOffers += (6 - (maxChartValueOffers % 6));
        } else {
            maxChartValueOffers += 6;
        }
        if (maxChartValueOffers
                < 12) {
            maxChartValueOffers = 12;
        }
    }

    public CartesianChartModel getCategoryChart() {
        if (categoryChart == null) {
            categoryChart = new CartesianChartModel();
        }
        return categoryChart;
    }

    public void setCategoryChart(CartesianChartModel categoryChart) {
        this.categoryChart = categoryChart;
    }

    public CartesianChartModel getOffersChart() {
        if (offersChart == null) {
            offersChart = new CartesianChartModel();
        }
        return offersChart;
    }

    public void setOffersChart(CartesianChartModel offersChart) {
        this.offersChart = offersChart;
    }

    public long getMaxChartValueOffers() {
        return maxChartValueOffers;
    }

    public void setMaxChartValueOffers(long maxChartValueOffers) {
        this.maxChartValueOffers = maxChartValueOffers;
    }

    @Override
    public String getSeriesColors() {
        return "aa0000,0000aa,767676,00aa7a,000000";
    }

    public ArrayList<Entry<String, Long>> getCategoryList() {
        if (categoryList == null) {
            categoryList = new ArrayList<Entry<String, Long>>();
        }
        return categoryList;
    }

    public void setCategoryList(ArrayList<Entry<String, Long>> categoryList) {
        this.categoryList = categoryList;
    }

    public long getMaxChartValue() {
        return maxChartValue;
    }

    public void setMaxChartValue(long maxChartValue) {
        this.maxChartValue = maxChartValue;
    }

    public long getTotalMessagesSentInProperty() {
        return totalMessagesSentInProperty;
    }

    public void setTotalMessagesSentInProperty(long totalMessagesSentInProperty) {
        this.totalMessagesSentInProperty = totalMessagesSentInProperty;
    }

    public long getTotalCurrMonthMessagesSent() {
        return totalCurrMonthMessagesSent;
    }

    public void setTotalCurrMonthMessagesSent(long totalCurrMonthMessagesSent) {
        this.totalCurrMonthMessagesSent = totalCurrMonthMessagesSent;
    }

    public long getTotalPrevMonthMessagesSent() {
        return totalPrevMonthMessagesSent;
    }

    public void setTotalPrevMonthMessagesSent(long totalPrevMonthMessagesSent) {
        this.totalPrevMonthMessagesSent = totalPrevMonthMessagesSent;
    }

    public long getTotalCurrMonthOptIns() {
        return totalCurrMonthOptIns;
    }

    public void setTotalCurrMonthOptIns(long totalCurrMonthOptIns) {
        this.totalCurrMonthOptIns = totalCurrMonthOptIns;
    }

    public long getTotalPrevMonthOptIns() {
        return totalPrevMonthOptIns;
    }

    public void setTotalPrevMonthOptIns(long totalPrevMonthOptIns) {
        this.totalPrevMonthOptIns = totalPrevMonthOptIns;
    }

    public long getTotalCurrMonthMessagesSentInProperty() {
        return totalCurrMonthMessagesSentInProperty;
    }

    public void setTotalCurrMonthMessagesSentInProperty(long totalCurrMonthMessagesSentInProperty) {
        this.totalCurrMonthMessagesSentInProperty = totalCurrMonthMessagesSentInProperty;
    }

    public long getTotalPrevMonthMessagesSentInProperty() {
        return totalPrevMonthMessagesSentInProperty;
    }

    public void setTotalPrevMonthMessagesSentInProperty(long totalPrevMonthMessagesSentInProperty) {
        this.totalPrevMonthMessagesSentInProperty = totalPrevMonthMessagesSentInProperty;
    }

    public boolean isShowContent() {
        return showContent;
    }

    public void setShowContent(boolean showContent) {
        this.showContent = showContent;
    }
}
