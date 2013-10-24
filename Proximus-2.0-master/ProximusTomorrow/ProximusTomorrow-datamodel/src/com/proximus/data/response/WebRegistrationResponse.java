/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.response;

import com.proximus.data.sms.Subscriber;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
@XmlRootElement(name = "webRegistrationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebRegistrationResponse extends BaseResponse
{
    private Subscriber subscriber;

    public Subscriber getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber)
    {
        this.subscriber = subscriber;
    }
    
    
}
