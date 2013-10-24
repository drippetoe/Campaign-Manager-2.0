/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.sms.Category;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.web.WebOffer;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.CategoryDataModel;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.MobileOfferManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.manager.web.WebOfferManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "categoryController")
@SessionScoped
public class CategoryController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    CategoryManagerLocal categoryMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    MobileOfferManagerLocal mobileOfferMgr;
    @EJB
    WebOfferManagerLocal webOfferMgr;
    @EJB
    SubscriberManagerLocal subMgr;
    private Category newCategory;
    private CategoryDataModel categoryModel;
    private List<Category> filteredCategories;
    private Category selectedCategory;
    private List<Property> listProperties;
    private Property selectedProperty;
    private ResourceBundle message;

    public CategoryController() {
        message = this.getHttpSession().getMessages();
    }

    public CategoryDataModel getCategoryModel() {
        if (categoryModel == null) {
            populateCategoryModel();
        }
        return categoryModel;
    }

    public void setCategoryModel(CategoryDataModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public List<Category> getFilteredCategories() {
        return filteredCategories;
    }

    public void setFilteredCategories(List<Category> filteredCategories) {
        this.filteredCategories = filteredCategories;
    }

    public List<Property> getListProperties() {
        if (listProperties == null) {
            this.populatePropertyList();
        }
        return listProperties;
    }

    public void setListProperties(List<Property> listProperties) {
        this.listProperties = listProperties;
    }

    public Category getNewCategory() {
        if (newCategory == null) {
            newCategory = new Category();
        }
        return newCategory;
    }

    public void setNewCategory(Category newCategory) {
        this.newCategory = newCategory;
    }

    public Category getSelectedCategory() {
        if (selectedCategory == null) {
            selectedCategory = new Category();
        }
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Property getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    private void populateCategoryModel() {
        List<Category> categories = categoryMgr.getAllByBrand(this.getCompanyFromSession().getBrand());
        categoryModel = new CategoryDataModel(categories);
        filteredCategories = new ArrayList<Category>(categories);
    }

    private void populatePropertyList() {
        listProperties = propertyMgr.getPropertiesByCompany(this.getCompanyFromSession());
    }

    public String prepareList() {
        categoryModel = null;
        newCategory = null;
        selectedCategory = null;
        selectedProperty = null;
        listProperties = null;
        return "/category/List?faces-redirect=true";
    }

    public void createNewCategory(Category category) {



        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("categoryNameError"));
            category = new Category();
            return;
        }
        List<String> categoryNames = new ArrayList<String>();
        if (categoryModel.getCategoryData() != null && !categoryModel.getCategoryData().isEmpty()) {
            for (Category c : categoryModel.getCategoryData()) {
                categoryNames.add(c.getName().toLowerCase());
            }


            if (categoryNames.contains(category.getName().toLowerCase())) {
                JsfUtil.addErrorMessage(message.getString("categoryDuplicatedError"));
                return;
            }

        }
        category.setBrand(this.getCompanyFromSession().getBrand());
        categoryMgr.create(category);
        JsfUtil.addSuccessMessage(message.getString("categoryCreated"));
        prepareList();

    }

    public boolean editCategory() {
        try {
            if (selectedCategory.getName() == null || selectedCategory.getName().isEmpty()) {
                JsfUtil.addErrorMessage(message.getString("categoryNameError"));
                prepareList();
                return false;
            }
            categoryMgr.update(selectedCategory);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("categoryPersistError"));
            prepareList();
            return false;
        }
        return true;
    }

    public void deleteCategory() {
        if (selectedCategory == null) {
            return;
        }
        try {

            List<MobileOffer> mo = selectedCategory.getMobileOffers();
            List<WebOffer> wo = selectedCategory.getWebOffers();
            List<Subscriber> sub = selectedCategory.getSubscribers();


            for (Subscriber s : sub) {
                s.removeCategory(selectedCategory);
                subMgr.update(s);

            }
            for (MobileOffer m : mo) {
                m.removeCategory(selectedCategory);
                mobileOfferMgr.update(m);
            }


            for (WebOffer w : wo) {
                w.removeCategory(selectedCategory);
                webOfferMgr.update(w);
            }


            selectedCategory.clearMobileOffers();
            selectedCategory.clearWebOffers();
            selectedCategory.clearSubscribers();
            categoryMgr.update(selectedCategory);


            categoryMgr.delete(selectedCategory);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("categoryDeleteError") + ": " + selectedCategory.getName() + ".\n" + message.getString("categoryMobileOfferAssociatedDeleteError"));
            prepareList();
            return;
        }
        JsfUtil.addSuccessMessage(message.getString("categoryDeleteSuccess"));
        prepareList();
    }

    @FacesConverter(forClass = Category.class, value = "categoryControllerConverter")
    public static class CategoryControllerConverter implements Converter, Serializable {

        private static final ResourceBundle MESSAGE = ResourceBundle.getBundle("resources.Messages");

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);

                Category controller = ((CategoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "categoryController")).categoryMgr.find(id);

                return controller;
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", MESSAGE.getString("categoryConverterValueNotFound")));
            }
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
            if (value == null || value.toString().isEmpty() || !(value instanceof Category)) {
                return null;
            }
            return String.valueOf(((Category) value).getId());
        }
    }
}
