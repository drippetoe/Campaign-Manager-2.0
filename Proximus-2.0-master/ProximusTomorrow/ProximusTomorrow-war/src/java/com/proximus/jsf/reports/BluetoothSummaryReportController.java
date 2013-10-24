/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.reports;

import com.proximus.data.BluetoothSend;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.ViewBluetoothDaySummary;
import com.proximus.data.report.ViewWifiDaySummary;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractReportController;
import com.proximus.manager.BluetoothSendManagerLocal;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.util.ServerURISettings;
import com.proximus.util.TimeConstants;
import com.proximus.util.ZipUnzip;
import java.io.*;
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
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "bluetoothSummaryReportController")
@SessionScoped
public class BluetoothSummaryReportController extends AbstractReportController implements Serializable {

    private static final Logger logger = Logger.getLogger(BluetoothSummaryReportController.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    @EJB
    BluetoothSendManagerLocal bluetoothSendMgr;
    //GRAPH VARS
    private CartesianChartModel bluetoothChartModel;
    private List<ViewBluetoothDaySummary> bluetoothSendDaySummaries;
    //DATA MODEL
    private List<Campaign> listCampaigns;
    private List<String> listDevices;
    private long maxChartVal;
    private ResourceBundle messages;
    private Long totalDevicesSeen;
    private Long uniqueDevicesSeen;
    private Long uniqueDevicesAcceptingPush;
    private Long uniqueDevicesDownloadingContent;
    private Long totalContentDownloads;

    public BluetoothSummaryReportController() {
        messages = this.getHttpSession().getMessages();
    }

    public void updateInterface() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        populateBluetoothSendReport();
        createBluetoothChartModel();
    }

    private void prepareVars() {
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = this.getEndDate();
        selectedCampaign = null;
        selectedDevice = null;
        company = companyMgr.find(this.getHttpSession().getCompany_id());
        listCampaigns = null;
        listDevices = null;
        campaignMap = null;
        totalDevicesSeen = 0L;
        uniqueDevicesSeen = 0L;
        uniqueDevicesAcceptingPush = 0L;
        uniqueDevicesDownloadingContent = 0L;
        totalContentDownloads = 0L;
        bluetoothChartModel = null;
        maxChartVal = 0;
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
    }

    public String prepareList() {
        prepareVars();
        updateInterface();
        return "/reports/BluetoothSummaryReport?faces-redirect=true";
    }

    private void populateBluetoothSendReport() {
        this.totalDevicesSeen = bluetoothSendMgr.getTotalDevicesSeen(company, selectedCampaign, selectedDevice, startDate, endDate);
        this.uniqueDevicesSeen = bluetoothSendMgr.getUniqueDevicesSeen(company, selectedCampaign, selectedDevice, startDate, endDate);
        this.uniqueDevicesAcceptingPush = bluetoothSendMgr.getUniqueDevicesAcceptingPush(company, selectedCampaign, selectedDevice, startDate, endDate);
        this.uniqueDevicesDownloadingContent = bluetoothSendMgr.getUniqueDevicesDownloadingContent(company, selectedCampaign, selectedDevice, startDate, endDate);
        this.totalContentDownloads = bluetoothSendMgr.getTotalContentDownloads(company, selectedCampaign, selectedDevice, startDate, endDate);

    }

    private void populateBluetoothDaySummary() {
        Map<Date, ViewBluetoothDaySummary> map = new HashMap<Date, ViewBluetoothDaySummary>();
        List<ViewBluetoothDaySummary> devicesSeen = bluetoothSendMgr.getBluetoothDaySummaryForDevicesSeen(company, selectedCampaign, selectedDevice, startDate, endDate);
        if (devicesSeen != null) {
            for (ViewBluetoothDaySummary d : devicesSeen) {
                map.put(d.getEventDate(), d);
            }
        }

        List<ViewBluetoothDaySummary> contentDownloaded = bluetoothSendMgr.getBluetoothDaySummaryForContentDownload(company, selectedCampaign, selectedDevice, startDate, endDate);
        if (contentDownloaded != null) {
            for (ViewBluetoothDaySummary c : contentDownloaded) {
                if (map.containsKey(c.getEventDate())) {
                    map.get(c.getEventDate()).setDownloads(c.getDownloads());
                } else {
                    map.put(c.getEventDate(), c);
                }
            }
        }
        bluetoothSendDaySummaries = new ArrayList<ViewBluetoothDaySummary>();
        Set<Map.Entry<Date, ViewBluetoothDaySummary>> values = map.entrySet();
        for (Map.Entry<Date, ViewBluetoothDaySummary> entry : values) {
            bluetoothSendDaySummaries.add(entry.getValue());

        }
        Collections.sort(bluetoothSendDaySummaries);
    }

