/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.User;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Ronald 
 */
public class UserDataModel extends ListDataModel<User> implements SelectableDataModel<User> {

    public UserDataModel() {
    }

    public UserDataModel(List<User> userData) {
        super(userData);
    }

    @Override
    public Object getRowKey(User u) {
        return u.getUserName();
    }

    @Override
    public User getRowData(String rowKey) {
        List<User> users = (List<User>) getWrappedData();
        for (User u : users) {
            if (u.getUserName().equals(rowKey)) {
                return u;
            }
        }
        return null;
    }
}
