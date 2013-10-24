/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.WifiLog;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.WifiLogManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "mostPopularServersReportController")
@SessionScoped
public class MostPopularServersReportController extends AbstractReportController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    WifiLogManagerLocal wifiMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    private WifiLog wifiLog;
    private List<WifiLog> wifiLogs;
    private List<WifiLog> filteredWifiLogs;
    private List<Campaign> listCampaigns;
    private List<String> listDevices;

    public MostPopularServersReportController() {
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
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(companyMgr.find(getCompanyIdFromSession()));
        this.listCampaigns = new ArrayList<Campaign>();
        if (camps != null) {
            for (Campaign c : camps) {
                listCampaigns.add(c);
            }
        }
    }

    public void populateListDevices() {
        List<Device> devices = deviceMgr.getDeviceByCompany(companyMgr.find(getCompanyIdFromSession()));
        this.listDevices = new ArrayList<String>();
        if (devices != null) {
            for (Device d : devices) {
                listDevices.add(d.getName());
            }
        }
    }

    public WifiLog getWifiLog() {
        return wifiLog;
    }

    public void setWifiLog(WifiLog wifiLog) {
        this.wifiLog = wifiLog;
    }

    public List<WifiLog> getWifiLogs() {
        if (wifiLogs == null) {
            updateMostPopularServers();
        }
        return wifiLogs;
    }

    public void setWifiLogs(List<WifiLog> wifiLogs) {
        this.wifiLogs = wifiLogs;
    }

    public List<WifiLog> getFilteredWifiLogs() {
        return filteredWifiLogs;
    }

    public void setFilteredWifiLogs(List<WifiLog> filteredWifiLogs) {
        this.filteredWifiLogs = filteredWifiLogs;
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
        listDevices = null;
    }

    public void updateInterface() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        updateMostPopularServers();
    }

    public String renderMostPopularServers() {
        clearCriteria();
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = new Date();
        updateMostPopularServers();
        return "/reports/MostPopularServers?faces-redirect=true";
    }

    private void updateMostPopularServers() {

        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        wifiLogs = wifiMgr.getMostPopularServersFromWifiLog(startDate, endDate, company, selectedCampaign, selectedDevice);
        filteredWifiLogs = new ArrayList<WifiLog>(wifiLogs);
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
