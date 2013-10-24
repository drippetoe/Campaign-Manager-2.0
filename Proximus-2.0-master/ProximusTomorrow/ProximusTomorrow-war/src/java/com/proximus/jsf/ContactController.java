/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jsf;

import com.proximus.data.Contact;
import com.proximus.data.Device;
import com.proximus.data.sms.Country;
import com.proximus.jsf.datamodel.ContactDataModel;
import com.proximus.jsf.datamodel.DeviceDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.ContactManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.sms.CountryManagerLocal;
import com.proximus.util.ListChoosers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "contactController")
@SessionScoped
public class ContactController extends AbstractController implements Serializable {

    private List<String> listContacts;
    private String contactEmail;
    private Contact newContact;
    private Contact selectedContact;
    private ContactDataModel contactModel;
    private List<Contact> filteredContacts;
    private Contact deviceContact;
    private Device[] selectedDevices;
    private DeviceDataModel deviceModel;
    private List<Device> filteredDevices;
    @EJB
    DeviceManagerLocal deviceFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    ContactManagerLocal contactFacade;
    @EJB
    CountryManagerLocal countryMgr;
    private String filterInput;
    private List<String> selectedCountries;
    private List<String> selectedStates = ListChoosers.getStates();
    private final String EMAIL_REGEX = "^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
    public final Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
    private ResourceBundle message;

    public ContactController() {
        this.message = this.getHttpSession().getMessages();
    }

    private boolean isValidEmailAddress(String emailAddress) {
        CharSequence inputStr = emailAddress;
        return pattern.matcher(inputStr).matches();

    }

    public void deleteContact() {
        if (selectedContact == null) {
            return;
        }
        try {
            contactFacade.delete(selectedContact);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(this.message.getString("contactDeleteError") + ": " + selectedContact.getEmail() + ".\n" + this.message.getString("contactAssociatedDeleteError"));
        }
        prepareList();
    }

