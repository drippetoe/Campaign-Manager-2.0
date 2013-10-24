/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.app;

import com.proximus.data.app.AppUser;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface AppUserManagerLocal extends AbstractManagerInterface<AppUser> {

    public AppUser getByMsisdnOrIdentifierOrMac(String msisdn, String uuid, String mac);
}
