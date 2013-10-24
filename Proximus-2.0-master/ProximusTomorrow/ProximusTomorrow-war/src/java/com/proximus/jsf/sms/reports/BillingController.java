/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.jsf.sms.reports;

import com.proximus.bean.sms.MobileSystemMessageProfile;
import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.MobileSystemMessageManagerLocal;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "billingController")
@ViewScoped
public class BillingController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    private static final double MESSAGE_SENT_COST = 0.04;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    MobileSystemMessageManagerLocal messageMgr;
    @EJB
    MobileOfferSendLogManagerLocal sendLogMgr;
    private String kazeBill;
    private Date startDate;
    private Date endDate;
    private Long totalMessages;
    private List<MobileSystemMessageProfile> messageProfile;
    private Company selectedCompany;
    private List<Company> listCompanies;

    public BillingController() {
    }

    public Company getSelectedCompany() {
        if (this.selectedCompany == null) {
            this.selectedCompany = companyMgr.getCompanybyId(this.getCompanyIdFromSession());
        }
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<Company> getListCompanies() {
        if (listCompanies == null) {
            this.populateListCompany();
        }
        return listCompanies;
    }

    public void populateListCompany() {
        listCompanies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);

    }

    public void setListCompanies(List<Company> listCompanies) {
        this.listCompanies = listCompanies;
    }

    public Long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public Date getStartDate() {
        if (startDate == null) {
            startDate = DateUtil.getFirstDayOfMonth(new Date());
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = DateUtil.getLastDayOfMonth(new Date());
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getKazeBill() {
        if (kazeBill == null) {
            calculateBill();
        }
        return kazeBill;
    }

    public void setKazeBill(String kazeBill) {
        this.kazeBill = kazeBill;
    }

    public List<MobileSystemMessageProfile> getMessageProfile() {
        if (messageProfile == null) {
            populateBillingTable();
        }
        return messageProfile;
    }

    public void setMessageProfile(List<MobileSystemMessageProfile> messageProfile) {
        this.messageProfile = messageProfile;
    }

    private void populateBillingTable() {
        messageProfile = new ArrayList<MobileSystemMessageProfile>();
        SortedMap sortedSystemMessageMap = new TreeMap();
        Company realCompany = companyMgr.getCompanybyName(getSelectedCompany().getName());

        Map systemMessageMap = messageMgr.getSystemMessageMap(realCompany, getStartDate(), getEndDate());
        Map offerMap = sendLogMgr.getSendLogMap(realCompany, startDate, endDate);
        if (systemMessageMap != null) {
            sortedSystemMessageMap.putAll(systemMessageMap);
            for (Iterator it = sortedSystemMessageMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, Number> entry = (Entry<String, Number>) it.next();
                MobileSystemMessageProfile systemMessage = new MobileSystemMessageProfile();
                systemMessage.setSystemMessage(entry.getKey());
                systemMessage.setMessageCount((Long) entry.getValue());
                Double value = systemMessage.getMessageCount() * MESSAGE_SENT_COST;
                systemMessage.setCost(formatDoubleToDecimal(value));
                messageProfile.add(systemMessage);
            }
        }
        if (offerMap != null) {
            sortedSystemMessageMap.putAll(offerMap);
            for (Iterator it = sortedSystemMessageMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, Number> entry = (Entry<String, Number>) it.next();
                MobileSystemMessageProfile systemMessage = new MobileSystemMessageProfile();
                systemMessage.setSystemMessage(entry.getKey());
                systemMessage.setMessageCount((Long) entry.getValue());
                Double value = systemMessage.getMessageCount() * MESSAGE_SENT_COST;
                systemMessage.setCost(formatDoubleToDecimal(value));
                if (messageProfile.contains(systemMessage)) {
                    continue;
                }
                messageProfile.add(systemMessage);
            }
        }
    }

    private void calculateBill() {
        Company realCompany = companyMgr.getCompanybyName(getSelectedCompany().getName());
        Long systemMessages = messageMgr.getTotalMessageCountByCompanyAndDate(realCompany, getStartDate(), getEndDate());
        Long offersSent = sendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndCompany(getStartDate(), getEndDate(), realCompany, MobileOfferSendLog.STATUS_DELIVERED);
        setKazeBill(formatDoubleToDecimal((systemMessages + offersSent) * MESSAGE_SENT_COST));
        setTotalMessages(systemMessages + offersSent);
    }

    public void updateBillingReport() {
        populateBillingTable();
        calculateBill();
    }

    private String formatDoubleToDecimal(double aNumber) {
        String formattedNumber = null;
        try {
            DecimalFormat myFormatter = new DecimalFormat("0.00");
            formattedNumber = myFormatter.format(aNumber);
        } catch (NumberFormatException ex) {
            return formattedNumber;
        }
        return formattedNumber;
    }

    public void prepareVars() {
        startDate = null;
        endDate = null;
        this.listCompanies = null;
        this.selectedCompany = null;
        kazeBill = null;
        messageProfile = null;
    }

    public String prepareBillingReport() {
        prepareVars();
        return "/geo-reports/Billing?faces-redirect=true";
    }
}
