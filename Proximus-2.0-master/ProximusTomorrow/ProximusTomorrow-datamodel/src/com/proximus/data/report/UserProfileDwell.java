/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Gilberto Gaxiola
 */
public class UserProfileDwell {

    private String macAddress;
    private String friendlyName;
    private long totalSessions;
    private long totalDwellTime;
    private double averageDwellTime;
    private Date firstSeen;
    private Date lastSeen;
    
    public static String formatTimestampForWeb(Date date) {
        final String TIMESTAMP_FMT = "dd-MMMM-yyyy HH:mm:ss";
        return new SimpleDateFormat(TIMESTAMP_FMT).format(date);
    }

    public UserProfileDwell(String macAddress, String friendlyName, long totalSessions, long totalDwellTime, double averageDwellTime, Date firstSeen, Date lastSeen) {
        this.macAddress = macAddress;
        this.friendlyName = friendlyName;
        this.totalSessions = totalSessions;
        this.totalDwellTime = totalDwellTime;
        this.averageDwellTime = averageDwellTime;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
    }

    public double getAverageDwellTime() {
        return averageDwellTime;
    }

    public void setAverageDwellTime(double averageDwellTime) {
        this.averageDwellTime = averageDwellTime;
    }

    public Date getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen) {
        this.firstSeen = firstSeen;
    }
    
    public String getFirstSeenFmt() {
        if (this.firstSeen == null) {
            return "Never";
        } else {
            return UserProfileDwell.formatTimestampForWeb(firstSeen);
        }

    }
    
    public String getLastSeenFmt() {
        if (this.lastSeen == null) {
            return "Never";
        } else {
            return UserProfileDwell.formatTimestampForWeb(lastSeen);
        }
    }
   

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public long getTotalDwellTime() {
        return totalDwellTime;
    }

    public void setTotalDwellTime(long totalDwellTime) {
        this.totalDwellTime = totalDwellTime;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    @Override
    public String toString() {
        return "UserProfileDwell{" + "macAddress=" + macAddress + ", friendlyName=" + friendlyName + ", totalSessions=" + totalSessions + ", totalDwellTime=" + totalDwellTime + ", averageDwellTime=" + averageDwellTime + ", firstSeen=" + firstSeen + ", lastSeen=" + lastSeen + '}';
    }
    
    
    
    
}
