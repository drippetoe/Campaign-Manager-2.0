package com.proximus.jsf;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.DeviceInLimbo;
import com.proximus.jsf.datamodel.DeviceInLimboDataModel;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceInLimboManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.util.TimeConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author Gilberto Gaxiola
 */
@ManagedBean(name = "deviceInLimboController")
@SessionScoped
public class DeviceInLimboController extends AbstractController implements Serializable {

    private static final long serialVersionUID = 1;
    private DeviceInLimbo selectedDevice;
    private DeviceInLimboDataModel deviceModel;
    private List<DeviceInLimbo>filteredDevices;
    private Company selectedCompany;
    @EJB
    DeviceManagerLocal deviceFacade;
    @EJB
    DeviceInLimboManagerLocal deviceInLimboFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    private String filterInput;
    private String newName;

    public DeviceInLimboController() {
    }

    public Company getSelectedCompany() {
        if (selectedCompany == null) {
            this.selectedCompany = companyFacade.find(getCompanyIdFromSession());
        }
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public DeviceInLimbo getSelectedDevice() {
        if (selectedDevice == null) {
            selectedDevice = new DeviceInLimbo();
        }
        return selectedDevice;
    }

    public void setSelectedDevice(DeviceInLimbo selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public DeviceInLimboDataModel getDeviceModel() {
        if (deviceModel == null) {
            populateDeviceInLimboModel();
        }
        return deviceModel;
    }

    public void setDeviceModel(DeviceInLimboDataModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public List<DeviceInLimbo> getFilteredDevices() {
        return filteredDevices;
    }

    public void setFilteredDevices(List<DeviceInLimbo> filteredDevices) {
        this.filteredDevices = filteredDevices;
    }

    private void populateDeviceInLimboModel() {
        List<DeviceInLimbo> devices = deviceInLimboFacade.findAll();
        deviceModel = new DeviceInLimboDataModel(devices);
        filteredDevices = new ArrayList<DeviceInLimbo>(devices);
    }

    public String getFilterInput() {
        return filterInput;
    }

    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    public void updateCompany(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        Company c = companyFacade.getCompanybyName(newValue);
        if (c != null) {
            this.selectedCompany = c;
            updateName();
        }

    }

    public void updateName() {
        if (selectedDevice != null && selectedDevice.getMacAddress() != null) {
            String serial = "";
            serial += getSelectedDevice().getSerialNumber();
            serial = serial.substring(serial.length() - 4);
            newName = getSelectedCompany().getName() + " " + serial;
        }
    }

    public String getNewName() {
        if (newName == null || newName.isEmpty()) {
            updateName();
        }
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String prepareList() {
        this.selectedCompany = companyFacade.find(getCompanyIdFromSession());
        deviceModel = null;
        selectedDevice = null;
        newName = null;
        populateDeviceInLimboModel();
        return "/device/InLimbo?faces-redirect=true";
    }

    public void deleteDevice() {
        if (selectedDevice != null) {
            deviceInLimboFacade.delete(selectedDevice);
        }
        prepareList();
    }
    
    
    public void clearAll(){
        deviceInLimboFacade.truncateDevicesInLimbo();
        populateDeviceInLimboModel();
    }

    public boolean deviceExists() {
        if (selectedDevice != null) {
            if (selectedDevice.getMacAddress() != null) {
                Device d = deviceFacade.getDeviceByMacAddress(selectedDevice.getMacAddress());
                if (d != null) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void syncDevice() {
        if (selectedDevice != null) {
            Device newDevice = new Device();
            newDevice.setSerialNumber(selectedDevice.getSerialNumber());
            newDevice.setMacAddress(selectedDevice.getMacAddress());
            newDevice.setKernel(selectedDevice.getKernel());
            newDevice.setMajor(selectedDevice.getMajor());
            newDevice.setMinor(selectedDevice.getMinor());
            newDevice.setBuild(selectedDevice.getBuild());
            newDevice.setLastIpAddress(selectedDevice.getLastIpAddress());
            newDevice.setCompany(selectedCompany);
            newDevice.setLastSeen(selectedDevice.getLastSeen());
            newDevice.setName(newName);
            if (selectedDevice.getToken() != null) {
                //Accept
                Device d = deviceFacade.getDeviceByMacAddress(selectedDevice.getMacAddress());
                if (d != null) {
                    d.setToken(selectedDevice.getToken());
                    d.setReconnectInterval((long)TimeConstants.THIRTY_SECONDS);
                    deviceFacade.update(d);
                } else {
                    newDevice.setToken(selectedDevice.getToken());
                    newDevice.setReconnectInterval((long)TimeConstants.THIRTY_SECONDS);
                    deviceFacade.create(newDevice);
                }
            } else {
                Device d = deviceFacade.getDeviceByMacAddress(selectedDevice.getMacAddress());
                if (d != null) {
                    //Let the device go through the registration process
                    d.setToken(null);
                    d.setReconnectInterval((long)TimeConstants.THIRTY_SECONDS);
                    deviceFacade.update(d);
                } else {
                    newDevice.setToken(null);
                    newDevice.setReconnectInterval((long)TimeConstants.THIRTY_SECONDS);
                    deviceFacade.create(newDevice);
                }
            }
            deviceInLimboFacade.delete(selectedDevice);
            populateDeviceInLimboModel();
        }
    }
}
