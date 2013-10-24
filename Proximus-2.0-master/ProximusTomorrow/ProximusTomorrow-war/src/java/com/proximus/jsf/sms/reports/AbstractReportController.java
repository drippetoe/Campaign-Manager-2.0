/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Company;
import com.proximus.data.sms.DMA;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Retailer;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.DMAManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.RetailerManagerLocal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ronald
 */
public abstract class AbstractReportController extends AbstractController {

    protected Company company;
    protected DMA selectedDMA;
    protected Property selectedProperty;
    protected Retailer selectedRetailer;
    protected Date startDate;
    protected Date endDate;
    protected Date today;

    public abstract Company getCompany();

    public abstract void setCompany(Company c);

    public abstract DMA getSelectedDMA();

    public abstract void setSelectedDMA(DMA selectedDMA);

    public abstract Property getSelectedProperty();

    public abstract void setSelectedProperty(Property selectedProperty);

    public abstract Retailer getSelectedRetailer();

    public abstract void setSelectedRetailer(Retailer selectedRetailer);

    public Date getStartDate() {
        if (startDate == null) {
            startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        }
        return startDate;
    }

    public void setStartDate(Date d) {
        if (d == null) {
            startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        } else {
            this.startDate = d;
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = getEndOfTheDay(new Date());
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = getEndOfTheDay(endDate);
    }

    public Date getToday() {
        if (today == null) {
            today = new Date();
        }
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * helper to get the correct filtered report criteria
     *
     * @param companyMgr
     * @param dmaMgr
     * @param propertyMgr
     * @param retailerMgr
     */
    public void prepareReportCriteria(CompanyManagerLocal companyMgr, DMAManagerLocal dmaMgr, PropertyManagerLocal propertyMgr, RetailerManagerLocal retailerMgr) {
        if (company == null) {
            company = companyMgr.find(this.getHttpSession().getCompany_id());
        }
        if (selectedDMA != null && selectedDMA.getName() != null && !selectedDMA.getName().isEmpty()) {
            selectedDMA = dmaMgr.getDMAByName(selectedDMA.getName());
        } else {
            selectedDMA = null;
        }
        if (selectedProperty != null && selectedProperty.getName() != null && !selectedProperty.getName().isEmpty()) {
            selectedProperty = propertyMgr.getPropertyByCompanyAndName(company, selectedProperty.getName());
        } else {
            selectedProperty = null;
        }
        if (selectedRetailer != null && selectedRetailer.getName() != null && !selectedRetailer.getName().isEmpty()) {
            selectedRetailer = retailerMgr.getRetailerByName(company.getBrand(), selectedRetailer.getName());
        } else {
            selectedRetailer = null;
        }
        startDate = DateUtil.getStartOfDay(getStartDate());
        endDate = getEndOfTheDay(getEndDate());
    }

    public void startDateToFirstOfMonth() {
        setStartDate(getFirstOfMonth(getStartDate()));
    }

    public void startDateToLastOfMonth() {
        setStartDate(getLastOfMonth(getStartDate()));
    }

    public void endDateToFirstOfMonth() {
        setEndDate(getFirstOfMonth(getEndDate()));
    }

    public void endDateToLastOfMonth() {
        setEndDate(getLastOfMonth(getEndDate()));
    }

    private Date getFirstOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date getLastOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int actualMaximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, actualMaximum);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
//    public static void main(String[] args) {
//        Date date = new Date("Wed Sep 01 00:00:00 EDT 2012");
//        if (date == null) {
//            
//        }
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int actualMaximum = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        c.set(Calendar.DAY_OF_MONTH, actualMaximum);
//        c.set(Calendar.HOUR_OF_DAY, 23);
//        c.set(Calendar.MINUTE, 59);
//        c.set(Calendar.SECOND, 59);
//        c.set(Calendar.MILLISECOND, 999);
//        
//        System.out.println(c.getTime());
//    }

    /**
     * Returns the given date with time set to the end of the day
     */
    private Date getEndOfTheDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
