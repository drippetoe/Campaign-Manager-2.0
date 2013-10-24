/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.events.EventSubscription;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class EventSubscriptionDataModel extends ListDataModel<EventSubscription> implements SelectableDataModel<EventSubscription> {

    public EventSubscriptionDataModel() {
    }

    public EventSubscriptionDataModel(List<EventSubscription> eventSubscriptionData) {
        super(eventSubscriptionData);
    }

    @Override
    public Object getRowKey(EventSubscription es) {
        return es.getEventType().getId();
    }

    @Override
    public EventSubscription getRowData(String rowKey) {
        List<EventSubscription> eventSubscriptions = (List<EventSubscription>) getWrappedData();
        for (EventSubscription es : eventSubscriptions) {
            if (es.getEventType().getId().equals(rowKey)) {
                return es;
            }
        }
        return null;
    }
}
