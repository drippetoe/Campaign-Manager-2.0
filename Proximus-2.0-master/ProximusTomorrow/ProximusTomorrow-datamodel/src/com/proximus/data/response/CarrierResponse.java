/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.response;

import com.proximus.data.sms.Carrier;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
@XmlRootElement(name = "carrierResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CarrierResponse extends BaseResponse
{
    private List<Carrier> carrier;

    public CarrierResponse()
    {
    }

    public List<Carrier> getCarriers()
    {
        return carrier;
    }

    public void setCarriers(List<Carrier> carrier)
    {
        this.carrier = carrier;
    }

    

    
    
    
}
