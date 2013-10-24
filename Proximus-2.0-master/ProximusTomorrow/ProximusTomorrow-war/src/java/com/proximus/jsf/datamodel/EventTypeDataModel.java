/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.events.EventType;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class EventTypeDataModel extends ListDataModel<EventType> implements SelectableDataModel<EventType> {

    public EventTypeDataModel() {
    }

    public EventTypeDataModel(List<EventType> eventTypeData) {
        super(eventTypeData);
    }

    @Override
    public Object getRowKey(EventType e) {
        return e.getId();
    }

    @Override
    public EventType getRowData(String rowKey) {
        List<EventType> eventTypes = (List<EventType>) getWrappedData();
        for (EventType e : eventTypes) {
            if (e.getId().equals(rowKey)) {
                return e;
            }
        }
        return null;
    }
}
