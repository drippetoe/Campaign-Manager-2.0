/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.Property;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class PropertyDataModel extends ListDataModel<Property> implements SelectableDataModel<Property> {

    public PropertyDataModel() {
    }

    public List<Property> getPropertyData() {
        return (List<Property>) this.getWrappedData();
    }

    public PropertyDataModel(List<Property> propertyData) {
        super(propertyData);
    }

    @Override
    public Object getRowKey(Property p) {
        return p.getName();
    }

    @Override
    public Property getRowData(String rowKey) {
        List<Property> properties = (List<Property>) getWrappedData();
        for (Property p : properties) {
            if (p.getName().equals(rowKey)) {
                return p;
            }
        }
        return null;
    }
}
