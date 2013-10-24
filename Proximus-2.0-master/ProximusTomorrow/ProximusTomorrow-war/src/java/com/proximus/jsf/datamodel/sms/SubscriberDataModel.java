/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.Subscriber;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class SubscriberDataModel extends ListDataModel<Subscriber> implements SelectableDataModel<Subscriber> {

    public SubscriberDataModel() {
    }

    public SubscriberDataModel(List<Subscriber> userData) {
        super(userData);
    }

    @Override
    public Object getRowKey(Subscriber u) {
        return u.getMsisdn();
    }

    @Override
    public Subscriber getRowData(String rowKey) {
        List<Subscriber> users = (List<Subscriber>) getWrappedData();
        for (Subscriber u : users) {
            if (u.getMsisdn().equals(rowKey)) {
                return u;
            }
        }
        return null;
    }
}
