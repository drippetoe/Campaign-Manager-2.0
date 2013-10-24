package com.proximus.jsf;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Gilberto Gaxiola
 */
public abstract class AbstractReportController extends AbstractController {

    protected Company company;
    protected Device selectedDevice;
    protected Campaign selectedCampaign;
    protected Date startDate;
    protected Date endDate;
    protected Map<String, Campaign> campaignMap;

    public abstract void setCompany(Company c);

    public abstract void setSelectedDevice(Device d);

    public abstract void setSelectedCampaign(Campaign camp);

    public abstract Company getCompany();

    public abstract Device getSelectedDevice();

    public abstract Campaign getSelectedCampaign();

    public Date getStartDate() {
        if (startDate == null) {
            startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        }
        return startDate;
    }

    public void setStartDate(Date d) {
        if (d == null) {
            startDate = new Date(System.currentTimeMillis() - ONE_WEEK);
        } else {
            this.startDate = d;
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = getEndOfTheDay(new Date());
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = getEndOfTheDay(endDate);
    }

    /**
     * helper to get the correct filtered report criteria
     *
     * @param companyFacade
     * @param deviceFacade
     * @param campaignFacade
     */
    public void prepareReportCriteria(CompanyManagerLocal companyMgr, DeviceManagerLocal deviceMgr, CampaignManagerLocal campaignMgr) {
        if (company == null) {
            company = companyMgr.find(this.getHttpSession().getCompany_id());
        }
        if (selectedDevice != null && selectedDevice.getName() != null && !selectedDevice.getName().isEmpty()) {
            selectedDevice = deviceMgr.getDevicebyName(selectedDevice.getName());
        } else {
            selectedDevice = null;
        }
        if (selectedCampaign == null || selectedCampaign.getName() == null || selectedCampaign.getName().isEmpty()) {
            selectedCampaign = null;
        }
        endDate = getEndOfTheDay(endDate);
    }

    /**
     * Returns the given date with time set to the end of the day
     */
    private Date getEndOfTheDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
