/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.PricingModel;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@ManagedBean(name = "invoiceController")
@SessionScoped
public class InvoiceController extends AbstractController implements Serializable
{

    private String selectedMonth;
    private Company selectedGeoCompany;
    private List<Company> geoFenceCompanies;
    private PricingModel pricingModel;
    private long optinsCount = 0L;
    private BigDecimal totalSubscriberFee = new BigDecimal(0.00);
    private BigDecimal totalPropertiesFee = new BigDecimal(0.00);
    private BigDecimal totalNewPropertiesFee = new BigDecimal(0.00);
    private BigDecimal totalInvoice = new BigDecimal(0.00);
    private long totalMessagesSent = 0L;
    private long totalActiveSubscribers = 0L;
    private long totalOptOuts = 0L;
    private long propertiesCount = 0L;
    private long newPropertiesCount = 0L;
    private Date startDate;
    private Date endDate;
    
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    LocationDataManagerLocal locationMgr;
    @EJB
    MobileOfferSendLogManagerLocal sendLogMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    
    private static final Logger logger = Logger.getLogger(InvoiceController.class.getName());

    public long getOptinsCount()
    {
        retrieveOptinsCount();
        return optinsCount;
    }

    public void setOptinsCount(long optinsCount)
    {
        this.optinsCount = optinsCount;
    }

    public BigDecimal getTotalPropertiesFee()
    {
        calculateTotalPropertiesFee();
        return totalPropertiesFee;
    }

    public void setTotalPropertiesFee(BigDecimal totalPropertiesFee)
    {
        this.totalPropertiesFee = totalPropertiesFee;
    }

    public long getNewPropertiesCount()
    {
        retrieveNewPropertiesCount();
        return newPropertiesCount;
    }

    public void setNewPropertiesCount(long newPropertiesCount)
    {
        this.newPropertiesCount = newPropertiesCount;
    }

    public BigDecimal getTotalNewPropertiesFee()
    {
        calculateNewPropertiesFee();
        return totalNewPropertiesFee;
    }

    public void setTotalNewPropertiesFee(BigDecimal totalNewPropertiesFee)
    {
        this.totalNewPropertiesFee = totalNewPropertiesFee;
    }
    

    public PricingModel getPricingModel()
    {
        if (pricingModel == null) {
            retrievePricingModel();
        }
        return pricingModel;
    }

    public void setPricingModel(PricingModel pricingModel)
    {
        this.pricingModel = pricingModel;
    }

    public BigDecimal getTotalSubscriberFee()
    {
        calculateTotalSubscriberFee();
        return totalSubscriberFee;
    }

    public void setTotalSubscriberFee(BigDecimal totalSubscriberFee)
    {
        this.totalSubscriberFee = totalSubscriberFee;
    }

    public BigDecimal getTotalInvoice()
    {
        calculateTotalInvoice();
        return totalInvoice;
    }

    public void setTotalInvoice(BigDecimal totalInvoice)
    {
        this.totalInvoice = totalInvoice;
    }
    

    

    
    public long getPropertiesCount()
    {
        retrievePropertiesCount();
        return propertiesCount;
    }

    public void setPropertiesCount(long propertiesCount)
    {
        this.propertiesCount = propertiesCount;
    }

    public Date getEndDate()
    {
        if(endDate == null){
            endDate = DateUtil.getLastDayOfMonth(new Date());
        }
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getStartDate()
    {
        if(startDate == null){
            startDate = DateUtil.getFirstDayOfMonth(new Date());
        }
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }
    
    
    public String getSelectedMonth()
    {
        return selectedMonth;
    }

    public void setSelectedMonth(String selectedMonth)
    {
        this.selectedMonth = selectedMonth;
    }

    public List<Company> getGeoFenceCompanies()
    {
        if (geoFenceCompanies == null) {
            retrieveGeoFenceCompanies();
        }
        return geoFenceCompanies;
    }

    public void setGeoFenceCompanies(List<Company> geoFenceCompanies)
    {
        this.geoFenceCompanies = geoFenceCompanies;
    }

    private void retrieveGeoFenceCompanies()
    {
        geoFenceCompanies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);
    }

