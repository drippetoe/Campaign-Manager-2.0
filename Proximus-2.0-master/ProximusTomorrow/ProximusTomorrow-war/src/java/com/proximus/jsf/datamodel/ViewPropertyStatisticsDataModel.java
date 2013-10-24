/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.sms.report.PropertyStatsAgreggator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ViewPropertyStatisticsDataModel extends ListDataModel<PropertyStatsAgreggator> implements SelectableDataModel<PropertyStatsAgreggator>, Serializable {

    
    public List<PropertyStatsAgreggator> getViewPropertyData() {
        return (List<PropertyStatsAgreggator>) this.getWrappedData();
    }
    
    public ViewPropertyStatisticsDataModel() {
        super(new ArrayList<PropertyStatsAgreggator>());
    }

    public ViewPropertyStatisticsDataModel(List<PropertyStatsAgreggator> data) {
        super(data);
    }
    
    
    @Override
    public Object getRowKey(PropertyStatsAgreggator stat) {
        if(stat.getPropStats() != null) {
            return stat.getPropStats().getPropertyId();
        }
        return null;
    }

    @Override
    public PropertyStatsAgreggator getRowData(String rowKey) {
        List<PropertyStatsAgreggator> list = (List<PropertyStatsAgreggator>) getWrappedData();
        for(PropertyStatsAgreggator v : list) {
            long keyVal = Long.parseLong(rowKey);
            if(v.getPropStats() != null && v.getPropStats().getPropertyId() == keyVal) {
                return v;
            }
        }
        return null;
    }
}
