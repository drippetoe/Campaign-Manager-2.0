/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.sms.LocationData;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class LocationDataModel extends ListDataModel<LocationData> implements SelectableDataModel<LocationData> {

    public LocationDataModel() {
    }

    public LocationDataModel(List<LocationData> locationData) {
        super(locationData);
    }

    @Override
    public Object getRowKey(LocationData l) {
        return l.getMsisdn();
    }

    @Override
    public LocationData getRowData(String rowKey) {
        List<LocationData> locations = (List<LocationData>) getWrappedData();
        for (LocationData l : locations) {
            if (l.getMsisdn().equals(rowKey)) {
                return l;
            }
        }
        return null;
    }
}