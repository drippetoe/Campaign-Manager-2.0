/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Campaign;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class CampaignDataModel extends ListDataModel<Campaign> implements SelectableDataModel<Campaign>
{
    public CampaignDataModel()
    {
    }

    public CampaignDataModel(List<Campaign> campaignData)
    {
        super(campaignData);
    }

    @Override
    public Object getRowKey(Campaign c)
    {
        return c.getName();
    }

    @Override
    public Campaign getRowData(String rowKey)
    {
        List<Campaign> campaigns = (List<Campaign>) getWrappedData();
        for (Campaign c : campaigns) {
            if (c.getName().equals(rowKey)) {
                return c;
            }
        }
        return null;
    }
}
