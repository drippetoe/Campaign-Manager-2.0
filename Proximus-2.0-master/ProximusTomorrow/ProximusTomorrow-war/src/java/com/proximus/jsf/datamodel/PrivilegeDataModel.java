/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Privilege;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Ronald
 */
public class PrivilegeDataModel extends ListDataModel<Privilege> implements SelectableDataModel<Privilege>
{
    public PrivilegeDataModel()
    {
    }

    public PrivilegeDataModel(List<Privilege> privilegeData)
    {
        super(privilegeData);

    }

    public List<Privilege> getPrivilegeData()
    {
        return (List<Privilege>) this.getWrappedData();
    }

    public void setPrivilegeData(List<Privilege> privilegeData)
    {
        this.setWrappedData(privilegeData);
    }

    @Override
    public Object getRowKey(Privilege p)
    {
        return p.getPrivilegeName();
    }

    @Override
    public Privilege getRowData(String rowKey)
    {
        List<Privilege> Privilege = (List<Privilege>) getWrappedData();
        for (Privilege p : Privilege) {
            if (p.getPrivilegeName().equalsIgnoreCase(rowKey)) {
                return p;
            }
        }
        return null;
    }
}