    private void createBluetoothChartModel() {
        maxChartVal = 0;
        bluetoothChartModel = new CartesianChartModel();
        ChartSeries totalContentDownloadsSeries = new ChartSeries();
        totalContentDownloadsSeries.setLabel(messages.getString("totalContentDownloads"));

        ChartSeries dailyContentDownloadsSeries = new ChartSeries();
        dailyContentDownloadsSeries.setLabel(messages.getString("dailyContentDownloads"));

//        ChartSeries dailyDevicesSeenSeries = new ChartSeries();
//        dailyDevicesSeenSeries.setLabel(messages.getString("dailyDevicesSeen"));

        LinkedHashMap<Object, Number> totalContentDownloadsMap = new LinkedHashMap<Object, Number>();
        LinkedHashMap<Object, Number> dailyContentDownloadsMap = new LinkedHashMap<Object, Number>();
        //LinkedHashMap<Object, Number> dailyDevicesSeenMap = new LinkedHashMap<Object, Number>();

        populateBluetoothDaySummary();

        if (bluetoothSendDaySummaries == null || bluetoothSendDaySummaries.isEmpty()) {
            totalContentDownloadsMap.put(DateUtil.formatDateForChart(new Date()), 0L);
            dailyContentDownloadsMap.put(DateUtil.formatDateForChart(new Date()), 0L);
            //dailyDevicesSeenMap.put(DateUtil.formatDateForChart(new Date()), 0L);
        } else {

            long runningTotal = 0;
            for (ViewBluetoothDaySummary summary : bluetoothSendDaySummaries) {
                String dateStr = DateUtil.formatDateForChart(summary.getEventDate());
                long dailyDeviceSeen = summary.getDevicesSeen();
                long dailyDownloads = summary.getDownloads();
                dailyContentDownloadsMap.put(dateStr, dailyDownloads);
                // dailyDevicesSeenMap.put(dateStr, dailyDeviceSeen);
                runningTotal += dailyDownloads;
                totalContentDownloadsMap.put(dateStr, runningTotal);

            }
            maxChartVal = runningTotal;

            if (maxChartVal % 6 != 0) {
                maxChartVal += (6 - (maxChartVal % 6));
            } else {
                maxChartVal += 6;
            }
            if (maxChartVal < 12) {
                maxChartVal = 12;
            }

            totalContentDownloadsSeries.setData(totalContentDownloadsMap);
            dailyContentDownloadsSeries.setData(dailyContentDownloadsMap);
            //  dailyDevicesSeenSeries.setData(dailyDevicesSeenMap);

            bluetoothChartModel.addSeries(totalContentDownloadsSeries);
            bluetoothChartModel.addSeries(dailyContentDownloadsSeries);
            //   bluetoothChartModel.addSeries(dailyDevicesSeenSeries);
        }
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

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    public Long getTotalContentDownloads() {
        return totalContentDownloads;
    }

    public void setTotalContentDownloads(Long totalContentDownloads) {
        this.totalContentDownloads = totalContentDownloads;
    }

    public Long getTotalDevicesSeen() {
        return totalDevicesSeen;
    }

    public void setTotalDevicesSeen(Long totalDevicesSeen) {
        this.totalDevicesSeen = totalDevicesSeen;
    }

    public Long getUniqueDevicesAcceptingPush() {
        return uniqueDevicesAcceptingPush;
    }

    public void setUniqueDevicesAcceptingPush(Long uniqueDevicesAcceptingPush) {
        this.uniqueDevicesAcceptingPush = uniqueDevicesAcceptingPush;
    }

    public Long getUniqueDevicesDownloadingContent() {
        return uniqueDevicesDownloadingContent;
    }

    public void setUniqueDevicesDownloadingContent(Long uniqueDevicesDownloadingContent) {
        this.uniqueDevicesDownloadingContent = uniqueDevicesDownloadingContent;
    }

    public Long getUniqueDevicesSeen() {
        return uniqueDevicesSeen;
    }

    public void setUniqueDevicesSeen(Long uniqueDevicesSeen) {
        this.uniqueDevicesSeen = uniqueDevicesSeen;
    }

    public CartesianChartModel getBluetoothChartModel() {
        if (bluetoothChartModel == null) {
            bluetoothChartModel = new CartesianChartModel();
        }
        return bluetoothChartModel;
    }

    public void setBluetoothChartModel(CartesianChartModel bluetoothChartModel) {
        this.bluetoothChartModel = bluetoothChartModel;
    }

    public List<ViewBluetoothDaySummary> getBluetoothSendDaySummaries() {
        if (bluetoothSendDaySummaries == null) {
            this.bluetoothSendDaySummaries = new ArrayList<ViewBluetoothDaySummary>();
        }
        return bluetoothSendDaySummaries;
    }

    public void setBluetoothSendDaySummaries(List<ViewBluetoothDaySummary> bluetoothSendDaySummaries) {
        this.bluetoothSendDaySummaries = bluetoothSendDaySummaries;
    }

    public long getMaxChartVal() {
        return maxChartVal;
    }

    public void setMaxChartVal(long maxChartVal) {
        this.maxChartVal = maxChartVal;
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
    
    
    private String getFileName(String type, Date startDate, Date endDate) {
        return "downloaded_" + type + "RawData_" + TimeConstants.formatForLogFileName(startDate) + "-" + TimeConstants.formatForLogFileName(endDate) + ".csv";
    }

    private String getDownloadedFileName(String type, Date startDate, Date endDate) {
        return "downloaded_" + type + "RawData_" + TimeConstants.formatForLogFileName(startDate) + "-" + TimeConstants.formatForLogFileName(endDate) + ".zip";
    }
    
    
    private File exportBluetoothSendFile() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<BluetoothSend> logs = bluetoothSendMgr.getBluetoothSendInRangeByCampaignAndDevice(company, selectedCampaign, selectedDevice, startDate, endDate);
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
}
