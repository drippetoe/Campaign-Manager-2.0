/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data.response;

import com.proximus.data.sms.Category;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angela Mercer
 */
@XmlRootElement(name = "categoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryResponse extends BaseResponse
{
    private List<Category> category;

    public CategoryResponse()
    {
    }

    public List<Category> getCategories()
    {
        return category;
    }

    public void setCategories(List<Category> category)
    {
        this.category = category;
    }

    

    
    
    
}
