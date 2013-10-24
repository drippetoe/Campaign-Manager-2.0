/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.startup.event;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.Role;
import com.proximus.data.User;
import com.proximus.data.events.EventSubscription;
import com.proximus.data.events.EventType;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.RoleManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.events.EventManagerLocal;
import com.proximus.manager.events.EventTypeManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.manager.sms.report.ViewOptInsByMonthManagerLocal;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@Singleton
@LocalBean
public class SuperUserDigest {

    private static final Logger logger = Logger.getLogger(SuperUserDigest.class.getName());
    private DateTimeFormatter dateFmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter dayFmt = DateTimeFormat.forPattern("EE MM/dd");
    private DateTimeFormatter monthFmt = DateTimeFormat.forPattern("MMMM");
    private DateTimeFormatter timeFmt = DateTimeFormat.forPattern("hh:mm");
    public static final EventType SUPER_USER_DIGEST = new EventType("SUPER_USER_DIGEST", License.LICENSE_GEOFENCE);
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private UserManagerLocal userMgr;
    @EJB
    private RoleManagerLocal roleMgr;
    @EJB
    private EventManagerLocal eventMgr;
    @EJB
    private EventTypeManagerLocal eventTypeMgr;
    @EJB
    private MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    @EJB
    private ViewOptInsByMonthManagerLocal viewOptInsByMonthMgr;
    @EJB
    private SubscriberManagerLocal subscriberManager;
    @EJB
    private LocationDataManagerLocal locationDataMgr;

    private void sendMail(User user, String subject, String msg) {
        try {
            DateTime now = new DateTime();
            HtmlEmail email = new HtmlEmail();
            email.setHostName("localhost");
            email.addTo(user.getUserName(), user.toString());
            //email.addTo("ejohansson@proximusmobility.com", "Eric Johansson");
            email.setFrom("daily-digest@proximusmobility.com", "Proximus Mobility, LLC");
            email.setSubject(subject);
            // embed the image and get the content id
            File image = new File("/home/eric/proximus.png");
            String cid = email.embed(image, "Proximus Logo");
            String style = "<style>th,td{border:1px solid black;} th{text-align:left; hr{width:90%;height:1px; color: red;}</style><html><body style=\"font-family:Arial;\"><img src=\"cid:" + cid + "\"><br/>\n";
            String header = "<h1>" + subject + " - " + dayFmt.print(now) + "</h1><hr/>";
            String footer = "<hr/><p>Proximus Mobility, LLC</p>";
            msg = style + header + msg + footer + "</body></html>";
            email.setHtmlMsg(msg);
            email.send();
        } catch (EmailException ex) {
            logger.error(ex);
        } catch (Exception ex) {
            logger.error(ex);
        }

    }

