/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.Device;
import com.proximus.data.SoftwareRelease;
import javax.ejb.Local;

/**
 *
 * @author dshaw
 */
@Local
public interface SoftwareReleaseManagerLocal extends AbstractManagerInterface<SoftwareRelease>
{
    public SoftwareRelease getReleaseForDevice(Device d);
    public SoftwareRelease getRelease(String platform, Long major, Long minor, Long build, Device d);
}
