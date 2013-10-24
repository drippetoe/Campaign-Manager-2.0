/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.MobileOffer;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class MobileOfferDataModel extends ListDataModel<MobileOffer> implements SelectableDataModel<MobileOffer> {

    public MobileOfferDataModel() {
    }

    public List<MobileOffer> getMobileOfferData() {
        return (List<MobileOffer>) this.getWrappedData();
    }

    public MobileOfferDataModel(List<MobileOffer> mobileOfferData) {
        super(mobileOfferData);
    }

    @Override
    public Object getRowKey(MobileOffer mo) {
        return mo.getId();
    }

    @Override
    public MobileOffer getRowData(String rowKey) {
        List<MobileOffer> mobileOffers = (List<MobileOffer>) getWrappedData();
        for (MobileOffer mo : mobileOffers) {
            if (mo.getId().equals(Long.parseLong(rowKey))) {
                return mo;
            }
        }
        return null;
    }
}
