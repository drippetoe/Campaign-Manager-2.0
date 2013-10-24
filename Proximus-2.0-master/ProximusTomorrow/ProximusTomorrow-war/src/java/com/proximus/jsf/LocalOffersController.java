/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Barcode;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.BarcodeManagerLocal;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "localOffersController")
@SessionScoped
public class LocalOffersController extends AbstractReportController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    BarcodeManagerLocal barcodeMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    private List<String> listCampaigns;
    private List<String> listDevices;
    private List<Barcode> barcodeList;
    private List<Barcode> filteredBarcodes;

    public List<Barcode> getBarcodeList() {
        if (barcodeList == null) {
            populateBarcodeList();
        }
        return barcodeList;
    }

    public void setBarcodeList(List<Barcode> barcodeList) {
        this.barcodeList = barcodeList;
    }

    public List<Barcode> getFilteredBarcodes() {
        return filteredBarcodes;
    }

    public void setFilteredBarcodes(List<Barcode> filteredBarcodes) {
        this.filteredBarcodes = filteredBarcodes;
    }

    public void populateBarcodeList() {
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        try {
            barcodeList = barcodeMgr.fetchBarcodeOffers(startDate, endDate, company, selectedCampaign, selectedDevice);
            filteredBarcodes = new ArrayList<Barcode>(barcodeList);
        } catch (Exception err) {
            System.err.println(err.getMessage());
            barcodeList = null;
        }
    }

    public List<String> getListCampaigns() {
        if (listCampaigns == null) {
            populateListCampaigns();
        }
        return listCampaigns;
    }

    public void setListCampaigns(List<String> listCampaigns) {
        this.listCampaigns = listCampaigns;
    }

    public void populateListCampaigns() {
        List<Campaign> camps = campaignMgr.findAllByCompanyActive(companyMgr.find(this.getHttpSession().getCompany_id()));
        this.listCampaigns = new ArrayList<String>();
        if (camps != null) {
            for (Campaign c : camps) {
                listCampaigns.add(c.getName());
            }
        }
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

    public void populateListDevices() {
        List<Device> devices = deviceMgr.getDeviceByCompany(companyMgr.find(this.getHttpSession().getCompany_id()));
        this.listDevices = new ArrayList<String>();
        if (devices != null) {
            for (Device d : devices) {
                listDevices.add(d.getName());
            }
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

    @Override
    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    private void prepareVars() {
        startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        endDate = new Date();
        selectedCampaign = null;
        selectedDevice = null;
        company = companyMgr.find(this.getHttpSession().getCompany_id());
        listCampaigns = null;
        listDevices = null;
        this.prepareReportCriteria(companyMgr, deviceMgr, campaignMgr);
        this.populateListCampaigns();
        this.populateListDevices();
    }

    public String prepareList() {
        prepareVars();
        return "/reports/LocalOffersReport";
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
    
    public void prepareBarcodeList() {
         this.populateBarcodeList();
    }
}
