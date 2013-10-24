/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.SourceType;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class SourceTypeDataModel extends ListDataModel<SourceType> implements SelectableDataModel<SourceType>, Serializable {

    public SourceTypeDataModel() {
    }

    public List<SourceType> getSourceTypeData() {
        return (List<SourceType>) this.getWrappedData();
    }

    public SourceTypeDataModel(List<SourceType> sourceTypeData) {
        super(sourceTypeData);
    }

    @Override
    public Object getRowKey(SourceType sourceType) {
        return sourceType.getSourceType();
    }

    @Override
    public SourceType getRowData(String rowKey) {
        List<SourceType> sourceTypes = (List<SourceType>) getWrappedData();
        for (SourceType sourceType : sourceTypes) {
            if (sourceType.getSourceType().equals(rowKey)) {
                return sourceType;
            }
        }
        return null;
    }
}
