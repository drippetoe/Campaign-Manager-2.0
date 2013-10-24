/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.SMSCampaign;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class SMSCampaignDataModel extends ListDataModel<SMSCampaign> implements SelectableDataModel<SMSCampaign> {

    public SMSCampaignDataModel() {
    }

    public SMSCampaignDataModel(List<SMSCampaign> smsCampaignData) {
        super(smsCampaignData);
    }

    @Override
    public Object getRowKey(SMSCampaign c) {
        return c.getName();
    }

    @Override
    public SMSCampaign getRowData(String rowKey) {
        List<SMSCampaign> smsCampaigns = (List<SMSCampaign>) getWrappedData();
        for (SMSCampaign c : smsCampaigns) {
            if (c.getName().equals(rowKey)) {
                return c;
            }
        }
        return null;
    }
}
