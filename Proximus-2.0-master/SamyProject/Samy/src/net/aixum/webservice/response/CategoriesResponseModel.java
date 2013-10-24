/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.Category;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class CategoriesResponseModel extends BaseResponseModel {

    private List<Category> categories;

    public CategoriesResponseModel(List<Category> categories, boolean success, String message) {
        super(success, message);
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
