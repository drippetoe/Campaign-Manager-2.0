/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.DeviceInLimbo;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gaxiola
 */
public class DeviceInLimboDataModel extends ListDataModel<DeviceInLimbo> implements SelectableDataModel<DeviceInLimbo> {

    public DeviceInLimboDataModel() {
    }

    public DeviceInLimboDataModel(List<DeviceInLimbo> deviceData) {
        super(deviceData);
    }

    @Override
    public Object getRowKey(DeviceInLimbo d) {
        return d.getMacAddress();
    }

    @Override
    public DeviceInLimbo getRowData(String rowKey) {
        List<DeviceInLimbo> devices = (List<DeviceInLimbo>) getWrappedData();
        for (DeviceInLimbo d : devices) {
            if (d.getMacAddress().equals(rowKey)) {
                return d;
            }
        }
        return null;
    }
}
