/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.rest;

import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.User;
import com.proximus.manager.*;
import com.proximus.manager.report.WifiDaySummaryManagerLocal;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.manager.sms.report.MobileOfferSendLogReportManagerLocal;
import com.proximus.manager.sms.report.RegistrationBySourceReportManagerLocal;
import com.proximus.manager.sms.report.RegistrationReportManagerLocal;
import com.proximus.startup.warehouse.WifiDaySummaryCreator;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Path("/statsCalc/{start_date}/{end_date}/{company}/{user_name}/{token}")
@RequestScoped
public class StatisticsCalculateREST {

    @Context
    private UriInfo context;
    @EJB
    private DeviceManagerLocal deviceMgr;
    @EJB
    private CampaignManagerLocal campaignMgr;
    @EJB
    private WifiLogManagerLocal wifiLogMgr;
    @EJB
    private WifiRegistrationManagerLocal wifiRegMgr;
    @EJB
    private BluetoothReportManagerLocal btReportMgr;
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private UserProfileSummaryManagerLocal userProfileMgr;
    @EJB
    private BluetoothDwellReportManagerLocal btDwellReportMgr;
    @EJB
    RegistrationReportManagerLocal registrationMgr;
    @EJB
    MobileOfferSendLogReportManagerLocal mobileOfferSendLogReportMgr;
    @EJB
    KeywordManagerLocal keywordMgr;
    @EJB
    RegistrationBySourceReportManagerLocal registrationSourceMgr;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    WifiDaySummaryManagerLocal wifiDaySummaryMgr;

    @EJB
    UserManagerLocal userMgr;
    private static final Logger logger = Logger.getLogger(StatisticsCalculateREST.class.getName());
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/statsCalc/";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response calculateChecksum(@PathParam("start_date") String startDateStr, @PathParam("end_date") String endDateStr,
            @PathParam("company") Long companyId, @PathParam("user_name") String userName, @PathParam("token") String requestToken) {
        if (userName != null && requestToken != null) {
            try {
                userName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                return Response.status(Response.Status.BAD_REQUEST).entity("CANNOT DECODE USERNAME").build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity("USERNAME AND TOKEN CANNOT BE EMPTY").build();
        }
        User user = userMgr.getUserByUsername(userName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            return Response.status(Response.Status.BAD_REQUEST).entity("NO USER IS FOUND").build();
        }
        String password = user.getApiPassword();
        String requestURL = BASE_URL + userName + "/" + password;
        Company company = null;
        String companySalt = null;
        if (companyId != null && companyId != 0) {
            company = companyMgr.find(companyId);
        }
        if (company != null) {
            companySalt = company.getSalt();
        }
        logger.warn("url to encode: " + requestURL);
        logger.warn("campany SALT: " + companySalt);
        if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date startRange = null;
            Date endRange = null;

            try {
                startRange = sdf.parse(startDateStr);
                endRange = sdf.parse(endDateStr);
            } catch (ParseException ex) {
                logger.error(ex);
            }
            if (startRange == null || endRange == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("BAD DATE FORMAT").build();
            }

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(startRange);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            List<Date> dateRange = new ArrayList<Date>();

            Date currentDate = cal.getTime();

            while (currentDate.getTime() < endRange.getTime()) {
                dateRange.add(currentDate);
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
                currentDate = cal.getTime();
            }

            if (dateRange.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("NO DATES IN THAT RANGE").build();
            }

            String msgBody = " for: ";

            List<Company> proximityCompanies = new ArrayList<Company>();
            List<Company> geoFenceCompanies = new ArrayList<Company>();


            if (company != null && company.getLicense().hasProximity()) {
                proximityCompanies.add(company);
                msgBody += "\nProximity company " + company.getName() + " (" + company.getId() + ")";
            }
            if (company != null && company.getLicense().hasGeofence()) {
                geoFenceCompanies.add(company);
                msgBody += "\nGeoFence company " + company.getName() + " (" + company.getId() + ")";
            }


            if (proximityCompanies.size() < 1 && geoFenceCompanies.size() < 1) {
                proximityCompanies = companyMgr.findCompaniesWithLicense(License.LICENSE_PROXIMITY);
                geoFenceCompanies = companyMgr.findCompaniesWithLicense(License.LICENSE_GEOFENCE);
            }

            if (proximityCompanies.size() > 0) {
                WifiDaySummaryCreator summaryCreator = new WifiDaySummaryCreator();
                summaryCreator.setBtDwellReportMgr(btDwellReportMgr);
                summaryCreator.setBtReportMgr(btReportMgr);
                summaryCreator.setCampaignMgr(campaignMgr);
                summaryCreator.setCompanyMgr(companyMgr);
                summaryCreator.setDeviceMgr(deviceMgr);
                summaryCreator.setUserProfileMgr(userProfileMgr);
                summaryCreator.setWifiLogMgr(wifiLogMgr);
                summaryCreator.setWifiRegMgr(wifiRegMgr);
                summaryCreator.setWifiDaySummaryMgr(wifiDaySummaryMgr);
                summaryCreator.generateStatisticsForDateRange(dateRange, proximityCompanies);
            }
            
            return Response.status(Response.Status.OK).entity("Range done" + msgBody).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication Failed with these parameters:" + "\nApi Password: "
                    + password + "\nRequest Token: " + requestToken + "\nRequest Url: " + requestURL + "\nCompany Salt: " + companySalt).build();
        }
    }
}