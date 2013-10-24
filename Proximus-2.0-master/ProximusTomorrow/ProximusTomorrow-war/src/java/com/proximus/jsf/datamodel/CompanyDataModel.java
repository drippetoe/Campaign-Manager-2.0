/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Company;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Gilberto Gaxiola
 */
public class CompanyDataModel  extends ListDataModel<Company> implements SelectableDataModel<Company>
{
        public CompanyDataModel()
    {
    }

    public List<Company> getCompanyData() {
        return (List<Company>)this.getWrappedData();
    }    
        
        
    public CompanyDataModel(List<Company> companyData)
    {
        super(companyData);
    }

    @Override
    public Object getRowKey(Company c)
    {
        return c.getName();
    }

    @Override
    public Company getRowData(String rowKey)
    {
         List<Company> companies = (List<Company>) getWrappedData();
        for (Company c : companies) {
            if (c.getName().equals(rowKey)) {
                return c;
            }
        }
        return null;
    }
    
}
