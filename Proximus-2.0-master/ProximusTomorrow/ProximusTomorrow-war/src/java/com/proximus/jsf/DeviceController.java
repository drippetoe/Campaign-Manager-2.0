package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.ShellCommandAction;
import com.proximus.data.Tag;
import com.proximus.jsf.datamodel.DeviceDataModel;
import com.proximus.jsf.util.JsfUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.ShellCommandActionManagerLocal;
import com.proximus.manager.TagManagerLocal;
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
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "deviceController")
@SessionScoped
public class DeviceController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    private Device selectedDevice;
    private DeviceDataModel deviceModel;
    private List<Device> filteredDevices;
    private Device createdDevice;
    private Device newDevice;
    private String tagName;
    private Company selectedCompany;
    private List<Company> companyList;
    private ResourceBundle message;
    @EJB
    DeviceManagerLocal deviceFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    ShellCommandActionManagerLocal shellFacade;
    @EJB
    TagManagerLocal tagFacade;
    private String filterInput;

    public DeviceController() {
        message = this.getHttpSession().getMessages();
    }

    public List<Device> getFilteredDevices() {
        return filteredDevices;
    }

    public void setFilteredDevices(List<Device> filteredDevices) {
        this.filteredDevices = filteredDevices;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Company getSelectedCompany() {
        if (selectedCompany == null) {
            selectedCompany = new Company();
        }
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public Device getCreatedDevice() {
        if (createdDevice == null) {
            createdDevice = new Device();
            createdDevice.setWifiChannel(6);
        }
        return createdDevice;
    }

    public void setCreatedDevice(Device createdDevice) {
        this.createdDevice = createdDevice;
    }

    public Device getNewDevice() {
        if (newDevice == null) {
            newDevice = new Device();
        }
        return newDevice;
    }

    public void setNewDevice(Device newDevice) {
        this.newDevice = newDevice;
    }

    public DeviceDataModel getDeviceModel() {
        if (deviceModel == null) {
            populateDeviceModel();
        }
        return deviceModel;
    }

    public void setDeviceModel(DeviceDataModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Device getSelectedDevice() {
        if (selectedDevice == null) {
            selectedDevice = new Device();
        }
        return selectedDevice;
    }

    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public List<Company> getCompanyList() {
        if (companyList == null) {
            this.populateCompanyList();
        }
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public String getFilterInput() {
        return filterInput;
    }

    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    public void populateDeviceModel() {
        List<Device> devices  = deviceFacade.findAllActive(companyFacade.find(getCompanyIdFromSession()));
        deviceModel = new DeviceDataModel(devices);
        filteredDevices = new ArrayList<Device>(devices);
    }

    /**
     * send a shellCommandAction of reboot (i.e. bash reboot)
     *
     * @param event
     */
    public void rebootDevice(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        if (selectedDevice == null) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("mustSelectDevice"));
            context.addCallbackParam("validation", validation);
            return;
        }
        ShellCommandAction action = new ShellCommandAction();
        action.setCommand("bash");
        action.setParameters("reboot");
        action.setCompleted(false);
        action.setDevice_id(selectedDevice.getId());
        shellFacade.update(action);
        validation = true;
        JsfUtil.addSuccessMessage("Rebooting " + selectedDevice.getName());
        context.addCallbackParam("validation", validation);
    }

    /**
     * send LED Locate script
     *
     * @param event
     */
    public void locateLED(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        if (selectedDevice == null) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("mustSelectDevice"));
            context.addCallbackParam("validation", validation);
            return;
        }
        ShellCommandAction action = new ShellCommandAction();
        //TODO: not sure Dream Plug will understand this command
        action.setCommand("ledshell");
        action.setParameters("locate");
        action.setCompleted(false);
        action.setDevice_id(selectedDevice.getId());
        shellFacade.update(action);
        validation = true;
        JsfUtil.addSuccessMessage("Led Locate " + selectedDevice.getName());
        context.addCallbackParam("validation", validation);
    }

    /**
     * This function allows a device to be moved to a new company while
     * preventing null pointer exceptions in reporting
     */
    public void changeCompany() {
        newDevice.setActive(selectedDevice.getActive());
        newDevice.setBuild(selectedDevice.getBuild());
        newDevice.setKeepAlive(selectedDevice.getKeepAlive());
        newDevice.setKernel(selectedDevice.getKernel());
        newDevice.setMacAddress(selectedDevice.getMacAddress());
        newDevice.setMajor(selectedDevice.getMajor());
        newDevice.setMinor(selectedDevice.getMinor());
        newDevice.setPlatform(selectedDevice.getPlatform());
        newDevice.setReconnectInterval(selectedDevice.getReconnectInterval());
        newDevice.setRegistrationDate(selectedDevice.getRegistrationDate());
        newDevice.setRotation(selectedDevice.getRotation());
        newDevice.setSerialNumber(selectedDevice.getSerialNumber());
        newDevice.setToken(selectedDevice.getToken());
        newDevice.setWifiChannel(selectedDevice.getWifiChannel());

        String time = System.currentTimeMillis()/1000 + "";
        String mac = "PROX-" + time;
        selectedDevice.setMacAddress(mac);
        selectedDevice.setToken(null);
        if (selectedCompany != null || !selectedCompany.getName().isEmpty()) {
            Company realCompany = companyFacade.getCompanybyName(selectedCompany.getName());
            newDevice.setCompany(realCompany);
        }
        String serial = "INVALID-" + time;
        selectedDevice.setSerialNumber(serial);
        selectedDevice.setActive(false);
        deviceFacade.update(selectedDevice);
        deviceFacade.create(newDevice);
    }

    /**
     * port are going to be open by a starting (4 or 5) + the 4 last digits of
     * the serial number
     *
     * @param event
     */
    public void openSSH(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        if (selectedDevice == null) {
            validation = false;
            JsfUtil.addErrorMessage(message.getString("mustSelectDevice"));
            context.addCallbackParam("validation", validation);
            return;
        }

        String port = (System.currentTimeMillis() % 2 == 0) ? "5" : "4";
        if (selectedDevice.getSerialNumber() == null || selectedDevice.getSerialNumber().isEmpty()) {
            //choosing a random 4 digit to add to the 5 or 4
            port += System.currentTimeMillis() % 10000;
        } else {
            String serialNumber = selectedDevice.getSerialNumber().replaceAll("\\D+", "");
            if (serialNumber.length() == 0) {
                //serial number doesn't have digits we choose a random port
                port += System.currentTimeMillis() % 10000;
            } else {
                String lastFour = selectedDevice.getSerialNumber().substring(selectedDevice.getSerialNumber().length() - 4, selectedDevice.getSerialNumber().length());
                port += lastFour;
            }
        }
        try {
            System.out.println("Port is: " + port);
            Integer p = Integer.parseInt(port);
            if (p > 1024 && p < 65536) {
                shellFacade.openSSHFor(selectedDevice, port);
                validation = true;
                JsfUtil.addSuccessMessage("Reverse Ssh Port: " + p + " for " + selectedDevice.getName());
                context.addCallbackParam("validation", validation);
                prepareList();
            } else {
                validation = false;
                JsfUtil.addErrorMessage("Port must be between 1024 and 65536.");
                context.addCallbackParam("validation", validation);
                return;
            }
        } catch (Exception e) {
            validation = false;
            JsfUtil.addErrorMessage("Port must be between 1024 and 65536.");
            JsfUtil.addErrorMessage(e.getMessage());
            context.addCallbackParam("validation", validation);
            return;
        }
    }

    public void editDevice(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean validation = false;
        try {
            if (selectedDevice.getName() == null || selectedDevice.getName().isEmpty()) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("deviceNameEmpty"));
                context.addCallbackParam("validation", validation); 
               return;
            }
            if (selectedDevice.getWifiChannel() == null || selectedDevice.getWifiChannel() == 0) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("pleaseSelectWifi"));
                context.addCallbackParam("validation", validation);
                return;
            }

            if (selectedDevice.getSerialNumber() == null || selectedDevice.getSerialNumber().isEmpty()) {
                validation = false;
                JsfUtil.addErrorMessage(message.getString("deviceSerialEmpty"));
                context.addCallbackParam("validation", validation);
                return;
            }

            if (tagName == null || tagName.isEmpty()) {
                Tag t = selectedDevice.getTag();
                selectedDevice.setTag(null);
                if (t != null) {
                    t.removeDevice(selectedDevice);
                    tagFacade.update(t);
                }
            } else {
                Tag t = tagFacade.findByName(tagName, companyFacade.find(this.getCompanyIdFromSession()));
                if (t == null) {
                    return;
                } else {

                    if (selectedDevice.getTag() == null || !selectedDevice.getTag().equals(t)) {
                        Tag oldTag = (selectedDevice != null) ? selectedDevice.getTag() : null;
                        if (oldTag != null) {
                            oldTag.removeDevice(selectedDevice);
                            tagFacade.update(oldTag);
                        }
                        selectedDevice.setTag(t);
                        t.addDevice(selectedDevice);
                        tagFacade.update(t);
                    }
                }
            }
            deviceFacade.update(selectedDevice);
            validation = true;
            context.addCallbackParam("validation", validation);
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(message.getString("devicePersistError"));
            validation = false;
            context.addCallbackParam("validation", validation);
            prepareList();
        }
    }

    public void save(Device device) {
        if (device.getId() == 0) {
            Company companybyId = companyFacade.find(getCompanyIdFromSession());
            device.setCompany(companybyId);

            // set some sensible defaults
            device.setReconnectInterval(300000L); // 5 min reconnect interval
            device.setKeepAlive(20000L); // HTTP keep-alive 20 seconds
            device.setRotation(14400000L); // 4 hour log rotation
            device.setBuild(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("BuildNumber")));
            device.setMajor(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("MajorVersion")));
            device.setMinor(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("MinorVersion")));

            deviceFacade.create(device);
            JsfUtil.addSuccessMessage(message.getString("deviceCreated"));
            prepareList();
            prepareCreate();
        } else {
            deviceFacade.update(device);
        }
    }

    public String prepareCreate() {
        createdDevice = new Device();
        createdDevice.setWifiChannel(6);
        return "/device/Create?faces-redirect=true";
    }

    public String prepareList() {
        newDevice = new Device();
        deviceModel = null;
        selectedCompany = null;
        selectedDevice = null;
        tagName = null;
        return "/device/List?faces-redirect=true";
    }

    private void populateCompanyList() {
        companyList = companyFacade.findAll();
    }

    @FacesConverter(forClass = Device.class, value = "deviceControllerConverter")
    public static class DeviceControllerConverter implements Converter, Serializable {

        private static final Logger logger = Logger.getLogger(DeviceControllerConverter.class.getName());

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                Long id = Long.parseLong(value);

                Device controller = ((DeviceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "deviceController")).deviceFacade.find(id);
                logger.info("Converted " + value + " to " + controller);
                return controller;
            } catch (NumberFormatException e) {
                return new Device();
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
            if (value == null || value.toString().isEmpty() || !(value instanceof Device)) {
                return null;
            }
            return String.valueOf(((Device) value).getId());
        }
    }
}
