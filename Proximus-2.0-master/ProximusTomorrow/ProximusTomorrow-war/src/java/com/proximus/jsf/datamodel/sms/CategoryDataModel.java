/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel.sms;

import com.proximus.data.sms.Category;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class CategoryDataModel extends ListDataModel<Category> implements SelectableDataModel<Category> {

    public CategoryDataModel() {
    }

    public List<Category> getCategoryData() {
        return (List<Category>) this.getWrappedData();
    }

    public CategoryDataModel(List<Category> categoryData) {
        super(categoryData);
    }

    @Override
    public Object getRowKey(Category c) {
        return c.getName();
    }

    @Override
    public Category getRowData(String rowKey) {
        List<Category> categories = (List<Category>) getWrappedData();
        for (Category c : categories) {
            if (c.getName().equals(rowKey)) {
                return c;
            }
        }
        return null;
    }
}
