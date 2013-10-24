/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.HotspotDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class HotspotDomainDataModel extends ListDataModel<HotspotDomain> implements SelectableDataModel<HotspotDomain> {

    public HotspotDomainDataModel(List<HotspotDomain> list) {
        super(list);
    }

    public HotspotDomainDataModel() {
    }

    public void removeDomain(HotspotDomain i) {
        List<HotspotDomain> domains = (List<HotspotDomain>) getWrappedData();
        if (domains == null || domains.isEmpty()) {
            return;
        }
        Iterator<HotspotDomain> iterator = domains.iterator();
        while (iterator.hasNext()) {
            HotspotDomain hsd = iterator.next();
            if (hsd.equals(i)) {
                iterator.remove();
                break;
            }
        }
        
        for (HotspotDomain hotspotDomain : domains) {
            System.out.println(hotspotDomain);
        }
        setWrappedData(domains);
    }

    public void setDomains(List<HotspotDomain> domains) {
        if (domains == null || domains.isEmpty()) {
            setWrappedData(new ArrayList<HotspotDomain>());
        }
        List<HotspotDomain> list = new ArrayList<HotspotDomain>();
        for (HotspotDomain domain : domains) {
            list.add(domain);
        }
        if (list.size() > 0) {
            setWrappedData(list);
        }
    }

    @Override
    public Object getRowKey(HotspotDomain d) {
        return d.getDomainName();
    }

    @Override
    public HotspotDomain getRowData(String rowKey) {
        List<HotspotDomain> domains = (List<HotspotDomain>) getWrappedData();
        for (HotspotDomain hotspotDomain : domains) {
            if (hotspotDomain.getDomainName().equals(rowKey)) {
                return hotspotDomain;
            }
        }
        return null;
    }
}