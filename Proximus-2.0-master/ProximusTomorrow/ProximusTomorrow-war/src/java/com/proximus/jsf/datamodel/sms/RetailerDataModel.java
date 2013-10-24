/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.Retailer;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class RetailerDataModel extends ListDataModel<Retailer> implements SelectableDataModel<Retailer> {

    public RetailerDataModel() {
    }

    public List<Retailer> getRetailerData() {
        return (List<Retailer>) this.getWrappedData();
    }

    public RetailerDataModel(List<Retailer> retailerData) {
        super(retailerData);
    }

    @Override
    public Object getRowKey(Retailer r) {
        return r.getName();
    }

    @Override
    public Retailer getRowData(String rowKey) {
        List<Retailer> retailers = (List<Retailer>) getWrappedData();
        for (Retailer r : retailers) {
            if (r.getName().equals(rowKey)) {
                return r;
            }
        }
        return null;
    }
}
