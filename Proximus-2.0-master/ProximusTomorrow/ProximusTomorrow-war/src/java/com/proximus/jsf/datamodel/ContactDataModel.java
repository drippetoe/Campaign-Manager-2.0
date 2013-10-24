/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Contact;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ContactDataModel extends ListDataModel<Contact> implements SelectableDataModel<Contact>
{


    
    public ContactDataModel() {
        
    }
    public ContactDataModel(List<Contact> data) {
        super(data);
    }
    
    
public List<Contact> getContactData()
    {
        return (List<Contact>)this.getWrappedData();
    }

    public void setContactData(List<Contact> contactData)
    {
        this.setWrappedData(contactData);
    }
    
    @Override
    public Object getRowKey(Contact co) {
        return co.getEmail();
    }

    @Override
    public Contact getRowData(String rowKey) {
        List<Contact> contacts = (List<Contact>) getWrappedData();
        for (Contact co : contacts) {
            if (co.getEmail().equalsIgnoreCase(rowKey)) {
                return co;
            }
        }
        return null;
    }
}
