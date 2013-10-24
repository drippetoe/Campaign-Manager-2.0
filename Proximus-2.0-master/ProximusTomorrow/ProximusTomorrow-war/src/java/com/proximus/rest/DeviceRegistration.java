package com.proximus.rest;

import com.proximus.data.Device;
import com.proximus.data.DeviceInLimbo;
import com.proximus.data.Registration;
import com.proximus.manager.DeviceManagerLocal;
import java.util.Date;
import java.util.UUID;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 *
 * @author dshaw
 */
@Path("/register")
@RequestScoped
public class DeviceRegistration {

    @Context
    private UriInfo context;
    @Context
    HttpServletRequest request;
    @EJB
    private DeviceManagerLocal deviceMgr;

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response register(MultivaluedMap<String, String> formParams) {
        String serialNumber = formParams.getFirst("serialNumber");
        String major = formParams.getFirst("major");
        String minor = formParams.getFirst("minor");
        String build = formParams.getFirst("build");
        String kernel = formParams.getFirst("kernel");
        String platform = formParams.getFirst("platform");
        String mac = formParams.getFirst("mac");
        Device dd = deviceMgr.getDeviceByMacAddress(mac.toUpperCase());
        //We got a rogue registration call we put it in the device_in_limbo table
        if (dd == null) {


            handleDeviceInLimbo(mac.toUpperCase().replaceAll(":", ""), serialNumber, major, minor, build, kernel, platform);
            //Unable to find the device interface the database make sure to report and reflect this somehow
            return Response.status(Response.Status.NOT_FOUND).entity("Device not found in our database").build();
        } else {
            Date now = new Date();
            dd.setLastSeen(now);

            if (dd.getToken() == null) {
                // Generate token

                dd.setToken(UUID.randomUUID().toString());
                dd.setRegistrationDate(now);
                Registration reg = new Registration();
                reg.setToken(dd.getToken());
                deviceMgr.update(dd);
                return Response.status(Response.Status.OK).entity(reg).build();
            }
            // this update is here to persist last-seen -- if a device is trying to
            // we might consider adding a "message" column or something to diagnose this
            deviceMgr.update(dd);
            handleDeviceInLimbo(mac.toUpperCase(), serialNumber, major, minor, build, kernel, platform);


            return Response.status(Response.Status.FORBIDDEN).entity("Device already registered").build();

        }

    }

    /**
     * Used when a mac address is not found in our Device Table
     *
     * @param mac
     */
    private void handleDeviceInLimbo(String mac, String serialNumber, String major, String minor, String build, String kernel, String platform) {
        DeviceInLimbo dl = deviceMgr.getDeviceInLimboByMacAdress(mac.toUpperCase());
        if (dl == null) {
            dl = new DeviceInLimbo(mac.toUpperCase());
            if (serialNumber != null) {
                dl.setSerialNumber(serialNumber);
            }
            if (major != null) {
                dl.setMajor(Long.parseLong(major));
            }
            if (minor != null) {
                dl.setMinor(Long.parseLong(minor));
            }
            if (build != null) {
                dl.setBuild(Long.parseLong(build));
            }
            if (kernel != null) {
                dl.setKernel(kernel);
            }
            dl.setLastSeen(new Date());
            dl.setLastIpAddress(request.getRemoteAddr());
            deviceMgr.createDeviceInLimbo(dl);

        } else {
            //This mac address was already seen. Updating ip and last_seen
            dl.setLastSeen(new Date());
            dl.setLastIpAddress(request.getRemoteAddr());
            deviceMgr.updateDeviceInLimbo(dl);
        }
    }

    
}
