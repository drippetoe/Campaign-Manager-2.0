/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.Brand;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Locale;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface CategoryManagerLocal extends AbstractManagerInterface<Category> {

    public Category getCategoryByBrandAndName(Brand b, String name);

    public List<Category> getAllByBrand(Brand b);

    public List<Category> getAllByBrandFilterLike(Brand b, String like);

    public Category getCategoryByBrandAndWebSafeName(Brand b, String webSafeName);

    public List<Category> getAllByBrandAndLocale(Brand b, Locale locale);
}
