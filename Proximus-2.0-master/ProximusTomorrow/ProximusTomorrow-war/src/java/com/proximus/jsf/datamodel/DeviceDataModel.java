/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Device;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class DeviceDataModel extends ListDataModel<Device> implements SelectableDataModel<Device>
{
    public DeviceDataModel()
    {
    }

    public DeviceDataModel(List<Device> deviceData)
    {
        super(deviceData);
    }

    @Override
    public Object getRowKey(Device d)
    {
        return d.getMacAddress();
    }

    @Override
    public Device getRowData(String rowKey)
    {
        List<Device> devices = (List<Device>) getWrappedData();
        for (Device d : devices) {
            if (d.getMacAddress().equals(rowKey)) {
                return d;
            }
        }
        return null;
    }
}
