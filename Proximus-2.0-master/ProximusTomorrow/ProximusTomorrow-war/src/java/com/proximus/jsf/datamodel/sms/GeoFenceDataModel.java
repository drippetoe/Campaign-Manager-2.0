/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.GeoFence;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class GeoFenceDataModel extends ListDataModel<GeoFence> implements SelectableDataModel<GeoFence> {

    public GeoFenceDataModel() {
    }

    public List<GeoFence> getGeoFenceData() {
        return (List<GeoFence>) this.getWrappedData();
    }

    public GeoFenceDataModel(List<GeoFence> geoFenceData) {
        super(geoFenceData);
    }

    @Override
    public Object getRowKey(GeoFence g) {
        return g.getName();
    }

    @Override
    public GeoFence getRowData(String rowKey) {
        List<GeoFence> geoFences = (List<GeoFence>) getWrappedData();
        for (GeoFence g : geoFences) {
            if (g.getName().equals(rowKey)) {
                return g;
            }
        }
        return null;
    }
}
