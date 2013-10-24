/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.bean.sms.CarrierProfile;
import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.LoginController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "carrierProfileController")
@SessionScoped
public class CarrierProfileController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    UserManagerLocal userMgr;
    private List<Company> companyList;
    private Company selectedCompany;
    private List<CarrierProfile> supportedCarrierProfile;
    private List<CarrierProfile> unSupportedCarrierProfile;
    private List<CarrierProfile> subscriberStatusProfile;
    private PieChartModel supportedCarrierModel;
    private PieChartModel unSupportedCarrierModel;
    private PieChartModel subscriberStatusModel;
    private Long totalSupportedCarriers;
    private Long totalUnSupportedCarriers;
    private Long totalSubscribers;

    public Long getTotalSupportedCarriers() {
        return totalSupportedCarriers;
    }

    public void setTotalSupportedCarriers(Long totalSupportedCarriers) {
        this.totalSupportedCarriers = totalSupportedCarriers;
    }

    public Long getTotalUnSupportedCarriers() {
        return totalUnSupportedCarriers;
    }

    public void setTotalUnSupportedCarriers(Long totalUnSupportedCarriers) {
        this.totalUnSupportedCarriers = totalUnSupportedCarriers;
    }

    public Long getTotalSubscribers() {
        return totalSubscribers;
    }

    public void setTotalSubscribers(Long totalSubscribers) {
        this.totalSubscribers = totalSubscribers;
    }

    public Company getSelectedCompany() {
        if (selectedCompany == null) {
            selectedCompany = companyMgr.find(getCompanyIdFromSession());
        }
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<Company> getCompanyList() {
        if (this.companyList == null || this.companyList.isEmpty()) {
            populateCompanyList();
        }
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public void populateCompanyList() {
        companyList = userMgr.getCompaniesFromUserAndLicense(this.getHttpSession().getCurrUser(), License.LICENSE_GEOFENCE);
    }

    public PieChartModel getSupportedCarrierModel() {
        return supportedCarrierModel;
    }

    public PieChartModel getUnSupportedCarrierModel() {
        return unSupportedCarrierModel;
    }

    public PieChartModel getSubscriberStatusModel() {
        return subscriberStatusModel;
    }

    public List<CarrierProfile> getSupportedCarrierProfile() {
        return supportedCarrierProfile;
    }

    public void setSupportedCarrierProfile(List<CarrierProfile> supportedCarrierProfile) {
        this.supportedCarrierProfile = supportedCarrierProfile;
    }

    public List<CarrierProfile> getUnSupportedCarrierProfile() {
        return unSupportedCarrierProfile;
    }

    public void setUnSupportedCarrierProfile(List<CarrierProfile> unSupportedCarrierProfile) {
        this.unSupportedCarrierProfile = unSupportedCarrierProfile;
    }

    public List<CarrierProfile> getSubscriberStatusProfile() {
        return subscriberStatusProfile;
    }

    public void setSubscriberStatusProfile(List<CarrierProfile> subscriberStatusProfile) {
        this.subscriberStatusProfile = subscriberStatusProfile;
    }

    public void createPieModel() {
        //TODO: This is just ugly, using a converter correctly could remove this
        if (selectedCompany == null) {
            getSelectedCompany();
        } else {
            selectedCompany = companyMgr.getCompanybyName(selectedCompany.getName());
        }
        supportedCarrierProfile = new ArrayList<CarrierProfile>();
        unSupportedCarrierProfile = new ArrayList<CarrierProfile>();
        subscriberStatusProfile = new LinkedList<CarrierProfile>();

        SortedMap sortedSupportedCarrierMap = new TreeMap();
        SortedMap sortedUnsupportedCarrierMap = new TreeMap();
        totalSupportedCarriers = subscriberMgr.getNrOfSupportedCarriers(selectedCompany);
        totalUnSupportedCarriers = subscriberMgr.getNrOfUnSupportedCarriers(selectedCompany);
        totalSubscribers = subscriberMgr.countAllByCompany(selectedCompany);

        Map supportedCarrierMap = subscriberMgr.getSupportedCarrierSubscriberMap(selectedCompany);
        if (supportedCarrierMap != null) {
            sortedSupportedCarrierMap.putAll(supportedCarrierMap);

            for (Iterator it = sortedSupportedCarrierMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, Number> entry = (Entry<String, Number>) it.next();
                CarrierProfile supportedCarrier = new CarrierProfile();
                supportedCarrier.setCarrier(entry.getKey());
                supportedCarrier.setUniqueSubscribers((Long) entry.getValue());
                supportedCarrierProfile.add(supportedCarrier);
            }
        }
        Map unSupportedCarrierMap = subscriberMgr.getUnSupportedCarrierSubscriberMap(selectedCompany);
        if (unSupportedCarrierMap != null) {
            sortedUnsupportedCarrierMap.putAll(unSupportedCarrierMap);
            for (Iterator it = sortedUnsupportedCarrierMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, Number> entry = (Entry<String, Number>) it.next();
                CarrierProfile unSupportedCarrier = new CarrierProfile();
                unSupportedCarrier.setCarrier(entry.getKey());
                unSupportedCarrier.setUniqueSubscribers((Long) entry.getValue());
                unSupportedCarrierProfile.add(unSupportedCarrier);
            }
        }

        Map subscriberStatusMap = subscriberMgr.getSubscriberStatuses(selectedCompany);
        if (subscriberStatusMap != null) {
            for (Iterator it = subscriberStatusMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, Number> entry = (Entry<String, Number>) it.next();
                CarrierProfile subscriberStatus = new CarrierProfile();
                subscriberStatus.setCarrier(entry.getKey());
                subscriberStatus.setUniqueSubscribers((Long) entry.getValue());
                subscriberStatusProfile.add(subscriberStatus);
            }
        }

        supportedCarrierModel = new PieChartModel(sortedSupportedCarrierMap);
        unSupportedCarrierModel = new PieChartModel(sortedUnsupportedCarrierMap);
        subscriberStatusModel = new PieChartModel(subscriberStatusMap);
    }

    public void prepareVars() {
        selectedCompany = null;
        totalSubscribers = null;
        totalSupportedCarriers = null;
        totalUnSupportedCarriers = null;
        companyList = new LinkedList<Company>();
        supportedCarrierProfile = null;
        unSupportedCarrierProfile = null;
        subscriberStatusProfile = null;
        supportedCarrierModel = null;
        unSupportedCarrierModel = null;
        subscriberStatusModel = null;
        createPieModel();
    }

    public String prepareProfile() {
        prepareVars();
        return "/geo-reports/CarrierProfile?faces-redirect=true";
    }
}
