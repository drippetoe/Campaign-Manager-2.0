/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.PubNubKey;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class NotificationDataModel extends ListDataModel<PubNubKey> implements SelectableDataModel<PubNubKey> {

    public NotificationDataModel() {
    }

    public NotificationDataModel(List<PubNubKey> data) {
        super(data);
    }

    public List<PubNubKey> getKeywordData() {
        return (List<PubNubKey>) this.getWrappedData();
    }

    @Override
    public Object getRowKey(PubNubKey n) {
        return n.getSecret();
    }

    @Override
    public PubNubKey getRowData(String secret) {
        List<PubNubKey> notfications = (List<PubNubKey>) getWrappedData();
        for (PubNubKey k : notfications) {
            if (k.getSecret().equals(secret)) {
                return k;
            }
        }
        return null;
    }

    
}
