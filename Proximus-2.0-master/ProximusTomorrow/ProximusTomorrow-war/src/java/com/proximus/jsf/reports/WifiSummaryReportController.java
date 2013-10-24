/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.reports;

import com.proximus.data.*;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.report.ViewWifiSuccessfulPages;
import com.proximus.data.report.WifiVisits;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractReportController;
import com.proximus.manager.*;
import com.proximus.manager.report.WifiDaySummaryManagerLocal;
import com.proximus.util.ServerURISettings;
import com.proximus.util.TimeConstants;
import com.proximus.util.ZipUnzip;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "wifiSummaryReportController")
@SessionScoped
public class WifiSummaryReportController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(WifiSummaryReportController.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    @EJB
    WifiLogManagerLocal wifiMgr;
    @EJB
    WifiRegistrationManagerLocal wifiRegMgr;
    @EJB
    private WifiDaySummaryManagerLocal wifiDaySummaryMgr;
    //Model Data
    private List<Campaign> listCampaigns;
    private List<String> listDevices;
    private Long wifiRegistrationCount;
    private Long wifiSummaryMaxValue;
    private Long wifiRegMaxValue;
    private CartesianChartModel wifiChartModelLine;
    private CartesianChartModel wifiRegistrationsChart;
    private Long pageViews;
    private Long successfulPages;
    private List<ViewWifiSuccessfulPages> filteredSuccessfulPages;
    private Long uniqueMacs;
    private List<ViewWifiDaySummary> wifiDaySummaries;
    private List<WifiVisits> wifiVisits;
    private List<Map.Entry<String, Long>> visitsList;
    private CartesianChartModel frequencyChart;
    private long maxFrequency = 0;
    public static final String FREQ_ONE = "1";
    public static final String FREQ_TWO_FOUR = "2-4";
    public static final String FREQ_FIVE_NINE = "5-9";
    public static final String FREQ_TEN_PLUS = "10+";
    private ResourceBundle messages;

    public WifiSummaryReportController() {
        messages = this.getHttpSession().getMessages();
    }

    public void populateWifiSummary() {
        pageViews = wifiDaySummaryMgr.getPageViews(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
        successfulPages = wifiDaySummaryMgr.getSuccessfulPageViews(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
        uniqueMacs = wifiDaySummaryMgr.getUniqueUsers(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
        wifiDaySummaries = wifiDaySummaryMgr.getViewSummary(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
        //handleWifiVisits();
        //handleFrequencyChart();
    }

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }

    public Long getSuccessfulPages() {
        return successfulPages;
    }

    public void setSuccessfulPages(Long successfulPages) {
        this.successfulPages = successfulPages;
    }

    public Long getUniqueMacs() {
        return uniqueMacs;
    }

    public void setUniqueMacs(Long uniqueMacs) {
        this.uniqueMacs = uniqueMacs;
    }

//    private void handleFrequencyChart() {
//        maxFrequency = 0;
//        frequencyChart = new CartesianChartModel();
//        ChartSeries users = new ChartSeries();
//        users.setLabel(messages.getString("users"));
//        if (visitsList != null) {
//            for (Entry<String, Long> entry : visitsList) {
//                users.set(entry.getKey(), entry.getValue());
//                maxFrequency = (entry.getValue() > maxFrequency) ? entry.getValue() : maxFrequency;
//            }
//            frequencyChart.addSeries(users);
//
//            if (maxFrequency % 6 != 0) {
//                maxFrequency += (6 - (maxFrequency % 6));
//            }
//
//            if (maxFrequency < 12) {
//                maxFrequency = 12;
//            }
//        }
//    }
    public long getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(long maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public CartesianChartModel getFrequencyChart() {
        if (frequencyChart == null) {
            frequencyChart = new CartesianChartModel();
        }
        return frequencyChart;
    }

    public void setFrequencyChart(CartesianChartModel frequencyChart) {
        this.frequencyChart = frequencyChart;
    }

//    private void handleWifiVisits() {
//        wifiDaySummaries = viewWifiPageManager.getWifiDaySummary(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
//        wifiVisits = viewWifiVisitsManager.getWifiVisits(getCompanyFromSession(), selectedCampaign, selectedDevice, getStartDate(), getEndDate());
//        createVisitsList(wifiVisits);
//    }
//    private void createVisitsList(List<WifiVisits> wifiVisits) {
//        String key;
//        if (wifiVisits != null) {
//            Map<String, Long> map = new HashMap<String, Long>();
//            map.put(WifiSummaryReportController.FREQ_ONE, 0L);
//            map.put(WifiSummaryReportController.FREQ_TWO_FOUR, 0L);
//            map.put(WifiSummaryReportController.FREQ_FIVE_NINE, 0L);
//            map.put(WifiSummaryReportController.FREQ_TEN_PLUS, 0L);
//
//
//            for (WifiVisits wv : wifiVisits) {
//                if (wv.getCount() < 1) {
//                    continue;
//                }
//                if (wv.getCount() == 1) {
//                    key = WifiSummaryReportController.FREQ_ONE;
//                } else if (wv.getCount() > 1 && wv.getCount() < 5) {
//                    key = WifiSummaryReportController.FREQ_TWO_FOUR;
//                } else if (wv.getCount() > 4 && wv.getCount() < 10) {
//                    key = WifiSummaryReportController.FREQ_FIVE_NINE;
//                } else {
//                    key = WifiSummaryReportController.FREQ_TEN_PLUS;
//                }
//                //Adding it to the map
//                long val = map.get(key);
//                val++;
//                map.put(key, val);
//
//            }
//
//            //Sort Map before setting Array
//            visitsList = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
//            Collections.sort(visitsList, new Comparator<Map.Entry<String, Long>>() {
//
//                @Override
//                public int compare(Entry<String, Long> one, Entry<String, Long> two) {
//                    if (one.getKey().equals(WifiSummaryReportController.FREQ_ONE) || two.getKey().equals(WifiSummaryReportController.FREQ_TEN_PLUS)) {
//                        return -1;
//                    } else if (two.getKey().equals(WifiSummaryReportController.FREQ_ONE) || one.getKey().equals(WifiSummaryReportController.FREQ_TEN_PLUS)) {
//                        return 1;
//                    }
//                    if (one.getKey().equals(WifiSummaryReportController.FREQ_TWO_FOUR) && two.getKey().equals(WifiSummaryReportController.FREQ_FIVE_NINE)) {
//                        return -1;
//                    } else if (one.getKey().equals(WifiSummaryReportController.FREQ_TWO_FOUR) && two.getKey().equals(WifiSummaryReportController.FREQ_FIVE_NINE)) {
//                        return 1;
//
//                    } else {
//                        return 0;
//                    }
//                }
//            });
//        }
//    }
    public List<Entry<String, Long>> getVisitsList() {
        if (visitsList == null) {
            visitsList = new ArrayList<Entry<String, Long>>();
        }
        return visitsList;
    }

    public void setVisitsList(List<Entry<String, Long>> visitsList) {
        this.visitsList = visitsList;
    }

//    public Long calculateUserCount() {
//        if (uniqueMacs == null || uniqueMacs.isEmpty()) {
//            return 0L;
//        } else {
//            return uniqueMacs.size() + 0L;
//        }
//    }
//
//    public Long successfulPageViewsCount() {
//        if (successfulPages == null) {
//            return 0L;
//        } else {
//            long total = 0L;
//            for (ViewWifiSuccessfulPages v : successfulPages) {
//                total += v.getSuccessfulPageViews();
//            }
//            return total;
//        }
//    }
    public Float averageSuccessfulPageRequestsPerUser() {
        if (successfulPages == null) {
            return 0f;
        } else if (uniqueMacs == null) {
            return 0f;
        } else {
            if (uniqueMacs == 0L) {
                return 0f;
            } else {
                return ((float) getSuccessfulPages() / getUniqueMacs());
            }
        }
    }
//
//    public List<ViewWifiPageViews> getPageViews() {
//        if (pageViews == null) {
//            pageViews = new ArrayList<ViewWifiPageViews>();
//        }
//        return pageViews;
//    }
//
//    public void setPageViews(List<ViewWifiPageViews> pageViews) {
//        this.pageViews = pageViews;
//    }

//    public List<ViewWifiSuccessfulPages> getSuccessfulPages() {
//        if (successfulPages == null) {
//            successfulPages = new ArrayList<ViewWifiSuccessfulPages>();
//        }
//        return successfulPages;
//    }
//
//    public void setSuccessfulPages(List<ViewWifiSuccessfulPages> successfulPages) {
//        this.successfulPages = successfulPages;
//    }
    public List<ViewWifiSuccessfulPages> getFilteredSuccessfulPages() {
        return filteredSuccessfulPages;
    }

    public void setFilteredSuccessfulPages(List<ViewWifiSuccessfulPages> filteredSuccessfulPages) {
        this.filteredSuccessfulPages = filteredSuccessfulPages;
    }

//    public List<String> getUniqueMacs() {
//        if (uniqueMacs == null) {
//            uniqueMacs = new ArrayList<String>();
//        }
//        return uniqueMacs;
//    }
//
//    public void setUniqueMacs(List<String> uniqueMacs) {
//        this.uniqueMacs = uniqueMacs;
//    }
    public List<ViewWifiDaySummary> getWifiDaySummaries() {
        if (wifiDaySummaries == null) {
            wifiDaySummaries = new ArrayList<ViewWifiDaySummary>();
        }
        return wifiDaySummaries;
    }

    public void setWifiDaySummaries(List<ViewWifiDaySummary> wifiDaySummaries) {
        this.wifiDaySummaries = wifiDaySummaries;
    }

    public List<WifiVisits> getWifiVisits() {
        if (wifiVisits == null) {
            wifiVisits = new ArrayList<WifiVisits>();
        }
        return wifiVisits;
    }

    public void setWifiVisits(List<WifiVisits> wifiVisits) {
        this.wifiVisits = wifiVisits;
    }

    @Override
    public Device getSelectedDevice() {
        if (this.selectedDevice == null) {
            this.selectedDevice = new Device();
        }
        return selectedDevice;
    }

    @Override
    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    @Override
    public Company getCompany() {
        if (company == null) {
            company = companyMgr.find(this.getHttpSession().getCompany_id());
        }
        return company;
    }

    @Override
    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public Campaign getSelectedCampaign() {
        if (this.selectedCampaign == null) {
            this.selectedCampaign = new Campaign();
        }
        return selectedCampaign;
    }

    public String getSelectedCampaignName() {
        if (selectedCampaign != null) {
            return selectedCampaign.getName();
        } else {
            return messages.getString("allCampaigns");
        }
    }

    @Override
    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public List<String> getListDevices() {
        if (listDevices == null) {
            populateListDevices();
        }
        return listDevices;
    }

    public void setListDevices(List<String> listDevices) {
        this.listDevices = listDevices;
    }

    public void populateListCampaigns() {

        //campaignMap = new HashMap<String, Campaign>();
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(this.getCompanyFromSession());
        this.listCampaigns = new ArrayList<Campaign>();
        if (camps != null) {
            for (Campaign c : camps) {
                //campaignMap.put(c.getName(), c);
                listCampaigns.add(c);
            }
        }
    }

    public List<Campaign> getListCampaigns() {
        if (listCampaigns == null) {
            this.populateListCampaigns();
        }
        return listCampaigns;
    }

    public void setListCampaigns(List<Campaign> listRealCampaigns) {
        this.listCampaigns = listRealCampaigns;
    }

    public void populateListDevices() {
        List<Device> devices = deviceMgr.getDeviceByCompany(this.getCompanyFromSession());
        this.listDevices = new ArrayList<String>();
        if (devices != null) {
            for (Device d : devices) {
                listDevices.add(d.getName());
            }
        }
    }

    public Long getWifiSummaryMaxValue() {
        return wifiSummaryMaxValue;
    }

    public void setWifiSummaryMaxValue(Long wifiSummaryMaxValue) {
        this.wifiSummaryMaxValue = wifiSummaryMaxValue;
    }

    public String updateInterface() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        createWifiChartModelLine();
        createWifiRegistrationsChart();
        return null;
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    public Long getWifiRegistrationCount() {
        return wifiRegistrationCount;
    }

    public double getAvgWifiRegistrationCount() {
        Calendar start = new GregorianCalendar();
        start.setTime(startDate);
        Calendar end = new GregorianCalendar();
        end.setTime(endDate);
        long numberOfDays = DateUtil.daysBetween(start, end);
        if (numberOfDays > 0) {
            return (getWifiRegistrationCount() * 1.0 / numberOfDays);
        } else {
            return 0.0;
        }
    }

    public String getAvgWifiRegistrationCountStr() {
        NumberFormat fmt = new DecimalFormat("#.##");
        return fmt.format(getAvgWifiRegistrationCount());
    }

    public void setWifiRegistrationCount(Long wifiRegistrationCount) {
        this.wifiRegistrationCount = wifiRegistrationCount;
    }

    public void prepareVars() {
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = this.getEndDate();
        selectedCampaign = null;
        selectedDevice = null;
        company = companyMgr.find(this.getHttpSession().getCompany_id());
        listCampaigns = null;
        listDevices = null;
        campaignMap = null;
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
    }

    public String prepareList() {
        prepareVars();
        createWifiChartModelLine();
        createWifiRegistrationsChart();
        return "/reports/WifiSummaryReport?faces-redirect=true";
    }

    public CartesianChartModel getWifiRegistrationsChart() {
        if (wifiRegistrationsChart == null) {
            createWifiRegistrationsChart();
        }
        return wifiRegistrationsChart;
    }

   
    private void createWifiRegistrationsChart() {
        wifiRegistrationsChart = new CartesianChartModel();
        wifiRegistrationCount = 0L;
        wifiRegMaxValue = 0L;
        ChartSeries totalRegistrations = new ChartSeries();
        totalRegistrations.setLabel(messages.getString("totalRegistrations"));

        //LinkedHashMap<Object, Number> totalRegistrationsMap = new LinkedHashMap<Object, Number>();
        
        
        int days = Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
        int interval = days;
        if (days > 10) {
            interval = 10;
        } else if(days == 0) {
            interval = 1;
        }
        
        boolean startingPoint = true;
        Long registrationRunningTotal = 0L;
        Long wifiRegistrationStartingPoint = 0L;

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.getStartOfDay(startDate));
        while (c.getTime().before(DateUtil.getEndOfDay(endDate))) {
            String dateStr = DateUtil.formatDateForChart(c.getTime());
            
            registrationRunningTotal = wifiRegMgr.getRegistrationCount(null, c.getTime(), company, selectedCampaign, selectedDevice);
            if(startingPoint) {
                wifiRegistrationStartingPoint = registrationRunningTotal;
                startingPoint = false;
            }
            wifiRegMaxValue = registrationRunningTotal;
            totalRegistrations.set(dateStr, registrationRunningTotal);
            if (interval == 0) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                c.add(Calendar.DAY_OF_MONTH, days / interval);
            }
        }
        
        wifiRegistrationCount = wifiRegMaxValue - wifiRegistrationStartingPoint;

//        List<WifiRegistration> registrations = wifiRegMgr.getWifiRegistrationInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
//        if (registrations != null) {
//            for (WifiRegistration registration : registrations) {
//                wifiRegistrationCount++;
//                wifiRegMaxValue++;
//                String dateStr = DateUtil.formatDateForChart(registration.getEventDate());
//                totalRegistrationsMap.put(dateStr, wifiRegistrationCount);
//            }
//        } else {
//            totalRegistrationsMap.put(startDate, 0L);
//            wifiRegistrationCount = 0L;
//        }
//        totalRegistrations.setData(totalRegistrationsMap);
        wifiRegistrationsChart.addSeries(totalRegistrations);

        if (wifiRegMaxValue % 6 != 0) {
            wifiRegMaxValue += (6 - (wifiRegMaxValue % 6));
        } else {
            wifiRegMaxValue += 6;
        }
        if (wifiRegMaxValue < 12) {
            wifiRegMaxValue = 12L;
        }
    }

    public Long getWifiRegMaxValue() {
        return wifiRegMaxValue;
    }

    public void setWifiRegMaxValue(Long wifiRegMaxValue) {
        this.wifiRegMaxValue = wifiRegMaxValue;
    }

    private void createWifiChartModelLine() {
        wifiChartModelLine = new CartesianChartModel();
        ChartSeries totalPageViews = new ChartSeries();
        totalPageViews.setLabel(messages.getString("totalPageViews"));

        ChartSeries pageViewForDay = new ChartSeries();
        pageViewForDay.setLabel(messages.getString("dailyPageViews"));

        LinkedHashMap<Object, Number> dailyPageViewMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalPageViewMap = new LinkedHashMap<Object, Number>();


        //Changing to warehouse strategy
        populateWifiSummary();

        if (wifiDaySummaries == null) {
            dailyPageViewMap.put(DateUtil.formatDateForChart(new Date()), 0L);
            totalPageViewMap.put(DateUtil.formatDateForChart(new Date()), 0L);
        } else {
            for (ViewWifiDaySummary summary : wifiDaySummaries) {
                String dateStr = DateUtil.formatDateForChart(summary.getEventDate());

                // because we track by device, there are multiple summaries per day, so we add them up for the daily total
                if (dailyPageViewMap.containsKey(dateStr)) {
                    long newValue = (Long) dailyPageViewMap.get(dateStr) + summary.getPageViews();
                    dailyPageViewMap.put(dateStr, newValue);
                } else {
                    dailyPageViewMap.put(dateStr, summary.getPageViews());
                }
            }

        }

        // now it's summed by day, so ass them up for each day for the total overall
        long runningTotal = 0;
        for (Map.Entry<Object, Number> entry : dailyPageViewMap.entrySet()) {
            Object dateStr = entry.getKey();
            Number number = entry.getValue();
            runningTotal += number.longValue();

            totalPageViewMap.put(dateStr, runningTotal);
        }

        totalPageViews.setData(totalPageViewMap);
        pageViewForDay.setData(dailyPageViewMap);


        wifiChartModelLine.addSeries(totalPageViews);
        wifiChartModelLine.addSeries(pageViewForDay);
    }

    public Long getWifiChartLineMax() {
        long maxValue = 0L;
        if (wifiDaySummaries == null) {
            return maxValue;
        }
        for (ViewWifiDaySummary summary : wifiDaySummaries) {
            maxValue += summary.getPageViews();
        }

        // add 10% for a nice clean UI
        return Math.round(maxValue * 1.1);
    }

    public Long getWifiRegistrationLineMax() {
        long maxValue = wifiRegistrationCount;
        // add 10% for a nice clean UI
        return Math.round(maxValue * 1.1);
    }

    public CartesianChartModel getWifiChartModelLine() {
        if (wifiChartModelLine == null) {
            createWifiChartModelLine();
        }
        return wifiChartModelLine;
    }

    private File exportWifiRegistrationFile() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<WifiRegistration> logs = wifiRegMgr.getWifiRegistrationInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
        new File(ServerURISettings.SERVER_TMP).mkdirs();
        File result = new File(ServerURISettings.SERVER_TMP + ServerURISettings.OS_SEP + System.currentTimeMillis() + ".csv");
        try {
            if (logs != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(result));
                writer.write(WifiRegistration.getCVSHeader());
                writer.newLine();
                for (WifiRegistration l : logs) {
                    writer.write(l.toCVS());
                    writer.newLine();
                }
                writer.close();
                return result;
            } else {

                return null;
            }
        } catch (IOException ex) {
            logger.fatal(ex);
            return null;
        }
    }

    private File exportWifiLogFile() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<WifiLog> logs = wifiMgr.getWifiLogInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
        new File(ServerURISettings.SERVER_TMP).mkdirs();
        File result = new File(ServerURISettings.SERVER_TMP + ServerURISettings.OS_SEP + System.currentTimeMillis() + ".csv");
        try {
            if (logs != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(result));
                writer.write(WifiLog.getCVSHeader());
                writer.newLine();
                for (WifiLog l : logs) {
                    writer.write(l.toCVS());
                    writer.newLine();
                }
                writer.close();
                return result;
            } else {

                return null;
            }
        } catch (IOException ex) {
            logger.fatal(ex);
            return null;
        }
    }

    private String getFileName(String type, Date startDate, Date endDate) {
        return "downloaded_" + type + "RawData_" + TimeConstants.formatForLogFileName(startDate) + "-" + TimeConstants.formatForLogFileName(endDate) + ".csv";
    }

    private String getDownloadedFileName(String type, Date startDate, Date endDate) {
        return "downloaded_" + type + "RawData_" + TimeConstants.formatForLogFileName(startDate) + "-" + TimeConstants.formatForLogFileName(endDate) + ".zip";
    }

    public StreamedContent getWifiLogFile() {
        try {
            File result = exportWifiLogFile();
            if (result != null) {
                File zippedResult = ZipUnzip.GetZippedFile(result, getFileName("WifiLog", startDate, endDate));
                InputStream zipStream = new FileInputStream(zippedResult);
                return new DefaultStreamedContent(zipStream, "application/zip", getDownloadedFileName("WifiLog", startDate, endDate));
            } else {
                String start = getFormattedStartDate();
                String end = getFormattedEndDate();
                String str = "NO DATA ON RANGE: " + start + " - " + end;
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", getFileName("NoWifiLog", startDate, endDate));
            }
        } catch (Exception e) {
            logger.error("Exception occurred in zipping or streaming log file." + e);
            return null;
        }
    }

    public StreamedContent getWifiRegistrationFile() {
        try {
            File result = exportWifiRegistrationFile();
            if (result != null) {
                InputStream stream = new FileInputStream(result);
                return new DefaultStreamedContent(stream, "text/csv", getFileName("WifiRegistration", startDate, endDate));
            } else {
                String start = new SimpleDateFormat("MMM, dd yyyy").format(startDate);
                String end = new SimpleDateFormat("MMM, dd yyyy").format(endDate);
                String str = "NO DATA ON RANGE: " + start + " - " + end;
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", getFileName("NoWifiRegistration", startDate, endDate));
            }
        } catch (Exception e) {
            return null;
        }
    }
}
