/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms.reports;

import com.proximus.data.Company;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.util.DateUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.util.server.GeoUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
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
@ManagedBean(name = "dashboardController")
@SessionScoped
public class DashboardController extends AbstractController implements Serializable {

    private long totalActiveSubscribersOverSixMonths;
    private long totalActiveSubscribersLastMonth;
    private long totalActiveSubscribersThisMonth;
    private long theoreticalLookupsOverSixMonths;
    private long theoreticalLookupsLastMonth;
    private long theoreticalLookupsCurrentMonth;
    private long actualLookupsOverSixMonths;
    private long actualLookupsLastMonth;
    private long actualLookupsThisMonth;
    private long theoreticalMessagesSentSixMonths;
    private long theoreticalMessagesSentLastMonth;
    private long theoreticalMessagesSentThisMonth;
    private int actualTheoreticalLookupsPercentSixMonths;
    private int actualTheoreticalLookupsPercentLastMonth;
    private int actualTheoreticalLookupsPercentCurrentMonth;
    private int actualTheoreticalMessagesPercentSixMonths;
    private int actualTheoreticalMessagesPercentLastMonth;
    private int actualTheoreticalMessagesPercentCurrentMonth;
    private long actualMessagesSentOverSixMonths;
    private long actualMessagesSentLastMonth;
    private long actualMessagesSentThisMonth;
    private String conversionRateThisMonth;
    private String conversionRateLastMonth;
    private String conversionRateSixMonths;
    private String slope;
    private String intercept;
    private BigDecimal multi = new BigDecimal(100);
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    LocationDataManagerLocal locationMgr;
    @EJB
    MobileOfferSendLogManagerLocal sendLogMgr;
    @EJB
    CompanyManagerLocal companyMgr;
    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());

    public long getTotalActiveSubscribersOverSixMonths() {
        calculateActiveSubscribersOverSixMonths();
        return totalActiveSubscribersOverSixMonths;
    }

    public void setTotalActiveSubscribersOverSixMonths(long totalActiveSubscribersOverSixMonths) {
        this.totalActiveSubscribersOverSixMonths = totalActiveSubscribersOverSixMonths;
    }

    public long getTotalActiveSubscribersLastMonth() {
        if (totalActiveSubscribersLastMonth == 0L) {
            calculateActiveSubscribersLastMonth();
        }
        return totalActiveSubscribersLastMonth;

    }

    public void setTotalActiveSubscribersLastMonth(long totalActiveSubscribersLastMonth) {
        this.totalActiveSubscribersLastMonth = totalActiveSubscribersLastMonth;
    }

    public long getTotalActiveSubscribersThisMonth() {
        if (totalActiveSubscribersThisMonth == 0) {
            calculateActiveSubscribersThisMonth();
        }
        return totalActiveSubscribersThisMonth;
    }

    public void setTotalActiveSubscribersThisMonth(long totalActiveSubscribersThisMonth) {
        this.totalActiveSubscribersThisMonth = totalActiveSubscribersThisMonth;
    }

    public long getTheoreticalLookupsCurrentMonth() {
        if (theoreticalLookupsCurrentMonth == 0L) {
            calculateTheoreticalLookupsThisMonth();
        }
        return theoreticalLookupsCurrentMonth;
    }

    public void setTheoreticalLookupsCurrentMonth(long theoreticalLookupsCurrentMonth) {
        this.theoreticalLookupsCurrentMonth = theoreticalLookupsCurrentMonth;
    }

    public long getTheoreticalLookupsLastMonth() {
        if (theoreticalLookupsLastMonth == 0L) {
            calculateTheoreticalLookupsLastMonth();
        }
        return theoreticalLookupsLastMonth;
    }

    public void setTheoreticalLookupsLastMonth(long theoreticalLookupsLastMonth) {
        this.theoreticalLookupsLastMonth = theoreticalLookupsLastMonth;
    }

    public long getTheoreticalLookupsOverSixMonths() {
        if (theoreticalLookupsOverSixMonths == 0) {
            calculateTheoreticalLookupsOverSixMonths();
        }
        return theoreticalLookupsOverSixMonths;
    }

    public void setTheoreticalLookupsOverSixMonths(long theoreticalLookupsOverSixMonths) {
        this.theoreticalLookupsOverSixMonths = theoreticalLookupsOverSixMonths;
    }

    public long getActualLookupsOverSixMonths() {
        if (actualLookupsOverSixMonths == 0L) {
            calculateActualLookupsOverSixMonths();
        }
        return actualLookupsOverSixMonths;
    }

    public void setActualLookupsOverSixMonths(long actualLookupsOverSixMonths) {
        this.actualLookupsOverSixMonths = actualLookupsOverSixMonths;
    }

    public long getActualLookupsLastMonth() {
        if (actualLookupsLastMonth == 0L) {
            calculateActualLookupsLastMonth();
        }
        return actualLookupsLastMonth;
    }

    public void setActualLookupsLastMonth(long actualLookupsLastMonth) {
        this.actualLookupsLastMonth = actualLookupsLastMonth;
    }

    public long getActualLookupsThisMonth() {
        if (actualLookupsThisMonth == 0L) {
            calculateActualLookupsThisMonth();
        }
        return actualLookupsThisMonth;
    }

    public void setActualLookupsThisMonth(long actualLookupsThisMonth) {
        this.actualLookupsThisMonth = actualLookupsThisMonth;
    }

    public int getActualTheoreticalLookupsPercentCurrentMonth() {
        if (actualTheoreticalLookupsPercentCurrentMonth == 0) {
            calculateActualTheoreticalLookupsPercentageThisMonth();
        }
        return actualTheoreticalLookupsPercentCurrentMonth;
    }

    public void setActualTheoreticalLookupsPercentCurrentMonth(int actualTheoreticalLookupsPercentCurrentMonth) {
        this.actualTheoreticalLookupsPercentCurrentMonth = actualTheoreticalLookupsPercentCurrentMonth;
    }

    public int getActualTheoreticalLookupsPercentLastMonth() {
        if (actualTheoreticalLookupsPercentLastMonth == 0) {
            calculateActualTheoreticalLookupsPercentageLastMonth();
        }
        return actualTheoreticalLookupsPercentLastMonth;
    }

    public void setActualTheoreticalLookupsPercentLastMonth(int actualTheoreticalLookupsPercentLastMonth) {
        this.actualTheoreticalLookupsPercentLastMonth = actualTheoreticalLookupsPercentLastMonth;
    }

    public int getActualTheoreticalLookupsPercentSixMonths() {
        if (actualTheoreticalLookupsPercentSixMonths == 0) {
            calculateActualTheoreticalLookupsPercentageSixMonths();
        }
        return actualTheoreticalLookupsPercentSixMonths;
    }

    public long getTheoreticalMessagesSentLastMonth() {
        if (theoreticalMessagesSentLastMonth == 0L) {
            calculateTheoreticalMessagesSentLastMonth();
        }
        return theoreticalMessagesSentLastMonth;
    }

    public void setTheoreticalMessagesSentLastMonth(long theoreticalMessagesSentLastMonth) {
        this.theoreticalMessagesSentLastMonth = theoreticalMessagesSentLastMonth;
    }

    public long getTheoreticalMessagesSentSixMonths() {
        if (theoreticalMessagesSentSixMonths == 0L) {
            calculateTheoreticalMessagesSentSixMonths();
        }
        return theoreticalMessagesSentSixMonths;
    }

    public void setTheoreticalMessagesSentSixMonths(long theoreticalMessagesSentSixMonths) {
        this.theoreticalMessagesSentSixMonths = theoreticalMessagesSentSixMonths;
    }

    public long getTheoreticalMessagesSentThisMonth() {
        if (theoreticalMessagesSentThisMonth == 0L) {
            calculateTheoreticalMessagesSentThisMonth();
        }
        return theoreticalMessagesSentThisMonth;
    }

    public void setTheoreticalMessagesSentThisMonth(long theoreticalMessagesSentThisMonth) {
        this.theoreticalMessagesSentThisMonth = theoreticalMessagesSentThisMonth;
    }

    public void setActualTheoreticalPercentSixMonths(int actualTheoreticalPercentSixMonths) {
        this.actualTheoreticalLookupsPercentSixMonths = actualTheoreticalPercentSixMonths;
    }

    public long getActualMessagesSentLastMonth() {
        if (actualMessagesSentLastMonth == 0L) {
            calculateActualMessagesSentLastMonth();
        }
        return actualMessagesSentLastMonth;
    }

    public void setActualMessagesSentLastMonth(long actualMessagesSentLastMonth) {
        this.actualMessagesSentLastMonth = actualMessagesSentLastMonth;
    }

    public long getActualMessagesSentOverSixMonths() {
        if (actualMessagesSentOverSixMonths == 0L) {
            calculateActualMessagesSentOverSixMonths();
        }
        return actualMessagesSentOverSixMonths;
    }

    public void setActualMessagesSentOverSixMonths(long actualMessagesSentOverSixMonths) {
        this.actualMessagesSentOverSixMonths = actualMessagesSentOverSixMonths;
    }

    public long getActualMessagesSentThisMonth() {
        if (actualMessagesSentThisMonth == 0L) {
            calculateActualMessagesSentThisMonth();
        }
        return actualMessagesSentThisMonth;
    }

    public void setActualMessagesSentThisMonth(long actualMessagesSentThisMonth) {
        this.actualMessagesSentThisMonth = actualMessagesSentThisMonth;
    }

    public int getActualTheoreticalMessagesPercentCurrentMonth() {
        if (actualTheoreticalMessagesPercentCurrentMonth == 0) {
            calculateActualTheoreticalMessagesPercentageThisMonth();
        }
        return actualTheoreticalMessagesPercentCurrentMonth;
    }

    public void setActualTheoreticalMessagesPercentCurrentMonth(int actualTheoreticalMessagesPercentCurrentMonth) {
        this.actualTheoreticalMessagesPercentCurrentMonth = actualTheoreticalMessagesPercentCurrentMonth;
    }

    public int getActualTheoreticalMessagesPercentLastMonth() {
        if (actualTheoreticalMessagesPercentLastMonth == 0) {
            calculateActualTheoreticalMessagesPercentageLastMonth();
        }
        return actualTheoreticalMessagesPercentLastMonth;
    }

    public void setActualTheoreticalMessagesPercentLastMonth(int actualTheoreticalMessagesPercentLastMonth) {
        this.actualTheoreticalMessagesPercentLastMonth = actualTheoreticalMessagesPercentLastMonth;
    }

    public double getActualTheoreticalMessagesPercentSixMonths() {
        if (actualTheoreticalMessagesPercentSixMonths == 0L) {
            calculateActualTheoreticalMessagesPercentageSixMonths();
        }
        return actualTheoreticalMessagesPercentSixMonths;
    }

    public void setActualTheoreticalMessagesPercentSixMonths(int actualTheoreticalMessagesPercentSixMonths) {
        this.actualTheoreticalMessagesPercentSixMonths = actualTheoreticalMessagesPercentSixMonths;
    }

    public String getConversionRateLastMonth() {
        return conversionRateLastMonth;
    }

    public void setConversionRateLastMonth(String conversionRateLastMonth) {
        this.conversionRateLastMonth = conversionRateLastMonth;
    }

    public String getConversionRateSixMonths() {
        if (conversionRateSixMonths == null) {
            calculateConversionRateSixMonths();
        }
        return conversionRateSixMonths;
    }

    public void setConversionRateSixMonths(String conversionRateSixMonths) {
        this.conversionRateSixMonths = conversionRateSixMonths;
    }

    public String getConversionRateThisMonth() {
        if (conversionRateThisMonth == null) {
            calculateConversionRateThisMonth();
        }
        return conversionRateThisMonth;
    }

    public void setConversionRateThisMonth(String conversionRateThisMonth) {
        this.conversionRateThisMonth = conversionRateThisMonth;
    }

    public String getIntercept() {
        if (intercept == null) {
            retreiveSlopeIntercept();
        }
        return intercept;
    }

    public void setIntercept(String intercept) {
        this.intercept = intercept;
    }

    public String getSlope() {
        if (slope == null) {
            retreiveSlopeIntercept();
        }
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getGeofenceSize(int dayOfMonth) {
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        double size = GeoUtil.calculateRadius(settings, 3.0, dayOfMonth);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(size);
    }

    public String prepareDashboard() {
//        selectedGeoCompany =  companyMgr.getCompanybyId(this.getCompanyIdFromSession());
        logger.info("in prepareDashboard()");
        calculateActiveSubscribersOverSixMonths();
        calculateActiveSubscribersLastMonth();
        calculateActiveSubscribersThisMonth();

        //theoretical lookups
        calculateTheoreticalLookupsOverSixMonths();
        calculateTheoreticalLookupsLastMonth();
        calculateTheoreticalLookupsThisMonth();

        //actual lookups
        calculateActualLookupsOverSixMonths();
        calculateActualLookupsLastMonth();
        calculateActualLookupsThisMonth();

        //actual/theoretical lookups
        calculateActualTheoreticalLookupsPercentageSixMonths();
        calculateActualTheoreticalLookupsPercentageLastMonth();
        calculateActualTheoreticalLookupsPercentageThisMonth();

        //actual messages
        calculateActualMessagesSentOverSixMonths();
        calculateActualMessagesSentLastMonth();
        calculateActualMessagesSentThisMonth();

        //theoretical messages
        calculateTheoreticalMessagesSentSixMonths();
        calculateTheoreticalMessagesSentLastMonth();
        calculateTheoreticalMessagesSentThisMonth();

        //actual/theoretical messages
        calculateActualTheoreticalMessagesPercentageSixMonths();
        calculateActualTheoreticalMessagesPercentageLastMonth();
        calculateActualTheoreticalMessagesPercentageThisMonth();

        //conversion rates
        calculateConversionRateSixMonths();
        calculateConversionRateLastMonth();
        calculateConversionRateThisMonth();

        return "/geo-reports/Dashboard?faces-redirect=true";
    }

    public void updateDashboard() {
        prepareDashboard();
    }

    private void calculateActiveSubscribersOverSixMonths() {
        Calendar current = Calendar.getInstance();
        Date currentDate = current.getTime();
        //get date six months ago
        current.add(Calendar.MONTH, -6);
        Date sixMonthDate = current.getTime();
        totalActiveSubscribersOverSixMonths = 0L;
        long total = 0L;
        logger.info("six month date is " + sixMonthDate + "and current date is " + currentDate);
        try {
            totalActiveSubscribersOverSixMonths = subscriberMgr.findOptedInCountByBrandAndDate(this.getBrandFromSession(), sixMonthDate, currentDate);
        } catch (Exception ex) {
            logger.error("exception getting active subscribers over six months. " + ex);
        }


    }

    private long calculateActiveSubscribersOverSixMonthsByCompany(Company geoCompany) {
        Calendar current = Calendar.getInstance();
        Date currentDate = current.getTime();

        //get date six months ago
        current.add(Calendar.MONTH, -6);
        Date sixMonthDate = current.getTime();
        long total = 0;
        logger.info("current date is " + currentDate + " and six months ago is " + sixMonthDate);
        if (geoCompany == null) {
            return total;
        } else {
            try {
                total = subscriberMgr.findOptedInCountByCompanyAndDate(geoCompany, sixMonthDate, currentDate);
            } catch (Exception e) {
                total = 0L;
                logger.info("error getting total active subs over six months" + e);
                return total;
            }

        }
        return total;

    }

    private long calculateActiveSubscribersLastMonthByCompany(Company geoCompany) {
        Calendar current = Calendar.getInstance();
        current.add(Calendar.MONTH, -1);
        Date firstDayOfLastMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date lastDayOfLastMonth = DateUtil.getLastDayOfMonth(current.getTime());
        long total = 0;
        if (geoCompany == null) {
            return total;
        } else {
            try {
                total = subscriberMgr.findOptedInCountByCompanyAndDate(geoCompany, firstDayOfLastMonth, lastDayOfLastMonth);
            } catch (Exception e) {
                total = 0L;
                logger.info("error getting total active subs" + e);
                return total;
            }

        }
        return total;

    }

    private long calculateActiveSubscribersThisMonthByCompany(Company geoCompany) {
        Calendar current = Calendar.getInstance();
        Date firstDayOfThisMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date currentDayOfThisMonth = current.getTime();
        long total = 0;
        if (geoCompany == null) {
            return total;
        } else {
            try {
                total = subscriberMgr.findOptedInCountByCompanyAndDate(geoCompany, firstDayOfThisMonth, currentDayOfThisMonth);
            } catch (Exception e) {
                total = 0L;
                logger.info("error getting total active subs" + e);
                return total;
            }

        }
        return total;

    }

    private void calculateActiveSubscribersLastMonth() {
        Calendar current = Calendar.getInstance();

        current.add(Calendar.MONTH, -1);
        Date firstDayOfLastMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date lastDayOfLastMonth = DateUtil.getLastDayOfMonth(current.getTime());
        totalActiveSubscribersLastMonth = 0L;
        try {
            totalActiveSubscribersLastMonth = subscriberMgr.findOptedInCountByBrandAndDate(this.getBrandFromSession(), firstDayOfLastMonth, lastDayOfLastMonth);
        } catch (Exception e) {
            logger.info("error getting totalActiveSubscribersLastMonth" + e);
        }

        logger.info("total active subscribers for last month: " + totalActiveSubscribersLastMonth);

    }

    private void calculateActiveSubscribersThisMonth() {
        Calendar current = Calendar.getInstance();
        Date firstDayOfThisMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date currentDayOfThisMonth = current.getTime();
        totalActiveSubscribersThisMonth = 0L;
        try {
            totalActiveSubscribersThisMonth = subscriberMgr.findTotalOptedInCountByBrandAndDate(this.getBrandFromSession(), currentDayOfThisMonth);
        } catch (Exception ex) {
            logger.error("exception getting active subscribers this month. " + ex);
        }

    }

    //do I want to use max lookups per month or max lookups per month per subscriber
    private void calculateTheoreticalLookupsOverSixMonths() {
        theoreticalLookupsOverSixMonths = 0L;
        int maxLookupsPerSub = 0;
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        if (settings != null) {
            maxLookupsPerSub = settings.getMaxLookupsPerMonthPerSubscriber();
        }
        calculateActiveSubscribersOverSixMonths();
        theoreticalLookupsOverSixMonths = maxLookupsPerSub * totalActiveSubscribersOverSixMonths;

    }

    private void calculateTheoreticalLookupsLastMonth() {
        theoreticalLookupsLastMonth = 0;
        int maxLookupsPerSub = 0;
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        if (settings != null) {
            maxLookupsPerSub = settings.getMaxLookupsPerMonthPerSubscriber();
        }
        calculateActiveSubscribersLastMonth();
        theoreticalLookupsLastMonth = maxLookupsPerSub * totalActiveSubscribersLastMonth;



    }

    private void calculateTheoreticalLookupsThisMonth() {
        theoreticalLookupsCurrentMonth = 0L;
        int maxLookupsPerSub = 0;
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        if (settings != null) {
            maxLookupsPerSub = settings.getMaxLookupsPerMonthPerSubscriber();
        }
        calculateActiveSubscribersThisMonth();
        theoreticalLookupsCurrentMonth = maxLookupsPerSub * totalActiveSubscribersThisMonth;


    }

    private void calculateActualLookupsOverSixMonths() {
        Calendar current = Calendar.getInstance();
        Date currentDate = current.getTime();
        //get date six months ago
        current.add(Calendar.MONTH, -6);
        Date sixMonthDate = current.getTime();
        actualLookupsOverSixMonths = 0L;
        try {
            actualLookupsOverSixMonths = locationMgr.getTotalLookupsInDateRangeByBrand(sixMonthDate, currentDate, this.getBrandFromSession(), "found");
        } catch (Exception e) {
            logger.info("error getting actual lookups over six months: " + e);
        }



    }

    /**
     * with lookup status = found
     */
    private void calculateActualLookupsLastMonth() {
        Calendar current = Calendar.getInstance();
        current.add(Calendar.MONTH, -1);
        Date firstDayOfLastMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date lastDayOfLastMonth = DateUtil.getLastDayOfMonth(current.getTime());
        actualLookupsLastMonth = 0L;
        try {
            actualLookupsLastMonth = locationMgr.getTotalLookupsInDateRangeByBrand(firstDayOfLastMonth, lastDayOfLastMonth, this.getBrandFromSession(), "found");
        } catch (Exception e) {
            actualLookupsLastMonth = 0;
            logger.info("error in actual lookups last month: " + e);
        }
        logger.info("actual lookups last month: " + actualLookupsLastMonth);
    }

    private void calculateActualLookupsThisMonth() {
        Calendar current = Calendar.getInstance();
        Date firstDayOfThisMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date currentDayOfThisMonth = current.getTime();
        actualLookupsThisMonth = 0L;
        try {
            actualLookupsThisMonth = locationMgr.getTotalLookupsInDateRangeByBrand(firstDayOfThisMonth, currentDayOfThisMonth, this.getBrandFromSession(), "found");
        } catch (Exception e) {
            logger.info("error getting actual lookups over six months: " + e);


        }
    }

    private void calculateActualTheoreticalLookupsPercentageSixMonths() {
        logger.info("actualLookupsOverSixMonths: " + actualLookupsOverSixMonths);
        logger.info("theoreticalLookupsOverSixMonths: " + theoreticalLookupsOverSixMonths);

        try {
            BigDecimal numerator = new BigDecimal(actualLookupsOverSixMonths);
            BigDecimal denominator = new BigDecimal(theoreticalLookupsOverSixMonths);
            actualTheoreticalLookupsPercentSixMonths = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
            logger.info("actual/theoretical lookups: " + actualTheoreticalLookupsPercentSixMonths);
        } catch (ArithmeticException ex) {
            actualTheoreticalLookupsPercentSixMonths = 0;
            logger.info("actual/theoretical lookups exception: " + ex);

        }
    }

    private void calculateActualTheoreticalLookupsPercentageLastMonth() {
        try {
            BigDecimal numerator = new BigDecimal(actualLookupsLastMonth);
            BigDecimal denominator = new BigDecimal(theoreticalLookupsLastMonth);
            actualTheoreticalLookupsPercentLastMonth = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
        } catch (ArithmeticException ex) {
            actualTheoreticalLookupsPercentLastMonth = 0;
            logger.info("actual/theoretical lookups last month exception: " + ex);
        }
    }

    private void calculateActualTheoreticalLookupsPercentageThisMonth() {
        try {
            BigDecimal numerator = new BigDecimal(actualLookupsThisMonth);
            BigDecimal denominator = new BigDecimal(theoreticalLookupsCurrentMonth);
            actualTheoreticalLookupsPercentCurrentMonth = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
        } catch (ArithmeticException ex) {
            actualTheoreticalLookupsPercentCurrentMonth = 0;
            logger.info("actual/theoretical lookups this month exception: " + ex);
        }

    }

    private void calculateTheoreticalMessagesSentSixMonths() {
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        int maxMsgsPerSub = settings.getMaxMessagesPerSubscriberPerMonth();
        calculateActiveSubscribersOverSixMonths();
        theoreticalMessagesSentSixMonths = maxMsgsPerSub * totalActiveSubscribersOverSixMonths;
    }

    private void calculateTheoreticalMessagesSentLastMonth() {
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        int maxMsgsPerSub = settings.getMaxMessagesPerSubscriberPerMonth();
        if (totalActiveSubscribersLastMonth == 0L) {
            calculateActiveSubscribersLastMonth();
        }
        theoreticalMessagesSentLastMonth = maxMsgsPerSub * totalActiveSubscribersLastMonth;
    }

    private void calculateTheoreticalMessagesSentThisMonth() {
        MobileOfferSettings settings = settingsMgr.findByBrand(this.getBrandFromSession());
        int maxMsgsPerSub = settings.getMaxMessagesPerSubscriberPerMonth();
        if (totalActiveSubscribersThisMonth == 0L) {
            calculateActiveSubscribersThisMonth();
        }
        theoreticalMessagesSentThisMonth = maxMsgsPerSub * totalActiveSubscribersThisMonth;
    }

    private void calculateActualMessagesSentThisMonth() {
        Calendar current = Calendar.getInstance();
        Date firstDayOfThisMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date currentDayOfThisMonth = current.getTime();
        try {
            actualMessagesSentThisMonth = sendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndBrand(firstDayOfThisMonth, currentDayOfThisMonth, this.getBrandFromSession(), "delivered");
        } catch (Exception e) {
            actualMessagesSentThisMonth = 0L;
            logger.info("error in actual messages sent this month: " + e);
        }

    }

    private void calculateActualMessagesSentLastMonth() {
        Calendar current = Calendar.getInstance();
        current.add(Calendar.MONTH, -1);
        Date firstDayOfLastMonth = DateUtil.getFirstDayOfMonth(current.getTime());
        Date lastDayOfLastMonth = DateUtil.getLastDayOfMonth(current.getTime());
        try {
            actualMessagesSentLastMonth = sendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndBrand(firstDayOfLastMonth, lastDayOfLastMonth, this.getBrandFromSession(), "delivered");
        } catch (Exception e) {
            logger.info("error in actual messages sent this month: " + e);
        }

    }

    private void calculateActualMessagesSentOverSixMonths() {
        Calendar current = Calendar.getInstance();
        Date currentDate = current.getTime();
        //get date six months ago
        current.add(Calendar.MONTH, -6);
        Date sixMonthDate = current.getTime();
        try {
            actualMessagesSentOverSixMonths = sendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndBrand(sixMonthDate, currentDate, this.getBrandFromSession(), "delivered");
        } catch (Exception e) {
            logger.info("error in actual messages sent over six months: " + e);
        }
    }

    private void calculateActualTheoreticalMessagesPercentageSixMonths() {
        try {
            BigDecimal numerator = new BigDecimal(actualMessagesSentOverSixMonths);
            BigDecimal denominator = new BigDecimal(theoreticalMessagesSentSixMonths);
            actualTheoreticalMessagesPercentSixMonths = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
        } catch (ArithmeticException ex) {
            actualTheoreticalMessagesPercentSixMonths = 0;
            logger.info("error getting percentage actual/theoretical messages six months: " + ex);
        }

    }

    private void calculateActualTheoreticalMessagesPercentageLastMonth() {
        try {
            BigDecimal numerator = new BigDecimal(actualMessagesSentLastMonth);
            BigDecimal denominator = new BigDecimal(theoreticalMessagesSentLastMonth);
            actualTheoreticalMessagesPercentLastMonth = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
        } catch (ArithmeticException ex) {
            actualTheoreticalMessagesPercentLastMonth = 0;
            logger.info("error getting percentage actual/theoretical messages last month: " + ex);

        }
    }

    private void calculateActualTheoreticalMessagesPercentageThisMonth() {
        try {
            BigDecimal numerator = new BigDecimal(actualMessagesSentThisMonth);
            BigDecimal denominator = new BigDecimal(theoreticalMessagesSentThisMonth);
            actualTheoreticalMessagesPercentCurrentMonth = numerator.multiply(multi).divide(denominator, 2, RoundingMode.HALF_UP).intValue();
        } catch (ArithmeticException ex) {
            actualTheoreticalMessagesPercentCurrentMonth = 0;
            logger.info("error getting percentage actual/theoretical messages this month: " + ex);
        }
    }

    private void calculateConversionRateThisMonth() {
        conversionRateThisMonth = "";
        if (actualMessagesSentThisMonth == 0L) {
            calculateActualMessagesSentThisMonth();
        }
        if (actualLookupsThisMonth == 0L) {
            calculateActualLookupsThisMonth();
        }
        logger.info("actual messages: " + actualMessagesSentThisMonth);
        try {
            BigDecimal numerator = new BigDecimal(actualLookupsThisMonth);
            BigDecimal denominator = new BigDecimal(actualMessagesSentThisMonth);
            int factor = numerator.divide(denominator, 0, RoundingMode.HALF_UP).intValue();
            conversionRateThisMonth = factor + ":1";
        } catch (ArithmeticException ex) {
            conversionRateThisMonth = "0:0";
            logger.info("conversion rate this month exception: " + ex);

        }
    }

    private void calculateConversionRateLastMonth() {
        conversionRateLastMonth = "";
        if (actualMessagesSentLastMonth == 0L) {
            calculateActualMessagesSentLastMonth();
        }
        if (actualLookupsLastMonth == 0L) {
            calculateActualMessagesSentLastMonth();
        }
        try {
            BigDecimal numerator = new BigDecimal(actualLookupsLastMonth);
            BigDecimal denominator = new BigDecimal(actualMessagesSentLastMonth);
            int factor = numerator.divide(denominator, 0, RoundingMode.HALF_UP).intValue();
            conversionRateLastMonth = factor + ":1";
        } catch (ArithmeticException ex) {
            conversionRateLastMonth = "0:0";
            logger.info("conversion rate this month exception: " + ex);

        }
    }

    private void calculateConversionRateSixMonths() {
        conversionRateSixMonths = "";
        if (actualMessagesSentOverSixMonths == 0L) {
            calculateActualMessagesSentOverSixMonths();
        }
        if (actualLookupsOverSixMonths == 0L) {
            calculateActualMessagesSentOverSixMonths();
        }
        logger.info("" + actualLookupsOverSixMonths);
        logger.info("" + actualMessagesSentOverSixMonths);
        try {
            BigDecimal numerator = new BigDecimal(actualLookupsOverSixMonths);
            BigDecimal denominator = new BigDecimal(actualMessagesSentOverSixMonths);
            int factor = numerator.divide(denominator, 0, RoundingMode.HALF_UP).intValue();
            logger.info("" + factor);
            conversionRateSixMonths = factor + ":1";
        } catch (ArithmeticException ex) {
            conversionRateSixMonths = "0:0";
            logger.info("conversion rate six month exception: " + ex);

        }
    }

    private void retreiveSlopeIntercept() {
        MobileOfferSettings mos = settingsMgr.findByBrand(this.getBrandFromSession());
        if (mos != null) {
            slope = Double.toString(mos.getGeofenceDateSizingSlope());
            intercept = Double.toString(mos.getGeofenceDateSizingIntercept());


        }

    }

    @FacesConverter(forClass = Company.class, value = "dashboardControllerConverter")
    public static class DashboardControllerConverter implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);
                Company controller = ((DashboardController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "dashboardController")).companyMgr.find(id);
                return controller;
            } catch (NumberFormatException e) {
                //throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Value not found"));
                throw new ConverterException();
            }
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
            if (value == null || value.toString().isEmpty() || !(value instanceof Company)) {
                return null;
            }
            return String.valueOf(((Company) value).getId());
        }
    }
}