/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.startup.sms;

import javax.ejb.Remote;

/**
 *
 * @author ronald
 */
@Remote
public interface SubscriberStatusCheckerEJBRemote {

    public void start();
}
