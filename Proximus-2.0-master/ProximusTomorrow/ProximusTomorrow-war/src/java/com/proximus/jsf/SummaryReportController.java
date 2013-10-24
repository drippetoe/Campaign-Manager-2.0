package com.proximus.jsf;

import com.proximus.data.*;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.*;
import com.proximus.util.ServerURISettings;
import com.proximus.util.TimeConstants;
import com.proximus.util.ZipUnzip;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author dshaw
 */
@ManagedBean(name = "summaryReportController")
@SessionScoped
public class SummaryReportController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(SummaryReportController.class.getName());
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
    BluetoothReportManagerLocal btMgr;
    @EJB
    private WifiLogManagerLocal wifiLogFacade;
    @EJB
    private WifiRegistrationManagerLocal wifiRegistrationFacade;
    @EJB
    private BluetoothReportManagerLocal bluetoothFacade;
    private List<Campaign> listCampaigns;
    private List<String> listDevices;
    private WifiDaySummary wifiDaySummary;
    private List<WifiDaySummary> wifiDaySummaries;
    private BluetoothDaySummary bluetoothDaySummary;
    private List<BluetoothDaySummary> bluetoothDaySummaries;
    private Long wifiRegistrationCount;
    private Long wifiSummaryMaxValue;
    private CartesianChartModel wifiChartModelLine;
    private CartesianChartModel btChartModelLine;
    private CartesianChartModel wifiRegistrationsModelLine;

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
            return "All Campaigns";
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
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(companyMgr.find(this.getHttpSession().getCompany_id()));
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
        List<Device> devices = deviceMgr.getDeviceByCompany(companyMgr.find(this.getHttpSession().getCompany_id()));
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
        updateWifiDaySummary();
        updateBluetoothDaySummary();
        createWifiChartModelLine();
        createBluetoothChartModelLine();
        createWifiRegistrationsModelLine();
        return null;
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    /**
     *
     * @return a summary WifiDaySummary for the date range and campaign
     * specified
     */
    public void updateWifiDaySummary() {
        company = companyMgr.getCompanybyId(getCompanyIdFromSession());
        WifiDaySummary fullSummary = new WifiDaySummary();
        fullSummary.setCompany(company);
        if (selectedCampaign != null && selectedCampaign.getName() != null) {
            fullSummary.setCampaign(selectedCampaign);
        }
        if (selectedDevice != null && selectedDevice.getName() != null) {
            fullSummary.setDevice(selectedDevice);
        }
        wifiDaySummaries = wifiMgr.fetchWifiDaySummaries(startDate, endDate, company, selectedCampaign, selectedDevice);
        for (WifiDaySummary summary : wifiDaySummaries) {
            fullSummary.setTotalRequests(fullSummary.getTotalRequests() + summary.getTotalRequests());
            fullSummary.setTotalPageViews(fullSummary.getTotalPageViews() + summary.getTotalPageViews());
            fullSummary.setSuccessfulPageViews(fullSummary.getSuccessfulPageViews() + summary.getSuccessfulPageViews());
            fullSummary.setUniqueUserCount(fullSummary.getUniqueUserCount() + summary.getUniqueUserCount());
        }
        wifiDaySummary = fullSummary;
    }

    public void updateBluetoothDaySummary() {
        company = companyMgr.getCompanybyId(getCompanyIdFromSession());

        BluetoothDaySummary fullSummary = new BluetoothDaySummary();
        fullSummary.setCompany(company);

        if (selectedCampaign != null) {
            fullSummary.setCampaign(selectedCampaign);
        }

        if (selectedDevice != null) {
            fullSummary.setDevice(selectedDevice);
        }


        bluetoothDaySummaries = btMgr.fetchBluetoothDaySummaries(startDate, endDate, company, selectedCampaign, selectedDevice);
        for (BluetoothDaySummary summary : bluetoothDaySummaries) {
            fullSummary.setTotalDevicesSeen(fullSummary.getTotalDevicesSeen() + summary.getTotalDevicesSeen());
            fullSummary.setUniqueDevicesSeen(fullSummary.getUniqueDevicesSeen() + summary.getUniqueDevicesSeen());
            fullSummary.setUniqueDevicesSupportingBluetooth(fullSummary.getUniqueDevicesSupportingBluetooth() + summary.getUniqueDevicesSupportingBluetooth());
            fullSummary.setUniqueDevicesAcceptingPush(fullSummary.getUniqueDevicesAcceptingPush() + summary.getUniqueDevicesAcceptingPush());
            fullSummary.setUniqueDevicesDownloadingContent(fullSummary.getUniqueDevicesDownloadingContent() + summary.getUniqueDevicesDownloadingContent());
            fullSummary.setTotalContentDownloads(fullSummary.getTotalContentDownloads() + summary.getTotalContentDownloads());
        }
        bluetoothDaySummary = fullSummary;
    }

    public BluetoothDaySummary getBluetoothDaySummary() {
        if (bluetoothDaySummary == null) {
            updateBluetoothDaySummary();
        }
        return bluetoothDaySummary;
    }

    public void setBluetoothDaySummary(BluetoothDaySummary bluetoothDaySummary) {
        this.bluetoothDaySummary = bluetoothDaySummary;
    }

    public WifiDaySummary getWifiDaySummary() {
        if (wifiDaySummary == null) {
            updateWifiDaySummary();
        }
        return wifiDaySummary;
    }

    public List<WifiDaySummary> getWifiDaySummaries() {
        return wifiDaySummaries;
    }

    public void setWifiDaySummary(WifiDaySummary wifiDaySummary) {
        this.wifiDaySummary = wifiDaySummary;
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
        updateWifiDaySummary();
        updateBluetoothDaySummary();
        createWifiChartModelLine();
        createBluetoothChartModelLine();
        createWifiRegistrationsModelLine();
        return "/reports/Retail?faces-redirect=true";
    }

    public String prepareIndex() {
        return "/reports/List?faces-redirect=true";
    }

    public CartesianChartModel getBluetoothChartModelLine() {
        if (btChartModelLine == null) {
            createBluetoothChartModelLine();
        }
        return btChartModelLine;
    }

    public CartesianChartModel getWifiRegistrationsModelLine() {
        if (wifiRegistrationsModelLine == null) {
            createWifiRegistrationsModelLine();
        }
        return wifiRegistrationsModelLine;
    }

    private void createBluetoothChartModelLine() {
        btChartModelLine = new CartesianChartModel();

        ChartSeries totalDownloads = new ChartSeries();
        totalDownloads.setLabel("Total Content Downloads");

        ChartSeries downloadsForDay = new ChartSeries();
        downloadsForDay.setLabel("Daily Content Downloads");

        ChartSeries devicesSeen = new ChartSeries();
        devicesSeen.setLabel("Daily Devices Seen");

        LinkedHashMap<Object, Number> dailySendMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> dailyDevicesSeenMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalSendMap = new LinkedHashMap<Object, Number>();

        for (BluetoothDaySummary summary : bluetoothDaySummaries) {
            String dateStr = DateUtil.formatDateForChart(summary.getEventDate());

            // because we track by device, there are multiple summaries per day, so we add them up for the daily total
            if (dailySendMap.containsKey(dateStr)) {
                long newValue = (Long) dailySendMap.get(dateStr) + summary.getTotalContentDownloads();
                dailySendMap.put(dateStr, newValue);
            } else {
                dailySendMap.put(dateStr, summary.getTotalContentDownloads());
            }

            // because we track by device, there are multiple summaries per day, so we add them up for the daily total
            if (dailyDevicesSeenMap.containsKey(dateStr)) {
                long newValue = (Long) dailyDevicesSeenMap.get(dateStr) + summary.getUniqueDevicesSeen();
                dailyDevicesSeenMap.put(dateStr, newValue);
            } else {
                dailyDevicesSeenMap.put(dateStr, summary.getUniqueDevicesSeen());
            }
        }

        // now it's summed by day, so ass them up for each day for the total overall
        long runningTotal = 0;
        for (Map.Entry<Object, Number> entry : dailySendMap.entrySet()) {
            Object dateStr = entry.getKey();
            Long number = (Long) entry.getValue();
            runningTotal += number;

            totalSendMap.put(dateStr, runningTotal);
        }

        totalDownloads.setData(totalSendMap);
        downloadsForDay.setData(dailySendMap);
        devicesSeen.setData(dailyDevicesSeenMap);

        btChartModelLine.addSeries(totalDownloads);
        btChartModelLine.addSeries(downloadsForDay);
        btChartModelLine.addSeries(devicesSeen);
    }

    private void createWifiRegistrationsModelLine() {
        wifiRegistrationsModelLine = new CartesianChartModel();
        wifiRegistrationCount = 0L;
        ChartSeries totalRegistrations = new ChartSeries();
        totalRegistrations.setLabel("Total Registrations");

        LinkedHashMap<Object, Number> totalRegistrationsMap = new LinkedHashMap<Object, Number>();
        List<WifiRegistration> registrations = wifiRegMgr.getWifiRegistrationInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
        if (registrations != null) {
            for (WifiRegistration registration : registrations) {
                wifiRegistrationCount++;
                String dateStr = DateUtil.formatDateForChart(registration.getEventDate());
                totalRegistrationsMap.put(dateStr, wifiRegistrationCount);
            }
        } else {
            totalRegistrationsMap.put(startDate, 0L);
            wifiRegistrationCount = 0L;
        }
        totalRegistrations.setData(totalRegistrationsMap);
        wifiRegistrationsModelLine.addSeries(totalRegistrations);
    }

    private void createWifiChartModelLine() {
        wifiChartModelLine = new CartesianChartModel();

        ChartSeries totalPageViews = new ChartSeries();
        totalPageViews.setLabel("Total Page Views");

        ChartSeries pageViewForDay = new ChartSeries();
        pageViewForDay.setLabel("Daily Page Views");

        LinkedHashMap<Object, Number> dailyPageViewMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> totalPageViewMap = new LinkedHashMap<Object, Number>();

        for (WifiDaySummary summary : wifiDaySummaries) {
            String dateStr = DateUtil.formatDateForChart(summary.getEventDate());

            // because we track by device, there are multiple summaries per day, so we add them up for the daily total
            if (dailyPageViewMap.containsKey(dateStr)) {
                long newValue = (Long) dailyPageViewMap.get(dateStr) + summary.getSuccessfulPageViews();
                dailyPageViewMap.put(dateStr, newValue);
            } else {
                dailyPageViewMap.put(dateStr, summary.getSuccessfulPageViews());
            }
        }

        // now it's summed by day, so ass them up for each day for the total overall
        long runningTotal = 0;
        for (Map.Entry<Object, Number> entry : dailyPageViewMap.entrySet()) {
            Object dateStr = entry.getKey();
            Long number = (Long) entry.getValue();
            runningTotal += number;

            totalPageViewMap.put(dateStr, runningTotal);
        }

        totalPageViews.setData(totalPageViewMap);
        pageViewForDay.setData(dailyPageViewMap);


        wifiChartModelLine.addSeries(totalPageViews);
        wifiChartModelLine.addSeries(pageViewForDay);
    }

    public Long getWifiChartLineMax() {
        long maxValue = wifiDaySummary.getTotalPageViews();
        // add 10% for a nice clean UI
        return Math.round(maxValue * 1.1);
    }

    public Long getWifiRegistrationLineMax() {
        long maxValue = wifiRegistrationCount;
        // add 10% for a nice clean UI
        return Math.round(maxValue * 1.1);
    }

    public Long getBluetoothChartLineMax() {
        long uniqueDevicesMax = 0;
        for (BluetoothDaySummary summary : bluetoothDaySummaries) {
            uniqueDevicesMax = Math.max(uniqueDevicesMax, summary.getUniqueDevicesSeen());
        }
        long maxValue = Math.max(
                bluetoothDaySummary.getTotalContentDownloads(), uniqueDevicesMax);
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
        List<WifiRegistration> logs = wifiRegistrationFacade.getWifiRegistrationInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
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
        List<WifiLog> logs = wifiLogFacade.getWifiLogInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
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

    private File exportBluetoothSendFile() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<BluetoothSend> logs = bluetoothFacade.getBluetoothSendInRangeByCampaignAndDevice(startDate, endDate, company, selectedCampaign, selectedDevice);
        new File(ServerURISettings.SERVER_TMP).mkdirs();
        File result = new File(ServerURISettings.SERVER_TMP + ServerURISettings.OS_SEP + System.currentTimeMillis() + ".csv");
        try {
            if (logs != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(result));
                writer.write(BluetoothSend.getCVSHeader());
                writer.newLine();
                for (BluetoothSend bs : logs) {
                    writer.write(bs.toCVS());
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

    private File exportBluetoothDwellFile() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<BluetoothDwell> logs = bluetoothFacade.getBluetoothDwellInRangeByCampaign(startDate, endDate, company, selectedCampaign);
        new File(ServerURISettings.SERVER_TMP).mkdirs();
        File result = new File(ServerURISettings.SERVER_TMP + ServerURISettings.OS_SEP + System.currentTimeMillis() + ".csv");
        try {
            if (logs != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(result));
                writer.write(BluetoothDwell.getCVSHeader());
                writer.newLine();
                for (BluetoothDwell bd : logs) {
                    writer.write(bd.toCVS());
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
            System.out.println("Inside");
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
                return new DefaultStreamedContent(stream, "text/plain", getDownloadedFileName("NoWifiLog", startDate, endDate));
            }
        } catch (Exception e) {
            logger.fatal(e);
            return null;
        }
    }

    public StreamedContent getBluetoothSendFile() {
        try {
            File result = exportBluetoothSendFile();
            if (result != null) {
                File zippedResult = ZipUnzip.GetZippedFile(result, getFileName("BluetoothSend", startDate, endDate));
                InputStream zipStream = new FileInputStream(zippedResult);
                return new DefaultStreamedContent(zipStream, "application/zip", getDownloadedFileName("BluetoothSend", startDate, endDate));
            } else {
                String start = new SimpleDateFormat("MMM, dd yyyy").format(startDate);
                String end = new SimpleDateFormat("MMM, dd yyyy").format(endDate);
                String str = "NO DATA IN RANGE: " + start + " - " + end;
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", getDownloadedFileName("NoBluetoothSend", startDate, endDate));
            }
        } catch (Exception e) {
            return null;
        }
    }

    public StreamedContent getBluetoothDwellFile() {
        try {
            File result = exportBluetoothDwellFile();
            if (result != null) {
                File zippedResult = ZipUnzip.GetZippedFile(result, getFileName("BluetoothDwell", startDate, endDate));
                InputStream zipStream = new FileInputStream(zippedResult);
                return new DefaultStreamedContent(zipStream, "application/zip", getDownloadedFileName("BluetoothDwell", startDate, endDate));
            } else {
                String str = "NO DATA ON RANGE: " + TimeConstants.formatDate(startDate) + " - " + TimeConstants.formatDate(endDate);
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", getDownloadedFileName("NoBluetoothDwell", startDate, endDate));
            }
        } catch (Exception e) {
            return null;
        }
    }

    public StreamedContent getWifiRegistrationFile() {
        try {
            File result = exportWifiRegistrationFile();
            if (result != null) {
                File zippedResult = ZipUnzip.GetZippedFile(result, getFileName("WifiRegistration", startDate, endDate));
                InputStream zipStream = new FileInputStream(zippedResult);
                return new DefaultStreamedContent(zipStream, "application/zip", getDownloadedFileName("WifiRegistration", startDate, endDate));
            } else {
                String start = new SimpleDateFormat("MMM, dd yyyy").format(startDate);
                String end = new SimpleDateFormat("MMM, dd yyyy").format(endDate);
                String str = "NO DATA ON RANGE: " + start + " - " + end;
                InputStream stream = new ByteArrayInputStream(str.getBytes());
                return new DefaultStreamedContent(stream, "text/plain", getDownloadedFileName("NoWifiRegistration", startDate, endDate));
            }
        } catch (Exception e) {
            return null;
        }
    }
}