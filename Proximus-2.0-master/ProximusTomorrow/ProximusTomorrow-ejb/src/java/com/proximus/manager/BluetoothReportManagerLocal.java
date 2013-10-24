package com.proximus.manager;

import com.proximus.data.*;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.BluetoothFileSendSummary;
import java.util.Date;
import java.util.List;

/**
 *
 * @author dshaw
 */
public interface BluetoothReportManagerLocal {

    public void createBluetoothSend(BluetoothSend entry);

    public void deleteBluetoothSend(BluetoothSend entry);

    public List<BluetoothSend> getAllUniqueDevicesSeen(Date start, Date end, Company company, Campaign campaign, Device device);

    public void createBluetoothDwell(BluetoothDwell entry);

    public Long getTotalDevicesSeen(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getUniqueDevicesSeen(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getUniqueDevicesSupportingBluetooth(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getUniqueDevicesAcceptingPush(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getUniqueDevicesDownloadingContent(Company company, Date eventDate, Campaign campaign, Device device);

    public Long getTotalContentDownloads(Company company, Date eventDate, Campaign campaign, Device device);

    public void createBluetoothDaySummary(BluetoothDaySummary entry);

    public void updateBluetoothDaySummary(BluetoothDaySummary entry);

    public void createBluetoothFileSendSummary(BluetoothFileSendSummary entry);

    public void updateBluetoothFileSendSummary(BluetoothFileSendSummary entry);

    public BluetoothDaySummary fetchBluetoothDaySummary(Date eventDate, Company company, Campaign campaign, Device device);

    public List<BluetoothDaySummary> fetchBluetoothDaySummaries(Date start, Date end, Company company, Campaign campaign, Device device);

    public List<BluetoothFileSendSummary> fetchBluetoothFileSendSummaries(Date eventDate, Company company, Campaign campaign, Device device);

    public List<BluetoothFileSendSummary> fetchBluetoothSendSummaries(Date start, Date end, Company company, Campaign campaign, Device device);

    public void createOrUpdateBluetoothDaySummary(BluetoothDaySummary entry);

    public void createOrUpdateBluetoothFileSendSummary(BluetoothFileSendSummary entry);

    public List<BluetoothSend> getBluetoothSendInRange(Date start, Date end, Company c);

    public List<BluetoothSend> getBluetoothSendInRangeByCampaignAndDevice(Date start, Date end, Company c, Campaign campaign, Device device);

    public List<BluetoothDwell> getBluetoothDwellInRange(Date start, Date end, Company c);

    public List<BluetoothDwell> getBluetoothDwellInRangeByCampaign(Date start, Date end, Company c, Campaign camp);

    public List<BluetoothFileSendSummary> getBluetoothFileSendSummaryFromBluetoothSend(Date start, Date end, Company c, Campaign camp, Device device);

}
