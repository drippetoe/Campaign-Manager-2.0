package com.proximus.jsf;

import com.proximus.data.Campaign;
import com.proximus.data.Device;
import com.proximus.data.Tag;
import com.proximus.jsf.datamodel.CampaignDataModel;
import com.proximus.jsf.datamodel.DeviceDataModel;
import com.proximus.jsf.datamodel.TagDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.TagManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "tagController")
@SessionScoped
/**
 * @author Gilberto Gaxiola
 */
public class TagController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    //GENERAL
    private List<String> listTags;
    private String tagName;
    private TagDataModel tagModel;
    private List<Tag> filteredTags;
    private Tag selectedTag;
    private Tag newTag;
    private String filterInput;
    @EJB
    TagManagerLocal tagFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    //TAG DEVICES
    private Tag deviceTag;
    private Device[] selectedDevices;
    private DeviceDataModel deviceModel;
    private List<Device> filteredDevices;
    private @EJB
    DeviceManagerLocal deviceFacade;
    //TAG CAMPAIGNS
    private Tag campaignTag;
    private CampaignDataModel campaignModel;
    private List<Campaign> filteredCampaigns;
    private Campaign[] selectedCampaigns;
    private String campaignFilterQuery;
    @EJB
    CampaignManagerLocal campaignFacade;
    private ResourceBundle message;
    private boolean link = false;

    public TagController() {
        message = this.getHttpSession().getMessages();
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public void linkOn() {
        this.link = true;
    }

    public void linkOff() {
        this.link = false;
    }

    public void deleteTag() {
        try {
            if (selectedTag.getDevices() != null && selectedTag.getDevices().size() > 0) {
                JsfUtil.addErrorMessage(message.getString("cantDeleteTag") + " " + selectedTag.getName() + ".\n" + message.getString("devicesStillAssociatedToTag"));
                return;
            }
            tagFacade.delete(selectedTag);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("cantDeleteTag") + " " + selectedTag.getName() + ".\n" + message.getString("devicesStillAssociatedToTag"));
        }
        prepareList();
    }

    public void createNewTag() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;

        if (newTag == null || newTag.getName() == null || newTag.getName().isEmpty()) {
            newTag = new Tag();
            validation = false;
            JsfUtil.addErrorMessage(message.getString("pleaseEnterDeviceGroupName"));
            context.addCallbackParam("validation", validation);
            return;
        }
        List<String> tagNames = new ArrayList<String>();
        if (getTagModel().getTagData() != null && !getTagModel().getTagData().isEmpty()) {
            for (Tag t : tagModel.getTagData()) {
                tagNames.add(t.getName().toLowerCase());
            }

            if (tagNames.contains(newTag.getName().toLowerCase())) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("duplicatedDeviceGroup"));
                context.addCallbackParam("validation", validation);
                newTag = new Tag();
                return;
            }
        }
        newTag.setCompany(companyFacade.find(getCompanyIdFromSession()));
        tagFacade.create(newTag);
        JsfUtil.addSuccessMessage(message.getString("tagCreated"));
        validation = true;
        context.addCallbackParam("validation", validation);
        newTag = new Tag();
        tagModel = null;
        prepareTagToDevice();
    }

    public void editTag() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;

        try {
            if (selectedTag.getName() == null || selectedTag.getName().isEmpty()) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("pleaseEnterName"));
                context.addCallbackParam("validation", validation);
                this.listTags = null;
                return;
            }
            tagFacade.update(selectedTag);
            validation = true;
            context.addCallbackParam("validation", validation);
        } catch (Exception e) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("cantPersistTag"));
            context.addCallbackParam("validation", validation);
            this.listTags = null;
            return;
        }
    }

    public Tag getNewTag() {
        if (newTag == null) {
            newTag = new Tag();
        }
        return newTag;
    }

    public void setNewTag(Tag newTag) {
        this.newTag = newTag;
    }

    public Tag getSelectedTag() {
        if (selectedTag == null) {
            selectedTag = new Tag();
        }
        return selectedTag;
    }

    public void setSelectedTag(Tag selectedTag) {
        this.selectedTag = selectedTag;
    }

    public TagDataModel getTagModel() {
        if (tagModel == null) {
            populateTagModel();
        }
        return tagModel;
    }

    public void setTagModel(TagDataModel tagModel) {
        this.tagModel = tagModel;
    }

    public List<Tag> getFilteredTags() {
        return filteredTags;
    }

    public void setFilteredTags(List<Tag> filteredTags) {
        this.filteredTags = filteredTags;
    }

    public String getCampaignFilterQuery() {
        return campaignFilterQuery;
    }

    public void setCampaignFilterQuery(String campaignFilterQuery) {
        this.campaignFilterQuery = campaignFilterQuery;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        if (!this.listTags.contains(tagName.toLowerCase())) {
            this.tagName = null;
        } else {
            this.tagName = tagName;
        }
    }

    @Deprecated
    public void updatePickList() {
        populateCampaignModel();
    }

    public List<String> getListTags() {
        if (listTags == null) {
            listTags = this.populateListTags();
        }
        return listTags;
    }

    public void setListTags(List<String> listTags) {
        this.listTags = listTags;
    }

    /**
     * Return the filtered list on auto complete fields
     *
     * @param query
     * @return
     */
    public List<String> tagComplete(String query) {
        query = query.toUpperCase();
        if (listTags == null) {
            listTags = populateListTags();
        }
        if (query.isEmpty()) {
            return listTags;
        }

        List<String> filtered = new ArrayList<String>();
        for (String s : listTags) {
            if (s.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    public Tag getCampaignTag() {
        if (campaignTag == null) {
            campaignTag = new Tag();
        }
        return campaignTag;
    }

    public void setCampaignTag(Tag campaignTag) {
        this.campaignTag = campaignTag;
    }

    public void setDeviceTag(Tag tag) {
        this.deviceTag = tag;
    }

    public Tag getDeviceTag() {
        if (deviceTag == null) {
            deviceTag = new Tag();
        }
        return deviceTag;
    }

    public DeviceDataModel getDeviceModel() {
        if (this.deviceModel == null) {
            populateDeviceModel();
        }

        return this.deviceModel;
    }

    public void setDeviceModel(DeviceDataModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public List<Device> getFilteredDevices() {
        return filteredDevices;
    }

    public void setFilteredDevices(List<Device> filteredDevices) {
        this.filteredDevices = filteredDevices;
    }

    public List<String> populateListTags() {
        return tagFacade.getTagNamesByCompany(companyFacade.find(this.getCompanyIdFromSession()));
    }

    private void populateDeviceModel() {
        deviceModel = new DeviceDataModel(deviceFacade.getDeviceByCompany(companyFacade.find(getCompanyIdFromSession())));
    }

    private void populateCampaignModel() {

        List<Campaign> campaigns = campaignFacade.findAllByCompanyActive(companyFacade.find(getCompanyIdFromSession()));
        campaignModel = new CampaignDataModel(campaigns);
        filteredCampaigns = new ArrayList<Campaign>(campaigns);
    }

    private void populateTagModel() {
        List<Tag> tags = tagFacade.findAllByCompany(companyFacade.find(getCompanyIdFromSession())); 
        tagModel = new TagDataModel(tags);
        if(tags!= null) {
        filteredTags = new ArrayList<Tag>(tags);
        } else {
            filteredTags = new ArrayList<Tag>();
        }
    }

    public Device[] getSelectedDevices() {

        return selectedDevices;
    }

    public void setSelectedDevices(Device[] devices) {
        this.selectedDevices = devices;
    }

    public String getFilterInput() {
        return filterInput;
    }

    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    /**
     * Saving the tag to the selected Campaigns
     *
     * @param tag
     */
    public void saveToCampaigns(Tag tag) {
        if (selectedCampaigns == null || selectedCampaigns.length == 0) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectCampaignToLink"));
            return;
        }

        System.out.println("tag is: " + tag);
        if (tag == null || tag.getName() == null || tag.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectDeviceGroupToLink"));
            return;
        }

        Tag t = tagFacade.findByName(tag.getName(), companyFacade.find(this.getCompanyIdFromSession()));
        if (t == null) {
            return;
        } else {
            tag = t;
        }
        //Checking if any of the campaigns is getting updated
        boolean tagChanged = false;

        for (Campaign c : selectedCampaigns) {
            if (!c.getTags().contains(tag)) {
                c.addTag(tag);
                tag.addCampaign(c);
                campaignFacade.update(c);
                tagChanged = true;
            }
        }
        if (tagChanged) {
            tagFacade.update(tag);
            JsfUtil.addSuccessMessage(message.getString("campaignLinked"));
            this.prepareTagToCampaign();
        }

    }

    /**
     * Deleting the tag from the selected Campaigns
     *
     * @param tag
     */
    public void deleteFromCampaigns(Tag tag) {
        if (selectedCampaigns == null || selectedCampaigns.length == 0) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectCampaignUnlink"));
            return;
        }

        if (tag == null || tag.getName() == null || tag.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectGroupDeviceToUnlink"));
            return;
        }

        Tag t = tagFacade.findByName(tag.getName(), companyFacade.find(this.getCompanyIdFromSession()));
        if (t == null) {
            return;
        } else {
            tag = t;
        }
        //Checking if any of the campaigns is getting updated
        boolean tagChanged = false;

        for (Campaign c : selectedCampaigns) {
            if (c.getTags().contains(tag)) {
                c.removeTag(tag);
                tag.removeCampaign(c);
                campaignFacade.update(c);
                tagChanged = true;
            }
        }
        if (tagChanged) {
            tagFacade.update(tag);
            JsfUtil.addSuccessMessage(message.getString("campaignUnlinked"));
            this.prepareTagToCampaign();
        }

    }

    /**
     * Saving the tag to the selected Devices
     *
     * @param tag
     */
    public void saveToDevices(Tag tag) {
        if (selectedDevices == null || selectedDevices.length == 0) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectDevice"));
            return;
        }

        if (tag == null || tag.getName() == null || tag.getName().isEmpty()) {
            JsfUtil.addErrorMessage(message.getString("pleaseSelectDeviceGroupName"));
            return;
        }

        Tag t = tagFacade.findByName(tag.getName(), companyFacade.find(this.getCompanyIdFromSession()));
        if (t == null) {
            return;
        } else {
            tag = t;
        }

        //Checking if any of the campaigns is getting updated
        boolean tagChanged = false;

        for (Device device : selectedDevices) {
            if (device.getTag() == null || !device.getTag().equals(tag)) {
                device.setTag(tag);
                tag.addDevice(device);
                deviceFacade.update(device);
                tagChanged = true;
            }
        }

        if (tagChanged) {
            tagFacade.update(tag);
            JsfUtil.addSuccessMessage(message.getString("deviceSuccessfullyLinked"));
            prepareTagToDevice();
        }
    }

    /**
     * Deleting the tag from the selected Devices
     *
     * @param tag
     */
    public void deleteFromDevices() {
        if (selectedDevices == null || selectedDevices.length == 0) {
            JsfUtil.addErrorMessage(message.getString("noDeviceSelected"));
            return;
        }

        //Checking if any of the devices is getting updated
        boolean tagChanged = false;
        for (Device device : selectedDevices) {
            if (device.getTag() != null) {
                Tag tag = device.getTag();
                device.setTag(null);
                tag.removeDevice(device);
                deviceFacade.update(device);
                tagFacade.update(tag);
                tagChanged = true;
            }
        }

        if (tagChanged) {
            JsfUtil.addSuccessMessage(message.getString("deviceSuccessfullyLinked"));
            prepareTagToDevice();
        }
    }

    public CampaignDataModel getCampaignModel() {
        if (campaignModel == null) {
            populateCampaignModel();
        }
        return campaignModel;
    }

    public void setCampaignModel(CampaignDataModel campaignModel) {
        this.campaignModel = campaignModel;
    }

    public List<Campaign> getFilteredCampaigns() {
        return filteredCampaigns;
    }

    public void setFilteredCampaigns(List<Campaign> filteredCampaigns) {
        this.filteredCampaigns = filteredCampaigns;
    }

    public Campaign[] getSelectedCampaigns() {
        return selectedCampaigns;
    }

    public void setSelectedCampaigns(Campaign[] selectedCampaigns) {
        this.selectedCampaigns = selectedCampaigns;
    }

    private void prepareVars() {
        listTags = populateListTags();
        tagModel = null;
        deviceTag = new Tag();
        campaignTag = new Tag();
        deviceModel = null;
        selectedDevices = null;
        selectedTag = null;
        newTag = null;
        tagName = null;
        campaignModel = null;
        selectedCampaigns = null;
    }

    public String prepareTagToDevice() {
        prepareVars();
        return "/tag/TagDevice?faces-redirect=true";
    }

    public String prepareTagToCampaign() {
        prepareVars();
        return "/tag/TagCampaign?faces-redirect=true";
    }

    public String prepareList() {
        prepareVars();
        return "/tag/List?faces-redirect=true";
    }
}
