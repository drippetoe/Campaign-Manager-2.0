package com.proximus.jsf;

import com.proximus.bean.PerFilePerDeviceSendSummary;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.BluetoothFileSendSummary;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.*;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "bluetoothfilesendController")
@SessionScoped
public class BluetoothFileSendController extends AbstractReportController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    BluetoothReportManagerLocal btrMgr;
    @EJB
    WifiLogManagerLocal wifiMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    private BluetoothFileSendSummary fileSendSummary;
    private List<PerFilePerDeviceSendSummary> btFileSendSummaries;
    private List<Campaign> listCampaigns;

    public BluetoothFileSendController() {
    }

    public BluetoothFileSendSummary getFileSendSummary() {
        return fileSendSummary;
    }

    public void setFileSendSummary(BluetoothFileSendSummary fileSendSummary) {
        this.fileSendSummary = fileSendSummary;
    }

    public List<PerFilePerDeviceSendSummary> getBtFileSendSummaries() {
        if (btFileSendSummaries == null) {
            updateBluetoothFileSendSummary();
        }
        return btFileSendSummaries;
    }

    public void setBtFileSendSummaries(List<PerFilePerDeviceSendSummary> btFileSendSummaries) {
        this.btFileSendSummaries = btFileSendSummaries;
    }

    public List<Campaign> getListCampaigns() {
        if (listCampaigns == null) {
            populateListCampaigns();
        }
        return listCampaigns;
    }

    public void setListCampaigns(List<Campaign> listCampaigns) {
        this.listCampaigns = listCampaigns;
    }

    public void populateListCampaigns() {
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(companyMgr.find(this.getHttpSession().getCompany_id()));
        this.listCampaigns = new ArrayList<Campaign>();
        if (camps != null) {
            for (Campaign c : camps) {
                listCampaigns.add(c);
            }
        }
    }

    public BluetoothReportManagerLocal getBtrMgr() {
        return btrMgr;
    }

    public void setBtrMgr(BluetoothReportManagerLocal btrMgr) {
        this.btrMgr = btrMgr;
    }

    public String getFormattedStartDate() {
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate() {
        return DateUtil.formatDateForWeb(endDate);
    }

    private void clearCriteria() {
        company = null;
        listCampaigns = null;
    }

    public String renderFileSendSummary() {
        clearCriteria();
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = new Date();
        updateBluetoothFileSendSummary();
        return "/reports/FileSendSummary?faces-redirect=true";
    }

    public void updateInterface() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        updateBluetoothFileSendSummary();
    }

    private void updateBluetoothFileSendSummary() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        List<BluetoothFileSendSummary> detailSendSummaries = btrMgr.fetchBluetoothSendSummaries(startDate, endDate, company, selectedCampaign, selectedDevice);

        // keyed by file, then subkeyed by device
        TreeMap<String, PerFilePerDeviceSendSummary> fileMap = new TreeMap<String, PerFilePerDeviceSendSummary>();


        HashMap<Long, Long> deviceWifiCountMap = new HashMap<Long, Long>();
        HashMap<Long, Long> btDetectCountMap = new HashMap<Long, Long>();

        // these summaries are per device
        for (BluetoothFileSendSummary btSummary : detailSendSummaries) {

            String fileKey = btSummary.getFile();

            PerFilePerDeviceSendSummary summary;
            if (fileMap.containsKey(fileKey)) {
                summary = fileMap.get(fileKey);
            } else {
                summary = new PerFilePerDeviceSendSummary();
                summary.setFile(fileKey);
            }

            long deviceId = btSummary.getDevice().getId();

            if (!deviceWifiCountMap.containsKey(deviceId)) {
                List<WifiDaySummary> wiSums = wifiMgr.fetchWifiDaySummaries(startDate, endDate, btSummary.getCompany(), btSummary.getCampaign(), btSummary.getDevice());

                long totalPageViewCount = 0;
                for (WifiDaySummary wifiDaySummary : wiSums) {
                    totalPageViewCount += wifiDaySummary.getSuccessfulPageViews();
                }
                deviceWifiCountMap.put(deviceId, totalPageViewCount);
            }
            if (!btDetectCountMap.containsKey(deviceId)) {
                long totalDetections = 0;
                List<BluetoothDaySummary> btSums = btrMgr.fetchBluetoothDaySummaries(startDate, endDate, btSummary.getCompany(), btSummary.getCampaign(), btSummary.getDevice());
                for (BluetoothDaySummary bluetoothDaySummary : btSums) {
                    totalDetections += bluetoothDaySummary.getUniqueDevicesSeen();
                }

                btDetectCountMap.put(deviceId, totalDetections);
            }

            btSummary.setBluetoothDetections(btDetectCountMap.get(deviceId));
            btSummary.setWifiSuccessfulPageViews(deviceWifiCountMap.get(deviceId));

            summary.putEntry(deviceId, btSummary);

            fileMap.put(fileKey, summary);
        }

        btFileSendSummaries = new ArrayList<PerFilePerDeviceSendSummary>(fileMap.values());
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

    @Override
    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }
}
