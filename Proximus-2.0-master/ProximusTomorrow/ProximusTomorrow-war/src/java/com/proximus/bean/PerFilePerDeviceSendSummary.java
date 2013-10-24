/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.bean;

import com.proximus.data.report.BluetoothFileSendSummary;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author dshaw
 */
public class PerFilePerDeviceSendSummary {
    
    String file;
    Long detections;
    Long sendCount;
    Long pageViews;
    TreeMap<Long, BluetoothFileSendSummary> perDeviceSummaries;

    public TreeMap<Long, BluetoothFileSendSummary> getPerDeviceSummaries() {
        return perDeviceSummaries;
    }
    
    public List<BluetoothFileSendSummary> getPerDeviceSummariesAsList()
    {
        return new ArrayList<BluetoothFileSendSummary>(perDeviceSummaries.values());
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getDetections() {
        return detections;
    }

    public void setDetections(Long detections) {
        this.detections = detections;
    }

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }

    public Long getSendCount() {
        return sendCount;
    }

    public void setSendCount(Long sendCount) {
        this.sendCount = sendCount;
    }
    
    
    
    public PerFilePerDeviceSendSummary()
    {
        perDeviceSummaries = new TreeMap<Long, BluetoothFileSendSummary>();
    }
    
    public Boolean containsKey(String key)
    {
        return perDeviceSummaries.containsKey(key); 
    }
    
    public void putEntry(Long deviceId, BluetoothFileSendSummary value)
    {
        BluetoothFileSendSummary entry;
        if ( perDeviceSummaries.containsKey(deviceId)){
            entry = perDeviceSummaries.get(deviceId);
            entry.setSendCount(entry.getSendCount() + value.getSendCount());
        }
        else
        {
            entry = value;
        }
        perDeviceSummaries.put(deviceId, value);
    }
    
    public Long getTotalSendCount()
    {
        long totalCount = 0;
        
        for (Map.Entry<Long, BluetoothFileSendSummary> entry : perDeviceSummaries.entrySet()) {
            Long key = entry.getKey();
            BluetoothFileSendSummary bluetoothFileSendSummary = entry.getValue();
            totalCount += bluetoothFileSendSummary.getSendCount();
        }
        
        return totalCount;
    }
    
    public Long getTotalDetections()
    {
        long totalDetections = 0;
        for (Map.Entry<Long, BluetoothFileSendSummary> entry : perDeviceSummaries.entrySet()) {
            Long long1 = entry.getKey();
            BluetoothFileSendSummary bluetoothFileSendSummary = entry.getValue();
            totalDetections += bluetoothFileSendSummary.getBluetoothDetections();
        }
        
        return totalDetections;
    }
    
    public Long getTotalPageViews()
    {
        long totalPageViews = 0;
        for (Map.Entry<Long, BluetoothFileSendSummary> entry : perDeviceSummaries.entrySet()) {
            Long long1 = entry.getKey();
            BluetoothFileSendSummary bluetoothFileSendSummary = entry.getValue();
            totalPageViews += bluetoothFileSendSummary.getWifiSuccessfulPageViews();
        }
        
        return totalPageViews;
    }
}
