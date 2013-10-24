/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.DMA;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class DMADataModel extends ListDataModel<DMA> implements SelectableDataModel<DMA>, Serializable {

    public DMADataModel() {
    }

    public List<DMA> getDMAData() {
        return (List<DMA>) this.getWrappedData();
    }

    public DMADataModel(List<DMA> dmaData) {
        super(dmaData);
    }

    @Override
    public Object getRowKey(DMA dma) {
        return dma.getName();
    }

    @Override
    public DMA getRowData(String rowKey) {
        List<DMA> dmas = (List<DMA>) getWrappedData();
        for (DMA dma : dmas) {
            if (dma.getName().equals(rowKey)) {
                return dma;
            }
        }
        return null;
    }
}
