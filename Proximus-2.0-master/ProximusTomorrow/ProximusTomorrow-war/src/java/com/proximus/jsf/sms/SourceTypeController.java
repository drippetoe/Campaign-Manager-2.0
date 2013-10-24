/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf.sms;

import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.SourceType;
import com.proximus.helper.util.JsfUtil;
import com.proximus.jsf.AbstractController;
import com.proximus.jsf.datamodel.sms.SourceTypeDataModel;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.manager.sms.SourceTypeManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ronald
 */
@ManagedBean(name = "sourceTypeController")
@SessionScoped
public class SourceTypeController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    @EJB
    SourceTypeManagerLocal sourceTypeMgr;
    @EJB
    KeywordManagerLocal keywordMgr;
    private SourceType newSourceType;
    private SourceTypeDataModel sourceModel;
    private List<SourceType>filteredSourceTypes;
    private SourceType selectedSourceType;
    private ResourceBundle message;

    public SourceTypeController() {
        message = this.getHttpSession().getMessages();
    }

    public SourceType getNewSourceType() {
        if (newSourceType == null) {
            newSourceType = new SourceType();
        }
        return newSourceType;
    }

    public void setNewSourceType(SourceType newSourceType) {
        this.newSourceType = newSourceType;
    }

    public SourceType getSelectedSourceType() {
        if (selectedSourceType == null) {
            selectedSourceType = new SourceType();
        }
        return selectedSourceType;
    }

    public void setSelectedSourceType(SourceType selectedSourceType) {
        this.selectedSourceType = selectedSourceType;
    }

    public SourceTypeDataModel getSourceModel() {
        if (sourceModel == null) {
            populateSourceModel();
        }
        return sourceModel;
    }

    public void setSourceModel(SourceTypeDataModel sourceModel) {
        this.sourceModel = sourceModel;
    }

    public List<SourceType> getFilteredSourceTypes() {
        return filteredSourceTypes;
    }

    public void setFilteredSourceTypes(List<SourceType> filteredSourceTypes) {
        this.filteredSourceTypes = filteredSourceTypes;
    }

    private void populateSourceModel() {
        List<SourceType> sources = sourceTypeMgr.getAllSorted();
        sourceModel = new SourceTypeDataModel(sources);
        filteredSourceTypes = new ArrayList<SourceType>(sources);
    }

    public String prepareList() {
        sourceModel = null;
        newSourceType = null;
        selectedSourceType = null;
        return "/source-type/List?faces-redirect=true";
    }

    public void addSourceType(SourceType sourceType) {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        if (sourceType == null || sourceType.getSourceType() == null || sourceType.getSourceType().isEmpty()) {
            validation = false;
            context.addCallbackParam("validation", validation);
            JsfUtil.addErrorMessage(message.getString("sourceEmptyError"));
            return;
        }
        List<String> sources = new ArrayList<String>();
        if (sourceModel.getSourceTypeData() != null && !sourceModel.getSourceTypeData().isEmpty()) {
            for (SourceType s : sourceModel.getSourceTypeData()) {
                sources.add(s.getSourceType().toLowerCase());
            }
            if (sources.contains(sourceType.getSourceType().toLowerCase())) {
                validation = false;
                context.addCallbackParam("validation", validation);
                JsfUtil.addErrorMessage(message.getString("sourceDuplicatedError"));
                return;
            }
        }
        sourceTypeMgr.create(sourceType);
        validation = true;
        context.addCallbackParam("validation", validation);
        JsfUtil.addSuccessMessage("sourceCreated");
        prepareList();
    }

    public void editSourceType(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        try {
            if (selectedSourceType.getSourceType() == null || selectedSourceType.getSourceType().isEmpty()) {
                validation = false;
                context.addCallbackParam("validation", validation);
                JsfUtil.addErrorMessage(message.getString("sourceEmptyError"));
                return;
            }
            sourceTypeMgr.update(selectedSourceType);
            prepareList();
        } catch (Exception e) {
            context.addCallbackParam("validation", false);
            JsfUtil.addErrorMessage(message.getString("sourcePersistError"));
            prepareList();
            return;
        }
        context.addCallbackParam("validation", true);
    }

    public void deleteSourceType() {
        if (selectedSourceType == null) {
            return;
        }

        try {
            List<Keyword> keywords = keywordMgr.getKeywordsBySourceType(selectedSourceType);
            if (keywords == null) {
                sourceTypeMgr.delete(selectedSourceType);
            } else {
                JsfUtil.addErrorMessage("Can't Delete SourceType because is tied to: " + keywords.size() + " keywords");
                return;
            }
        } catch (Exception e) {
        }
        prepareList();
    }
}
