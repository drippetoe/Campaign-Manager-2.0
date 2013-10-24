package com.proximus.jsf;

import com.proximus.data.Campaign;
import com.proximus.data.Device;
import com.proximus.data.SoftwareRelease;
import com.proximus.jsf.datamodel.DeviceDataModel;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.util.MACUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author eric
 */
@ManagedBean(name = "deviceDetailController")
@SessionScoped
public class DeviceDetailController extends AbstractController implements Serializable
{

    private static final long serialVersionUID = 1;
    private Device selectedDevice;
    private DeviceDataModel deviceModel;
    @EJB
    DeviceManagerLocal deviceFacade;
    @EJB
    CompanyManagerLocal companyFacade;
    @EJB
    CampaignManagerLocal campaignFacade;
    private List<Campaign> activeCampaigns;


    public DeviceDetailController()
    {
    }

    public CampaignManagerLocal getCampaignFacade()
    {
        return campaignFacade;
    }

    public void setCampaignFacade(CampaignManagerLocal campaignFacade)
    {
        this.campaignFacade = campaignFacade;
    }

    public CompanyManagerLocal getCompanyFacade()
    {
        return companyFacade;
    }

    public void setCompanyFacade(CompanyManagerLocal companyFacade)
    {
        this.companyFacade = companyFacade;
    }

    public DeviceManagerLocal getDeviceFacade()
    {
        return deviceFacade;
    }

    public void setDeviceFacade(DeviceManagerLocal deviceFacade)
    {
        this.deviceFacade = deviceFacade;
    }

    public DeviceDataModel getDeviceModel()
    {
        return deviceModel;
    }

    public void setDeviceModel(DeviceDataModel deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public Device getSelectedDevice()
    {
        return selectedDevice;
    }

    public void setSelectedDevice(Device selectedDevice)
    {
        this.selectedDevice = selectedDevice;
    }

    /**
     *
     */
    private void calculateActiveCampaigns()
    {
        List<Campaign> campaigns = selectedDevice.getCampaigns();
        activeCampaigns = new ArrayList<Campaign>();
        for (Campaign campaign : campaigns)
        {
            if (campaign.isActive() && !campaign.isExpired() && !campaign.isDeleted())
            {
                activeCampaigns.add(campaign);
            }
        }
    }

    public List<Campaign> getActiveCampaigns()
    {
        return activeCampaigns;
    }
    
    
    public List getPlatforms()
    {
        return Arrays.asList(SoftwareRelease.PLATFORMS);
    }
    

    public String prepareView()
    {
        if (selectedDevice != null)
        {
            calculateActiveCampaigns();
            return "/device/Detail?faces-redirect=true";
        }
        return null;
    }

    public String getReadableTimeValue(int value)
    {
        String output = String.valueOf(value);
        switch (value)
        {
            case 30 * 1000:
                output = "30 sec";
                break;
            case 3 * 60 * 1000:
                output = "3 min";
                break;
            case 5 * 60 * 1000:
                output = "5 min";
                break;
            case 15 * 60 * 1000:
                output = "15 min";
                break;
            case 30 * 60 * 1000:
                output = "30 min";
                break;
            case 45 * 60 * 1000:
                output = "45 min";
                break;
            case 60 * 60 * 1000:
                output = "1 hr";
                break;
            case 4 * 60 * 60 * 1000:
                output = "4 hrs";
                break;
            case 6 * 60 * 60 * 1000:
                output = "6 hrs";
                break;
            case 12 * 60 * 60 * 1000:
                output = "12 hrs";
                break;
            case 24 * 60 * 60 * 1000:
                output = "24 hrs";
                break;
            default:
                break;
        }
        return output;
    }

    public String getLogRotation()
    {
        String out  = getReadableTimeValue(Integer.parseInt(selectedDevice.getRotation().toString()));
        return out;
    }

    public String getReconnectInterval()
    {
        String out  = getReadableTimeValue(Integer.parseInt(selectedDevice.getReconnectInterval().toString()));
        return out;
    }
    
    public String getMacAddress(){
        return MACUtil.colonize(selectedDevice.getMacAddress());
    }
}
