/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.app;

import com.proximus.data.app.AppUserLocation;
import com.proximus.manager.AbstractManagerInterface;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface AppUserLocationManagerLocal extends AbstractManagerInterface<AppUserLocation> {

    public AppUserLocation findAppUserLocation(Double latitude, Double longitude);
}
