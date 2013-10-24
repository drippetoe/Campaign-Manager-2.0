/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.FacebookUserLog;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface FacebookUserLogManagerLocal extends AbstractManagerInterface<FacebookUserLog> {

    public FacebookUserLog getFacebookUserLogByFacebookId(Long facebookId);
}
