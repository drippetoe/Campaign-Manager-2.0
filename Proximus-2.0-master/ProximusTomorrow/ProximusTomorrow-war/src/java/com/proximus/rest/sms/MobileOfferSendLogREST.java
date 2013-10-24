/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.MobileOfferManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@Path("/mobileoffersendlog")
@RequestScoped
public class MobileOfferSendLogREST {

    static final Logger logger = Logger.getLogger(MobileOfferSendLogREST.class.getName());
    static final String BASE_URL = "http://proximusmobility.com/api/mobileoffersendlog";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private MobileOfferSettingsManagerLocal mosMgr;
    @EJB
    private MobileOfferManagerLocal moMgr;
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private MobileOfferSendLogManagerLocal sendLogMgr;
    @EJB
    private SubscriberManagerLocal subscriberMgr;
    @EJB
    private UserManagerLocal userMgr;

    public MobileOfferSendLogREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_PLAIN})
    public Response post(MultivaluedMap<String, String> formParams) {

        //get params
        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");
        String msisdn = formParams.getFirst("msisdn");
        String mobileOfferId = formParams.getFirst("mobileOfferId");
        String sendDate = formParams.getFirst("send_date");

        try {
            userName = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("decoding error").build();
        }
        User user = userMgr.getUserByUsername(userName);
        Company company = user.getCompanies().get(0);
        String password = user.getApiPassword();
        String requestURL = BASE_URL + "/" + userName + "/" + "/" + password + "/"
                + "/" + msisdn + "/" + mobileOfferId + "/" + sendDate;
        String companySalt = company.getSalt();


        if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
            Long mobileOfferLng = Long.parseLong(mobileOfferId);
            MobileOfferSendLog myLog = new MobileOfferSendLog();
            Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
            if (subscriber != null) {
                myLog.setSubscriber(subscriber);
            }
            MobileOffer mo = moMgr.getMobileOfferbyId(mobileOfferLng);
            if ((mo != null)) {
                myLog.setMobileOffer(mo);
            }
            myLog.setEventDate(DateUtil.formatStringToDate(sendDate));
            sendLogMgr.create(myLog);
            return Response.status(Response.Status.OK).entity("send_offer_log_complete").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("UNAUTHORIZED").build();
        }
    }
}