    public void createNewContact() {
        RequestContext context = RequestContext.getCurrentInstance();

        boolean validation = false;

        if (newContact == null || newContact.getEmail() == null || !this.isValidEmailAddress(newContact.getEmail())) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("contactValidEmail"));
            context.addCallbackParam("validation", validation);
            newContact = new Contact();
            newContact.setCountry("USA");
            return;
        }
        List<String> contactEmails = new ArrayList<String>();
        if (getContactModel().getContactData() != null && !getContactModel().getContactData().isEmpty()) {
            for (Contact co : contactModel.getContactData()) {
                contactEmails.add(co.getEmail());
            }
            if (contactEmails.contains(newContact.getEmail())) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("contactDuplicatedError"));
                context.addCallbackParam("validation", validation);
                newContact = new Contact();
                newContact.setCountry("USA");
                return;
            }
        }
        newContact.setCompany(companyFacade.find(getCompanyIdFromSession()));
        contactFacade.create(newContact);
        validation = true;
        context.addCallbackParam("validation", validation);
        JsfUtil.addSuccessMessage(message.getString("contactCreated"));
        newContact = new Contact();
        newContact.setCountry("USA");
        contactModel = null;
        prepareContactToDevice();
    }

    public Contact getNewContact() {
        if (newContact == null) {
            newContact = new Contact();
            newContact.setCountry("USA");
        }
        return newContact;
    }

    public void setNewContact(Contact newContact) {
        this.newContact = newContact;
    }

    public Contact getSelectedContact() {
        if (selectedContact == null) {
            selectedContact = new Contact();
        }
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public ContactDataModel getContactModel() {
        if (contactModel == null) {
            populateContactModel();
        }
        return contactModel;
    }

    public List<Contact> getFilteredContacts() {
        return filteredContacts;
    }

    public void setFilteredContacts(List<Contact> filteredContacts) {
        this.filteredContacts = filteredContacts;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        contactEmail = contactEmail.toUpperCase();
        if (!this.listContacts.contains(contactEmail)) {
            this.contactEmail = null;
        } else {
            this.contactEmail = contactEmail;
        }
    }

    public List<String> getListContacts() {
        if (listContacts == null) {
            listContacts = populateContactList();
        }
        return listContacts;
    }

    public void setListContacts(List<String> listContacts) {
        this.listContacts = listContacts;
    }

    /**
     * Return the filtered list on auto complete fields
     *
     * @param query
     * @return
     */
    public List<String> contactComplete(String query) {
        query = query.toUpperCase();
        if (listContacts == null) {
            listContacts = populateContactList();
            if (listContacts == null) {
                return null;
            }
        }

        List<String> filtered = new ArrayList<String>();
        for (String s : listContacts) {
            if (s.toUpperCase().contains(query)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    public Device[] getSelectedDevices() {

        return selectedDevices;
    }

    public void setSelectedDevices(Device[] devices) {
        this.selectedDevices = devices;
    }

    public Contact getDeviceContact() {
        if (deviceContact == null) {
            deviceContact = new Contact();
        }
        return deviceContact;
    }

    public void setDeviceContact(Contact deviceContact) {
        this.deviceContact = deviceContact;
    }

    private void populateDeviceModel() {
        List<Device> devices = deviceFacade.getDeviceByCompany(companyFacade.find(getCompanyIdFromSession()));
        deviceModel = new DeviceDataModel(devices);
        filteredDevices = new ArrayList<Device>(devices);
    }

    public void populateContactModel() {
        List<Contact> contacts = contactFacade.findAllByCompany(companyFacade.find(getCompanyIdFromSession()));
        contactModel = new ContactDataModel(contacts);
        filteredContacts = new ArrayList<Contact>(contacts);
    }

    public void setContactModel(ContactDataModel contactModel) {
        this.contactModel = contactModel;
    }

    public String getFilterInput() {
        return filterInput;
    }

    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    public List<String> getSelectedCountries() {
        if (selectedCountries == null) {
            List<Country> list = countryMgr.findAllSorted();
            selectedCountries = new ArrayList<String>();
            for (Country c : list) {
                selectedCountries.add(c.getName());
            }
        }
        return selectedCountries;
    }

    public void setSelectedCountries(List<String> selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public List<String> getSelectedStates() {
        return selectedStates;
    }

    public void setSelectedStates(List<String> selectedStates) {
        this.selectedStates = selectedStates;
    }

    public void changeSelectedContact(Contact contact) {
        this.selectedContact = contact;
    }

    /**
     * Saving the contact to the selected Devices
     *
     * @param contact
     */
    public void saveToDevices(Contact contact) {
        if (selectedDevices == null || selectedDevices.length == 0) {
            JsfUtil.addErrorMessage(message.getString("contactDuplicatedError"));
            return;
        }
        if (contact.getEmail().isEmpty() || contact.getEmail().matches("\\s+")) {
            JsfUtil.addErrorMessage(message.getString("contactNotSelected"));
            return;
        }
        Contact c = contactFacade.getContactByEmail(contact.getEmail(), companyFacade.find(this.getCompanyIdFromSession()));
        if (c == null) {
            return;
        } else {
            contact = c;
        }

        boolean contactChanged = false;

        for (Device device : selectedDevices) {
            if (device.getContact() == null || !device.getContact().equals(contact)) {
                device.setContact(contact);
                contact.addDevice(device);
                deviceFacade.update(device);
                contactChanged = true;
            }
        }

        if (contactChanged) {
            contactFacade.update(contact);
            JsfUtil.addSuccessMessage(message.getString("contactLinkSuccess"));
            prepareContactToDevice();
        }
    }

    /**
     * Deleting the contact from the selected Devices
     *
     * @param contact
     */
    public void deleteFromDevices() {
        if (selectedDevices == null || selectedDevices.length == 0) {
            JsfUtil.addErrorMessage(message.getString("contactDeviceUnlinked"));
            return;
        }
        boolean contactChanged = false;
        for (Device device : selectedDevices) {
            Contact contact = device.getContact();
            if (contact != null) {
                contact.removeDevice(device);
                device.setContact(null);
                deviceFacade.update(device);
                contactFacade.update(contact);
                contactChanged = true;
            }

        }

        if (contactChanged) {
            JsfUtil.addSuccessMessage(message.getString("contactUnlinkSuccess"));
            prepareContactToDevice();
        }
    }

    public void editContact() {
        RequestContext context = RequestContext.getCurrentInstance();

        boolean validation = false;
        try {
            if (selectedContact == null || selectedContact.getEmail() == null || !this.isValidEmailAddress(selectedContact.getEmail())) {
                JsfUtil.addErrorMessage(message.getString("contactValidEmail"));
                context.addCallbackParam("validation", validation);
                return;
            }
            contactFacade.update(selectedContact);
            this.listContacts = null;
            validation = true;
            context.addCallbackParam("validation", validation);
        } catch (Exception e) {
            prepareList();
            JsfUtil.addErrorMessage(message.getString("contactPersistError"));
            context.addCallbackParam("validation", validation);
        }
    }

    public String prepareCreate() {
        newContact = new Contact();
        newContact.setCountry("USA");
        return "/device/ContactDevice?faces-redirect=true";
    }

    public List<String> populateContactList() {
        return contactFacade.getContactEmailByCompany(companyFacade.find(this.getCompanyIdFromSession()));
    }

    private void prepareVars() {
        listContacts = null;
        contactModel = null;
        deviceContact = new Contact();
        deviceModel = null;
        selectedDevices = null;
        selectedContact = new Contact();
        newContact = null;
        contactEmail = null;
    }

    public String prepareContactToDevice() {
        prepareVars();
        return "/device/ContactDevice?faces-redirect=true";
    }

    public String prepareList() {
        prepareVars();
        return "/contact/List?faces-redirect=true";
    }

    @FacesConverter(forClass = Contact.class, value = "contactControllerConverter")
    public static class ContactControllerConverter implements Converter, Serializable {

        private static final Logger logger = Logger.getLogger(ContactController.ContactControllerConverter.class.getName());

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);

                Contact controller = ((ContactController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "contactController")).contactFacade.find(id);
                logger.info("Converted " + value + " to " + controller);
                return controller;
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Value not found"));
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
            if (value == null || value.toString().isEmpty() || !(value instanceof Contact)) {
                return null;
            }
            return String.valueOf(((Contact) value).getId());
        }
    }
}
