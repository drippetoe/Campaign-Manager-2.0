/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Role;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Ronald
 */
public class RoleDataModel extends ListDataModel<Role> implements SelectableDataModel<Role>
{
    public RoleDataModel()
    {
    }

    public RoleDataModel(List<Role> roleData)
    {
        super(roleData);

    }

    public List<Role> getRoleData()
    {
        return (List<Role>) this.getWrappedData();
    }

    public void setRoleData(List<Role> roleData)
    {
        this.setWrappedData(roleData);
    }

    @Override
    public Object getRowKey(Role r)
    {
        return r.getName();
    }

    @Override
    public Role getRowData(String rowKey)
    {
        List<Role> roles = (List<Role>) getWrappedData();
        for (Role r : roles) {
            if (r.getName().equalsIgnoreCase(rowKey)) {
                return r;
            }
        }
        return null;
    }
}