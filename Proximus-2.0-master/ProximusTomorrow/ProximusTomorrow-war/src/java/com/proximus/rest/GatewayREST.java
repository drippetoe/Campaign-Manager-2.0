package com.proximus.rest;

import com.proximus.data.*;
import com.proximus.manager.*;
import com.proximus.util.MapQuery;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author eric
 */
@Path("/gateway")
@RequestScoped
public class GatewayREST {

    static final Logger logger = Logger.getLogger(GatewayREST.class.getName());
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private DeviceManagerLocal deviceMgr;
    @EJB
    private CampaignManagerLocal campaignMgr;
    @EJB
    private GatewayManagerLocal gatewayMgr;
    @EJB
    private WifiRegistrationManagerLocal registrationMgr;
    @EJB
    private CompanyManagerLocal compMgr;

    public GatewayREST() {
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public Response baldursgateForm() {
        return Response.status(Response.Status.NO_CONTENT).entity("Please see Proximus API documentation").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces({MediaType.TEXT_PLAIN})
    @Produces({MediaType.WILDCARD})
    public Response baldursgate(MultivaluedMap<String, String> formParams) {
        String deviceMacAddress = formParams.getFirst("device_macaddress").toUpperCase().replace(":", "");
        Long campaignId = Long.parseLong(formParams.getFirst("campaign_id"));
        String userMacAddress = formParams.getFirst("user_macaddress").toUpperCase().replace(":", "");

        /*
         * debug
         */
        boolean register = Boolean.parseBoolean(formParams.getFirst("register"));
        if (register) {
            for (String string : formParams.keySet()) {
                if (!string.equals("device_macaddress") || !string.equals("campaign_id") || !string.equals("user_macaddress")) {
                    String value = formParams.getFirst(string);
                    System.out.println("key: " + string + " value:" + value);
                }
            }
        }
        /*
         * code
         */

        Device device = deviceMgr.getDeviceByMacAddress(deviceMacAddress);

        if (device == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Device NOT Found").build();
        }

        Campaign campaign = campaignMgr.find(campaignId);

        if (campaign == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Campaign NOT Found").build();
        }
        WifiCampaign wc = campaign.getWifiCampaign();
        if (wc == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Wifi Campaign NOT Found").build();
        }
        if ((wc.getHotSpotMode() != 2) && (wc.getHotSpotMode() != 4)) {
            return Response.status(Response.Status.NO_CONTENT).entity("Incorrect HOTSPOT MODE: " + wc.getHotSpotMode()).build();
        }

        Company company = campaign.getCompany();
        GatewayPartnerFields gwFields = gatewayMgr.getGatewayPartnerFieldsByCampaign(campaign);


        if (gwFields == null) {
            return Response.status(Response.Status.NO_CONTENT).entity("No Gateway Partner Fields defined in Campaign").build();
        }

        // if we get here, we either need to show the form, register the user, or redirect
        GatewayUser gwUser = gatewayMgr.getGatewayUserByMacAddress(company, userMacAddress);


        // if we don't have the user, we either register, or present the form
        if (gwUser == null) {
            if (register) {

                gwUser = new GatewayUser();
                gwUser.setCompany(company);
                gwUser.setMacAddress(userMacAddress);
                gwUser.setRegistrationTime(new Date());

                WifiRegistration reg;
                try {
                    reg = new WifiRegistration();
                    reg.setEventDate(new Date());
                    reg.setCompany(company);
                    reg.setCampaign(campaign);
                    reg.setDevice(device);
                    reg.setMacAddress(userMacAddress);
                    reg.setField1(formParams.getFirst("firstname"));
                    reg.setField2(formParams.getFirst("lastname"));
                    reg.setField3(formParams.getFirst("email"));
                    reg.setField4(formParams.getFirst("mobile"));
                    registrationMgr.create(reg);
                } catch (Exception err) {
                    logger.fatal(err);
                    reg = null;
                }

                if (reg != null) {
                    gwUser.setWifiRegistration(reg);
                }

                if (gwFields.getPartnerName().equalsIgnoreCase("MZ")) {
                    String identifier = getNewMZRegistrationId(reg, gwFields, userMacAddress);
                    logger.info("User with Mac " + userMacAddress + " assigned MZ registration id: " + identifier);

                    gwUser.setValue1(identifier);
                } else {
                    gwUser.setValue1(gwFields.getPartnerName() + "-" + userMacAddress);  // not sure what if anything is needed
                }

                gatewayMgr.create(gwUser);

            } else {
                return Response.status(Response.Status.OK).header("Content-Type", "text/html").entity(getRegistrationForm(deviceMacAddress, userMacAddress, gwFields)).build();
            }
        }

        redirectUser(gwUser, gwFields);
        return Response.status(Response.Status.TEMPORARY_REDIRECT).entity("").build();

    }

    private String buildURLForGateway(GatewayUser gwUser, GatewayPartnerFields gwFields) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (gwUser.getValue1() != null && !gwUser.getValue1().isEmpty()) {
            map.put(gwFields.getKey1(), gwUser.getValue1());
        }
        if (gwUser.getValue2() != null && !gwUser.getValue2().isEmpty()) {
            map.put(gwFields.getKey2(), gwUser.getValue2());
        }
        if (gwUser.getValue3() != null && !gwUser.getValue3().isEmpty()) {
            map.put(gwFields.getKey3(), gwUser.getValue3());
        }
        if (gwUser.getValue4() != null && !gwUser.getValue4().isEmpty()) {
            map.put(gwFields.getKey4(), gwUser.getValue4());
        }
        if (gwUser.getValue5() != null && !gwUser.getValue5().isEmpty()) {
            map.put(gwFields.getKey5(), gwUser.getValue5());
        }

        String url = gwFields.getUrl() + "?" + MapQuery.urlEncodeUTF8(map);
        return url;
    }

    /**
     * Authentication specific to Mobilozophy, keyed off partner name "MZ"
     *
     * @param partner GatewayPartnerFields for MZ campaign in question
     * @return resulting registration id
     */
    private String getNewMZRegistrationId(WifiRegistration reg, GatewayPartnerFields partner, String userMacAddress) {
        Client client = Client.create();

        client.addFilter(new HTTPBasicAuthFilter(partner.getPartnerLogin(), partner.getPartnerPassword()));

        StringBuilder urlBuilder = new StringBuilder("https://secure.mobilozophy.com/api/registrations/");
        urlBuilder.append("?merchantIdentifier=").append(userMacAddress);
        urlBuilder.append("&firstName=").append(reg.getField1());
        urlBuilder.append("&lastName=").append(reg.getField2());
        urlBuilder.append("&email=").append(reg.getField3());
        urlBuilder.append("&mobilePhone=").append(reg.getField4());

        String url = urlBuilder.toString();
        WebResource webResource = client.resource(url);

        ClientResponse clResponse = webResource.type("application/json").post(ClientResponse.class, "");
        String output = clResponse.getEntity(String.class);

        logger.debug("Request: " + url + " | Response: " + output);

        try {
            JSONObject json = new JSONObject(output);
            return json.getString("result");

        } catch (JSONException ex) {
            logger.fatal(ex);
            return null;
        }
    }

    private void redirectUser(GatewayUser gwUser, GatewayPartnerFields gwFields) {
        try {
            response.sendRedirect(buildURLForGateway(gwUser, gwFields));
        } catch (IOException err) {
            logger.fatal(err);
        }
    }

    private String getRegistrationForm(String deviceMacAddress, String userMacAddress, GatewayPartnerFields gwFields) {
        try {
            String form = IOUtils.toString(GatewayREST.class.getResourceAsStream("/resources/gateway/register.html"), "UTF-8");
            return form.replace("device_macaddress_here", deviceMacAddress).replace("campaign_id_here", gwFields.getCampaign().getId().toString()).replace("user_macaddress_here", userMacAddress);
        } catch (IOException ex) {
            logger.fatal(ex);
            return "ERROR: " + ex.toString();
        }
    }
}