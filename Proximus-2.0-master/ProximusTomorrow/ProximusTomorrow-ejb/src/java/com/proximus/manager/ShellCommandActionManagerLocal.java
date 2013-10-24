/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager;

import com.proximus.data.Device;
import com.proximus.data.ShellCommandAction;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface ShellCommandActionManagerLocal extends AbstractManagerInterface<ShellCommandAction> {

    public List<ShellCommandAction> getShellCommandsForDevice(Device d);

    public void updateShellCommandAction(ShellCommandAction action);

    public void openSSHFor(Device d, String port);
}
