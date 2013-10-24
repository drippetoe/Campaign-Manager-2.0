/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.report.UserProfileDwell;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.*;
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
@ManagedBean(name = "userProfileReportController")
@SessionScoped
public class UserProfileReportController extends AbstractReportController implements Serializable
{

    private static final long serialVersionUID = 1;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    @EJB
    BluetoothDwellManagerLocal btDwellMgr;
    private List<Campaign> listCampaigns;
    private List<String> listDevices;
    private String totalDwellSessions = "N/A";
    private String totalDwellTime = "N/A";
    private String averageDwellTime = "N/A";
    private List<UserProfileDwell> dwellList;
    private List<UserProfileDwell> filteredDwellList;
    
    public void updateInterface() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        dwellList = btDwellMgr.getTotalDevicesSeen(company, selectedDevice, startDate, endDate);
        calculateDwellStats();
    }

    public List<UserProfileDwell> getFilteredDwellList() {
        return filteredDwellList;
    }

    public void setFilteredDwellList(List<UserProfileDwell> filteredDwellList) {
        this.filteredDwellList = filteredDwellList;
    }
    
    public List<Campaign> getListCampaigns()
    {
        if (listCampaigns == null)
        {
            populateListCampaigns();
        }
        return listCampaigns;
    }

    public void setListCampaigns(List<Campaign> listCampaigns)
    {
        this.listCampaigns = listCampaigns;
    }

    public List<String> getListDevices()
    {
        if (listDevices == null)
        {
            populateListDevices();
        }
        return listDevices;
    }

    public void setListDevices(List<String> listDevices)
    {
        this.listDevices = listDevices;
    }

    public void populateListCampaigns()
    {
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(companyMgr.find(this.getHttpSession().getCompany_id()));
        this.listCampaigns = new ArrayList<Campaign>();
        if (camps != null)
        {
            for (Campaign c : camps)
            {
                listCampaigns.add(c);
            }
        }
    }

    public void populateListDevices()
    {
        List<Device> devices = deviceMgr.getDeviceByCompany(companyMgr.find(this.getHttpSession().getCompany_id()));
        this.listDevices = new ArrayList<String>();
        if (devices != null)
        {
            for (Device d : devices)
            {
                listDevices.add(d.getName());
            }
        }
    }

    private void prepareVars()
    {
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = new Date();
        selectedCampaign = null;
        selectedDevice = null;
        company = companyMgr.find(this.getHttpSession().getCompany_id());
        listCampaigns = null;
        listDevices = null;
        dwellList = new ArrayList<UserProfileDwell>();
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        dwellReportResults();
    }

    public String prepareList()
    {
        prepareVars();
        updateInterface();
        return "/reports/UserProfile?faces-redirect=true";
    }

   

    public Campaign getCampaign()
    {
        if (selectedCampaign == null)
        {
            selectedCampaign = new Campaign();
        }
        return selectedCampaign;
    }

    public void setCampaign(Campaign campaign)
    {
        this.selectedCampaign = campaign;
    }

    public Device getDevice()
    {
        if (selectedDevice == null)
        {
            selectedDevice = new Device();
        }
        return selectedDevice;
    }

    public void setDevice(Device selectedDevice)
    {
        this.selectedDevice = selectedDevice;
    }

    public List<UserProfileDwell> getDwellList() {
        if(dwellList == null) {
            dwellList = new ArrayList<UserProfileDwell>();
        }
        return dwellList; 
    }

    public void setDwellList(List<UserProfileDwell> dwellList) {
        this.dwellList = dwellList;
    }

    
    
    
    public void dwellReportResults()
    {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        this.calculateDwellStats();
    }

    
    
    private void calculateDwellStats()
    {

        
        if (dwellList == null || dwellList.isEmpty())
        {
            this.totalDwellSessions = "0";
            this.totalDwellTime = "0 sec";
            this.averageDwellTime = "0 sec";
        } else
        {

            Long totalSessions = 0L;
            Long totalDwell = 0L;
            for (UserProfileDwell up : dwellList)
            {
                totalSessions += up.getTotalSessions();
                totalDwell += up.getTotalDwellTime();
            }
            this.totalDwellSessions = totalSessions + "";
            String total = formatMSTime(totalDwell);
            this.totalDwellTime = (total.isEmpty() ? "0 sec" : total);
            Double result = (double) totalDwell / (double) totalSessions;
            Long rlong = Math.round(result);
            String avg = formatMSTime(rlong);
            this.averageDwellTime = (avg.isEmpty() ? "0 sec" : avg);


        }
    }

    public String formatMSTime(Long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)));

        String result = "";
        if (hours != 0)
        {
            result += hours + ((hours == 1) ? " hr" : " hrs");
        }
        if (minutes != 0)
        {
            result += (hours != 0 ? ", " : "") + minutes + " min";
        }
        if (seconds != 0)
        {
            result += (hours != 0 || minutes != 0 ? ", " : "") + seconds + " sec";
        }

        return result;
    }

    public String getFormattedStartDate()
    {
        if (startDate == null)
        {
            return "";
        }
        return DateUtil.formatDateForWeb(startDate);
    }

    public String getFormattedEndDate()
    {
        if (endDate == null)
        {
            return "";
        }
        return DateUtil.formatDateForWeb(endDate);
    }

    public String getAverageDwellTime()
    {
        return averageDwellTime;
    }

    public void setAverageDwellTime(String averageDwellTime)
    {
        this.averageDwellTime = averageDwellTime;
    }

    public String getTotalDwellSessions()
    {
        return totalDwellSessions;
    }

    public void setTotalDwellSessions(String totalDwellSessions)
    {
        this.totalDwellSessions = totalDwellSessions;
    }

    public String getTotalDwellTime()
    {
        return totalDwellTime;
    }

    public void setTotalDwellTime(String totalDwellTime)
    {
        this.totalDwellTime = totalDwellTime;
    }

    @Override
    public Device getSelectedDevice()
    {
        if (this.selectedDevice == null)
        {
            this.selectedDevice = new Device();
        }
        return selectedDevice;
    }

    @Override
    public void setSelectedDevice(Device selectedDevice)
    {
        this.selectedDevice = selectedDevice;
    }

    @Override
    public Company getCompany()
    {
        if (company == null)
        {
            company = companyMgr.find(this.getHttpSession().getCompany_id());
        }
        return company;
    }

    @Override
    public void setCompany(Company company)
    {
        this.company = company;
    }

    @Override
    public Campaign getSelectedCampaign()
    {
        if (this.selectedCampaign == null)
        {
            this.selectedCampaign = new Campaign();
        }
        return selectedCampaign;
    }

    @Override
    public void setSelectedCampaign(Campaign selectedCampaign)
    {
        this.selectedCampaign = selectedCampaign;
    }
}
