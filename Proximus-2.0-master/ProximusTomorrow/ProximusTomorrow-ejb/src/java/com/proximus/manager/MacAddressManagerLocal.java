/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.MacAddress;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface MacAddressManagerLocal extends AbstractManagerInterface<MacAddress> {

    public MacAddress getMacAddressByMAC(String mac);
}