    public Company getSelectedGeoCompany(){
     if (this.selectedGeoCompany == null) {
            this.selectedGeoCompany = companyMgr.getCompanybyId(this.getCompanyIdFromSession());
        }
        return selectedGeoCompany;
    }
    
    public void setSelectedGeoCompany(Company selectedGeoCompany)
    {
        this.selectedGeoCompany = selectedGeoCompany;
    }

    public long getTotalActiveSubscribers()
    {
        retrieveTotalActiveSubscribers();
        return totalActiveSubscribers;
    }

    public void setTotalActiveSubscribers(long totalActiveSubscribers)
    {
        this.totalActiveSubscribers = totalActiveSubscribers;
    }

    public long getTotalMessagesSent()
    {
        retrieveTotalMessagesSent();
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(long totalMessagesSent)
    {
        this.totalMessagesSent = totalMessagesSent;
    }

    public long getTotalOptOuts()
    {
        retrieveOptOuts();
        return totalOptOuts;
    }

    public void setTotalOptOuts(long totalOptOuts)
    {
        this.totalOptOuts = totalOptOuts;
    }
    

    

    public String prepareInvoice()
    {
        return "/geo-reports/Invoice?faces-redirect=true";
    }

    public void updateInvoice(Company geoCompany, Date invoiceStartDate, Date invoiceEndDate)
    {
        
        if (geoCompany == null || geoCompany.getName() == null || geoCompany.getName().isEmpty()
                || invoiceStartDate == null || invoiceEndDate == null) {
//            logger.info("everything is null");
//            selectedGeoCompany = null;
//            prepareInvoice();
            return;
        }else{
            logger.info("startDate: " + startDate);
            logger.info("endDate: " + endDate);
            totalSubscriberFee = new BigDecimal(0.00);
            totalPropertiesFee = new BigDecimal(0.00);
            totalNewPropertiesFee = new BigDecimal(0.00);
            totalInvoice = new BigDecimal(0.00);
            optinsCount = 0L;
            propertiesCount = 0L;
            newPropertiesCount = 0L;
            selectedGeoCompany = companyMgr.getCompanybyName(geoCompany.getName());
            startDate = invoiceStartDate;
            endDate = invoiceEndDate;
            
            logger.info("selectedGeoCompany: " + selectedGeoCompany.getName());
            retrieveOptinsCount();
            logger.info("after retrieve: " + optinsCount);
            retrievePropertiesCount();
            logger.info("after retrieve: " + propertiesCount);
            retrieveNewPropertiesCount();
            logger.info("after retrieve: " + newPropertiesCount);
            calculateTotalSubscriberFee();
            logger.info("after calculate: " + totalSubscriberFee);
            calculateTotalPropertiesFee();
            logger.info("after calculate: " + totalPropertiesFee);
            calculateNewPropertiesFee();
            logger.info("after calculate: " + totalNewPropertiesFee);
            calculateTotalInvoice();
            logger.info("after calculate: " + totalInvoice);
        }    
    }
    private void retrieveOptinsCount(){
        if(selectedGeoCompany != null){
            optinsCount = subscriberMgr.countedActiveSubscribersBetweenDatesByCompany(startDate, endDate, selectedGeoCompany);
        }else{
            optinsCount = 0L;
        }    
    }
    
    private void retrievePricingModel(){
        if (selectedGeoCompany != null) {
            try{
                pricingModel = companyMgr.getPricingModelByCompany(selectedGeoCompany);
            }catch(Exception ex){
                pricingModel = new PricingModel();
                logger.error("Exception retrieving pricing model: " + ex);
            }
        }
    }
    private void calculateTotalSubscriberFee()
    {
        if (selectedGeoCompany != null) {
            optinsCount = subscriberMgr.countedActiveSubscribersBetweenDatesByCompany(startDate, endDate, selectedGeoCompany);
            try {
                String subscriberFee = companyMgr.getPricingModelByCompany(selectedGeoCompany).getSubscriberFee();
                BigDecimal bdOptins = new BigDecimal(optinsCount);
                BigDecimal bdSubscriberFee = new BigDecimal(subscriberFee);
                totalSubscriberFee = bdOptins.multiply(bdSubscriberFee).setScale(2, RoundingMode.HALF_EVEN);
            }catch(Exception ex){
                totalSubscriberFee = new BigDecimal(0.00); 
            }    
           
        }

    }
    private void retrievePropertiesCount(){
        if(selectedGeoCompany != null) {
            propertiesCount = propertyMgr.countPropertiesByCompanyBeforeDate(selectedGeoCompany, endDate);
        }else{
            propertiesCount = 0L;
        }
        
    }
    private void retrieveNewPropertiesCount(){
        if(selectedGeoCompany != null) {
            newPropertiesCount = propertyMgr.countNewPropertiesByCompanyBetweenDates(startDate, endDate, selectedGeoCompany);
        }else{
            newPropertiesCount = 0L;
        }
        
    }
    private void calculateTotalPropertiesFee()
    {
        if (selectedGeoCompany != null) {
            propertiesCount = propertyMgr.countPropertiesByCompanyBeforeDate(selectedGeoCompany, startDate);
            try{
                String propertiesFee = companyMgr.getPricingModelByCompany(selectedGeoCompany).getMonthlyPropertyFee();
                BigDecimal bdPropertiesCount = new BigDecimal(propertiesCount);
                BigDecimal bdPropertiesFee = new BigDecimal(propertiesFee);
                totalPropertiesFee = bdPropertiesCount.multiply(bdPropertiesFee).setScale(2, RoundingMode.HALF_EVEN);
            }catch(Exception ex){
                totalPropertiesFee = new BigDecimal(0.00);
                logger.error("Exception in calculating total properties fee: " + ex);
            }
        }

    }
    private void calculateNewPropertiesFee()
    {
        if (selectedGeoCompany != null) {
            newPropertiesCount = propertyMgr.countNewPropertiesByCompanyBetweenDates(startDate, endDate, selectedGeoCompany);
            try{
                String newPropertiesFee = companyMgr.getPricingModelByCompany(selectedGeoCompany).getNewPropertyFee();
                BigDecimal bdNewPropertiesCount = new BigDecimal(newPropertiesCount);
                BigDecimal bdNewPropertiesFee = new BigDecimal(newPropertiesFee);
                totalNewPropertiesFee = bdNewPropertiesCount.multiply(bdNewPropertiesFee).setScale(2, RoundingMode.HALF_EVEN);
            }catch(Exception ex){
                totalNewPropertiesFee = new BigDecimal(0.00);
                logger.error("Exception in calculating total properties fee: " + ex);
            }
        }

    }

    private void calculateTotalInvoice()
    {
        calculateTotalSubscriberFee();
        calculateTotalPropertiesFee();
        calculateNewPropertiesFee();
        totalInvoice = (totalSubscriberFee.add(totalPropertiesFee)).add(totalNewPropertiesFee);
    }

    private void retrieveTotalActiveSubscribers()
    {
        if (selectedGeoCompany != null) {
            totalActiveSubscribers = subscriberMgr.findOptedInCountByCompanyAndDate(selectedGeoCompany, startDate, endDate);
        }else{
            totalActiveSubscribers = 0L;
        }
        
    }

    private void retrieveTotalMessagesSent()
    {
        if(selectedGeoCompany != null) {
            totalMessagesSent = sendLogMgr.findSendLogsByCompanyAndDate(selectedGeoCompany, startDate, endDate);           
        }else{
            totalMessagesSent = 0L;
        }
    }

    private void retrieveOptOuts()
    {
        if(selectedGeoCompany != null) {
            totalOptOuts = subscriberMgr.countOptedOutByCompanyBetweenDates(selectedGeoCompany, startDate, endDate);
        }else{
            totalOptOuts = 0L;
        }
    }
            

    @FacesConverter(forClass = Company.class, value = "invoiceControllerConverter")
    public static class InvoiceControllerConverter implements Converter, Serializable
    {
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);

                Company controller = ((InvoiceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "invoiceController")).companyMgr.find(id);
                return controller;
            } catch (NumberFormatException e) {
                //throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Value not found"));
                throw new ConverterException();
            }
        }

        java.lang.Long getKey(String value)
        {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object value)
        {
            if (value == null || value.toString().isEmpty() || !(value instanceof Company)) {
                return null;
            }
            return String.valueOf(((Company) value).getId());
        }
    }
}   

