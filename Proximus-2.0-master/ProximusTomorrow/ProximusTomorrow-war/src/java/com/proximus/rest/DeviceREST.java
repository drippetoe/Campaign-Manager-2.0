package com.proximus.rest;

import com.proximus.data.Device;
import com.proximus.manager.DeviceManagerLocal;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 *
 * @author dshaw
 */
@Path("/device")
@RequestScoped
public class DeviceREST
{
    @Context
    private UriInfo context;
    @EJB
    private DeviceManagerLocal deviceMgr;

    public DeviceREST()
    {
    }

//    @GET
//    @Produces("application/xml")
//    public String getXml()
//    {
//        List<Device> devices;
//
//        devices = deviceMgr.getAllDevices();
//
//        DeviceList d = new DeviceList(devices);
//        try {
//            return JAXBUtil.toXml(DeviceList.class, d);
//        } catch (Exception err) {
//            return "<xml>dp device list error" + err + "</xml>";
//        }
//    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response post(MultivaluedMap<String, String> formParams)
    {
        String nickname = formParams.getFirst("nickname");
        String serial_number = formParams.getFirst("serial_number");
        String macAddress = formParams.getFirst("macaddress");
        //Normalizing macaddress;
        String wifichannel = formParams.getFirst("wifichannel");
        Device d = deviceMgr.getDeviceByMacAddress(macAddress);
        if (d == null) {
            Device dd = new Device(serial_number, macAddress);
            try {
                dd.setName(nickname);
                dd.setWifiChannel(Integer.parseInt(wifichannel));
                dd.setLastIpAddress("unkown");
                dd.setRegistrationDate(new Date());
                //A WAY TO TIE IT TO THE SPECIFIC COMPANY
                //dd.setCompany(null);
                deviceMgr.create(dd);
            } catch (NumberFormatException nfe) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid wifichannel:" + wifichannel).build();
            }
            return Response.status(Response.Status.CREATED).entity(dd).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}