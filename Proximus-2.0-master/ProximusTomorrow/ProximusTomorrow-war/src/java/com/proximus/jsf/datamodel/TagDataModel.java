/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.datamodel;

import com.proximus.data.Tag;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author ronald
 */
public class TagDataModel extends ListDataModel<Tag> implements SelectableDataModel<Tag> {
    
    


    public TagDataModel() {
    }

    public TagDataModel(List<Tag> tagData) {
        super(tagData);
      
    }

    public List<Tag> getTagData()
    {
        return (List<Tag>)this.getWrappedData();
    }

    public void setTagData(List<Tag> tagData)
    {
        this.setWrappedData(tagData);
    }
    
    @Override
    public Object getRowKey(Tag t) {
        return t.getName();
    }

    @Override
    public Tag getRowData(String rowKey) {
        List<Tag> tags = (List<Tag>) getWrappedData();
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(rowKey)) {
                return t;
            }
        }
        return null;
    }
}