    /**
     * Send an email every 5 min.
     */
    @Schedule(minute = "*/5", hour = "*")
    public void sendRawDigest() {
        eventTypeMgr.update(SUPER_USER_DIGEST);
        Role superUser = roleMgr.findByName("Super User");
        List<Company> companies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);
        if (companies != null) {
            for (Company company : companies) {
                /*
                 * Generate data
                 */
                String data = getMobileOfferSummary(company);
                data += getRegistrationSummary(company);
                data += getLocaidSummary(company.getBrand());
                /*
                 * Send email to super users only
                 */
                List<User> users = userMgr.getUsersByRole(company, superUser);
                for (User user : users) {
                    //System.out.println("Company: " + company.getName() + " User: " + user);
                    EventSubscription eventSubscription = user.getEventSubscriptionByType(SUPER_USER_DIGEST);

                    if (eventSubscription != null) {
                        //System.out.println("  " + eventSubscription.getEventType().getId());
                        if (eventSubscription.hasSubscription(EventSubscription.ALERT_RAW)) {
                            logger.info("Sending Super User Raw Digest Email to: " + user);
                            System.out.println("Sending Super User Raw Digest Email to: " + user);
                            sendMail(user, "Raw Digest for " + company.getName(), data);
                        }
                    }
                }
            }
        }
    }

    /**
     * Send an email every Day reporting on yesterdays data. 8am EST
     */
    @Schedule(minute = "0", hour = "13", dayOfWeek = "*")
    public void sendDailyDigest() {
        eventTypeMgr.update(SUPER_USER_DIGEST);
        Role superUser = roleMgr.findByName("Super User");
        List<Company> companies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);
        if (companies != null) {
            for (Company company : companies) {
                /*
                 * Generate data
                 */
                String data = getMobileOfferSummary(company);
                data += getRegistrationSummary(company);
                data += getLocaidSummary(company.getBrand());
                /*
                 * Send email to super users only
                 */
                List<User> users = userMgr.getUsersByRole(company, superUser);
                for (User user : users) {
                    //System.out.println("Company: " + company.getName() + " User: " + user);
                    EventSubscription eventSubscription = user.getEventSubscriptionByType(SUPER_USER_DIGEST);

                    if (eventSubscription != null) {
                        //System.out.println("  " + eventSubscription.getEventType().getId());
                        if (eventSubscription.hasSubscription(EventSubscription.ALERT_DAILY)) {
                            logger.info("Sending Super User Daily Digest email to: " + user);
                            System.out.println("Sending Super User Daily Digest email to: " + user);
                            sendMail(user, "Daily Digest for " + company.getName(), data);
                        }
                    }
                }
            }
        }
    }

    /**
     * Send an email every Monday reporting on lest weeks data. 8am EST
     */
    @Schedule(minute = "0", hour = "13", dayOfWeek = "1", month = "*")
    public void sendWeeklyDigest() {
        logger.info("Implement sendWeeklyDigest");
    }

    /**
     * Send an email every last of the month reporting on the data. 8am EST
     */
    @Schedule(minute = "0", hour = "13", dayOfWeek = "*", dayOfMonth = "Last")
    public void sendMonthlyDigest() {
        logger.info("Implement sendMonthlyDigest");
    }

    public StringBuilder spanColorData(StringBuilder sb, long data) {
        /*sb.append("<span");
         if (data <= 0) {
         sb.append(" style='color:black;'");
         } else if (data > 10) {
         sb.append(" style='color:green;'");
         }

         sb.append(">").append(data).append("</span>");*/
        sb.append(data);
        return sb;
    }

    public StringBuilder singleValueData(StringBuilder sb, String header, long data) {
        sb.append("<li>").append(header).append(": ");
        sb = spanColorData(sb, data);
        sb.append("</li>\n");
        return sb;
    }

    /**
     * @param args the command line arguments
     */
    public String getMobileOfferSummary(Company company) {
        StringBuilder sb = new StringBuilder("");
        DateTime today = new DateTime(new Date());
        long totalThisMonth = mobileOfferSendLogMgr.findSendLogsByCompanyAndDate(company, DateUtil.getFirstDayOfMonth(today.toDate()), DateUtil.getLastDayOfMonth(today.toDate()));

        sb.append("<h3>Offers sent over the past 7 Days</h3>\n");
        sb.append("<table>\n");
        StringBuilder th = new StringBuilder();
        StringBuilder td = new StringBuilder();
        for (int i = 7; i > 0; i--) {
            long sentTotal = mobileOfferSendLogMgr.getTotalMessagesSentByCompanyAndDateRange(company, DateUtil.getStartOfDay(today.minusDays(i).toDate()), DateUtil.getEndOfDay(today.minusDays(i).toDate()));
            th.append("<th>");
            if (i > 1) {
                th.append(dayFmt.print(today.minusDays(i)));
            } else {
                th.append("Yesterday");
            }
            th.append("</th>\n");
            td.append("<td>");
            td = spanColorData(td, sentTotal);
            td.append("</td>\n");
        }
        sb.append("<tr>").append(th.toString()).append("</tr>");
        sb.append("<tr>").append(td.toString()).append("</tr>");
        sb.append("<tr><th colspan='6'>Total this month:</th><td>").append(totalThisMonth).append("</td></tr>");
        sb.append("</table>\n");
        
        //Offers pending
        long totalPendingThisMonth = mobileOfferSendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndCompany(DateUtil.getFirstDayOfMonth(today.toDate()), DateUtil.getLastDayOfMonth(today.toDate()), company, MobileOfferSendLog.STATUS_PENDING);
        sb.append("<h3>Offers Pending for the past 7 Days</h3>\n");
        sb.append("<table>\n<br/>\n");
        th = new StringBuilder();
        td = new StringBuilder();
        for (int i = 7; i > 0; i--) {
            long sentTotal = mobileOfferSendLogMgr.getTotalMessagesSentByDateRangeAndStatusAndCompany(DateUtil.getStartOfDay(today.minusDays(i).toDate()), DateUtil.getEndOfDay(today.minusDays(i).toDate()), company, MobileOfferSendLog.STATUS_PENDING);
            th.append("<th>");
            if (i > 1) {
                th.append(dayFmt.print(today.minusDays(i)));
            } else {
                th.append("Yesterday");
            }
            th.append("</th>\n");
            td.append("<td>");
            td = spanColorData(td, sentTotal);
            td.append("</td>\n");
        }
        sb.append("<tr>").append(th.toString()).append("</tr>");
        sb.append("<tr>").append(td.toString()).append("</tr>");
        sb.append("<tr><th colspan='6'>Total this month:</th><td>").append(totalPendingThisMonth).append("</td></tr>");
        sb.append("</table>\n");


        return sb.toString();
    }

    public String getRegistrationSummary(Company company) {
        StringBuilder sb = new StringBuilder();
        DateTime today = new DateTime();
        /*
         * Do stuff here
         */
        long totalActiveSubscribers = subscriberManager.findAllByCompanyAndStatus(company, Subscriber.STATUS_OPT_IN_COMPLETE).size();
        long totalInactiveSubscribers = subscriberManager.findAllByCompanyAndStatus(company, Subscriber.STATUS_OPT_OUT).size();
        long totalUnsupportedSubscribers = subscriberManager.findAllByCompanyAndStatus(company, Subscriber.STATUS_CARRIER_UNSUPPORTED).size();

        long totalMonth = subscriberManager.countOptInsForMonth(company, today.toDate());
        long totalYesterday = subscriberManager.countOptInsForOneDay(company, today.minusDays(1).toDate());
        /*
         * Generate body
         */
        sb.append("<h1>Subscribers</h1>\n");
        sb.append("<h3>OptIns over the past 7 Days</h3>\n");
        sb.append("<table>\n");
        StringBuilder th = new StringBuilder();
        StringBuilder td = new StringBuilder();
        for (int i = 7; i > 0; i--) {
            long sentTotal = subscriberManager.countOptInsForOneDay(company, today.minusDays(i).toDate());
            th.append("<th>");
            if (i > 1) {
                th.append(dayFmt.print(today.minusDays(i)));
            } else {
                th.append("Yesterday");
            }
            th.append("</th>\n");
            td.append("<td>");
            td = spanColorData(td, sentTotal);
            td.append("</td>\n");
        }
        sb.append("<tr>").append(th.toString()).append("</tr>");
        sb.append("<tr>").append(td.toString()).append("</tr>");

        sb.append("</table>\n");

        sb.append("<h3>Overall Totals</h3><table>\n");
        //sb.append("<tr><th colspan='2'>Overall Totals</th></tr>");
        sb.append("<tr><td>OptIns this month:</th><td>").append(totalMonth).append("</td></tr>");
        sb.append("<tr><td>Active Subscribers:</td><td>").append(totalActiveSubscribers).append("</td></tr>");
        sb.append("<tr><td>Inactive Subscribers:</td><td>").append(totalInactiveSubscribers).append("</td></tr>");
        sb.append("<tr><td>Unsupported Subscribers:</td><td>").append(totalUnsupportedSubscribers).append("</td></tr>");
        sb.append("</table>\n");
        return sb.toString();
    }

    public String getLocaidSummary(Brand brand) {
        StringBuilder sb = new StringBuilder();
        DateTime today = new DateTime();
        Date startOfMonth = DateUtil.getFirstDayOfMonth(today.toDate());
        Date lastOfMonth = DateUtil.getLastDayOfMonth(today.toDate());
        long totalThisMonth = locationDataMgr.getTotalLookupsInDateRangeByBrand(startOfMonth, lastOfMonth, brand, LocationData.STATUS_FOUND);
        sb.append("<h1>Loc-Aid</h1>\n");
        sb.append("<h3>Lookups over the past 7 Days</h3>\n");
        sb.append("<table>\n");
        StringBuilder th = new StringBuilder();
        StringBuilder td = new StringBuilder();
        for (int i = 7; i > 0; i--) {
            startOfMonth = DateUtil.getStartOfDay(today.minusDays(i).toDate());
            lastOfMonth = DateUtil.getEndOfDay(today.minusDays(i).toDate());
            long totalLookups = locationDataMgr.getTotalLookupsInDateRangeByBrand(startOfMonth, lastOfMonth, brand, LocationData.STATUS_FOUND);
            th.append("<th>");
            if (i > 1) {
                th.append(dayFmt.print(today.minusDays(i)));
            } else {
                th.append("Yesterday");
            }
            th.append("</th>\n");
            td.append("<td>");
            td = spanColorData(td, totalLookups);
            td.append("</td>\n");
        }
        sb.append("<tr>").append(th.toString()).append("</tr>");
        sb.append("<tr>").append(td.toString()).append("</tr>");
        sb.append("<tr><th colspan='6'>Total this month:</th><td>").append(totalThisMonth).append("</td></tr>");
        sb.append("</table>\n");
        return sb.toString();
    }
}
