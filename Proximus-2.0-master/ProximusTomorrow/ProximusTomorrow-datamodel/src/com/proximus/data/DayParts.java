/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import com.proximus.data.sms.GeoLocationSettings;
import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferSettings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "day_parts")
@XmlRootElement(name = "day_parts")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayParts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    protected Long id = 0L;
    @Size(max = 10)
    @Column(name = "start_time")
    @XmlAttribute(name = "start_time")
    protected String startTime; //HH:mm
    @Size(max = 10)
    @Column(name = "end_time")
    @XmlAttribute(name = "end_time")
    protected String endTime; //HH:mm
    @Size(max = 15)
    @Column(name = "days_of_week")
    @XmlAttribute(name = "days_of_week")
    protected String daysOfWeek;  //Comma separated values using one-letter acronym U,M,T,W,R,F,S (Sunday,Monday,Tuesday...etc)
    @ManyToOne
    @XmlTransient
    private MobileOfferSettings mobile_offer_settings;
    @ManyToOne
    @XmlTransient
    private MobileOffer mobileOffer;
    @ManyToOne
    @XmlTransient
    private GeoLocationSettings geo_location_settings;
    @Transient
    @XmlTransient
    private Map<String, String> daysMap;
    @Transient
    @XmlTransient
    private List<String> selectedDaysOfWeek;
    @Transient
    @XmlTransient
    private Integer hourStart;
    @Transient
    @XmlTransient
    private Integer minStart;
    @Transient
    @XmlTransient
    private Integer hourEnd;
    @Transient
    @XmlTransient
    private Integer minEnd;
    @Transient
    @XmlTransient
    private boolean amStart;
    @Transient
    @XmlTransient
    private boolean amEnd;

    public DayParts() {
        id = 0L;
        startTime = "00:00";
        endTime = "23:59";
        daysOfWeek = "M,T,W,R,F,S,U";
        
        initializeDaysMap();
        
        
        selectedDaysOfWeek = new ArrayList<String>();
        hourStart = 12;
        hourEnd = 12;
        minStart = 0;
        minEnd = 59;
        amStart = true;
        amEnd = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndTime() {
        computeEndTime();
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        computeStartTime();
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String dayOfWeek) {
        this.daysOfWeek = dayOfWeek;
    }

    public MobileOfferSettings getMobile_offer_settings() {
        return mobile_offer_settings;
    }

    public void setMobile_offer_settings(MobileOfferSettings mobile_offer_settings) {
        this.mobile_offer_settings = mobile_offer_settings;
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    public List<String> getSelectedDaysOfWeek() {
        return selectedDaysOfWeek;
    }

    public void setSelectedDaysOfWeek(List<String> selectedDaysOfWeek) {
        this.selectedDaysOfWeek = selectedDaysOfWeek;
        daysOfWeek = "";
        if (selectedDaysOfWeek != null && !selectedDaysOfWeek.isEmpty()) {
            for (int i = 0; i < this.selectedDaysOfWeek.size() - 1; i++) {
                daysOfWeek += this.selectedDaysOfWeek.get(i) + ",";
            }
            daysOfWeek += (this.selectedDaysOfWeek.get(this.selectedDaysOfWeek.size() - 1));
        }
    }

    public Map<String, String> getDaysMap() {
        return daysMap;
    }

    public void setDaysMap(Map<String, String> daysMap) {
        this.daysMap = daysMap;
    }

    public String getSelectedDays() {
        String result = "";
        for (String s : selectedDaysOfWeek) {
            String day = s.equals("M") ? "Mon" : s.equals("T") ? "Tue" : s.equals("W") ? "Wed" : s.equals("R") ? "Thu" : s.equals("F") ? "Fri" : s.equals("S") ? "Sat" : s.equals("U") ? "Sun" : "";
            result += day + ", ";
        }
        if (!result.isEmpty()) {
            return result.substring(0, result.lastIndexOf(",")).trim();
        }
        return "";
    }

    public Integer getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(Integer hourEnd) {
        this.hourEnd = hourEnd;
    }

    public Integer getHourStart() {
        return hourStart;
    }

    public void setHourStart(Integer hourStart) {
        this.hourStart = hourStart;
    }

    public Integer getMinEnd() {
        return minEnd;
    }

    public void setMinEnd(Integer minEnd) {
        this.minEnd = minEnd;
    }

    public Integer getMinStart() {
        return minStart;
    }

    public void setMinStart(Integer minStart) {
        this.minStart = minStart;
    }

    public boolean isAmEnd() {
        return amEnd;
    }

    public void setAmEnd(boolean amEnd) {
        this.amEnd = amEnd;
    }

    public boolean isAmStart() {
        return amStart;
    }

    public void setAmStart(boolean amStart) {
        this.amStart = amStart;
    }

    @Override
    public String toString() {
        String result = "";
        result += "Active Days: " + this.getSelectedDays();
        result += "\nTime Range: " + getStartTime() + " to " + getEndTime();
        return result;
    }
    
    public String getLogString() {
        StringBuilder value = new StringBuilder();
        value.append("[(DayParts) ");
        value.append(this.getSelectedDaysOfWeek());
        value.append(" (");
        value.append(this.getStartTime());
        value.append("-");
        value.append(this.getEndTime());
        value.append(")]");
        
        return value.toString();
    }

    /*
     * Converting from 12-hour Format to 24 hour Format How: For Hour If it is
     * in PM add 12 (except when is already 12 PM) If it is in AM then add the
     * leading zero on single digits and replace 12 AM with 00 For Minute Add
     * the leading zero on single digits
     */
    private void computeStartTime() {
        String hour;
        String leadingZero = (this.minStart < 10) ? "0" : "";
        String min = leadingZero + this.minStart;

        if (this.amStart) {
            if (hourStart == 12) {
                hour = "00";
            } else if (hourStart < 10) {
                hour = "0" + hourStart;
            } else {
                hour = hourStart + "";
            }
        } else if (hourStart == 12) {
            hour = hourStart + "";
        } else {
            hour = ((hourStart + 12) % 24) + "";
        }
        this.startTime = hour + ":" + min;
    }

    private void computeEndTime() {
        int adjustedHour;
        if (hourEnd == 1) {
            adjustedHour = 12;
        } else {
            adjustedHour = hourEnd - 1;
        }


        String hour;
        String leadingZero = (this.minEnd < 10) ? "0" : "";
        String min = leadingZero + this.minEnd;

        if (this.amEnd) {
            if (adjustedHour == 12) {
                hour = "00";
            } else if (adjustedHour < 10) {
                hour = "0" + adjustedHour;
            } else {
                hour = adjustedHour + "";
            }
        } else if (adjustedHour == 12) {
            hour = adjustedHour + "";
        } else {
            hour = ((adjustedHour + 12) % 24) + "";
        }
        //Hack to adjust 12 AM to 23:59  and 12 PM to 11:59 (as Ending Times)

        if (hour.equals("11")) {
            hour = "23";
        } else if (hour.equals("23")) {
            hour = "11";
        }
        this.endTime = hour + ":" + min;
    }

    public void update() {
        computeStartTime();
        computeEndTime();
    }
    
    private void initializeDaysMap()
    {
        daysMap = new LinkedHashMap<String, String>();
        daysMap.put("Monday", "M");
        daysMap.put("Tuesday", "T");
        daysMap.put("Wednesday", "W");
        daysMap.put("Thursday", "R");
        daysMap.put("Friday", "F");
        daysMap.put("Saturday", "S");
        daysMap.put("Sunday", "U");   
    }
    

    public void initialize() {
        initializeDaysMap();

        selectedDaysOfWeek = new ArrayList<String>();
        String[] days = this.daysOfWeek.split(",");
        selectedDaysOfWeek = new ArrayList<String>();
        for (String s : days) {
            selectedDaysOfWeek.add(s);
        }
        String[] start = this.startTime.split(":");
        String[] end = this.endTime.split(":");
        hourStart = Integer.parseInt(start[0]);
        minStart = Integer.parseInt(start[1]);
        hourEnd = (Integer.parseInt(end[0]) + 1) % 24;
        minEnd = Integer.parseInt(end[1]);
        if (hourStart < 12) {
            amStart = true;
        } else {
            amStart = false;
        }

        if (hourEnd < 12) {
            amEnd = true;
        } else {
            amEnd = false;
        }
        if (hourStart == 0) {
            hourStart = 12;
        } else {
            if (hourStart > 12) {
                hourStart = hourStart - 12;
            }
        }
        if (hourEnd == 0) {
            hourEnd = 12;
        } else {
            if (hourEnd > 12) {
                hourEnd = hourEnd - 12;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DayParts) {
            DayParts d = (DayParts) o;
            if (d.getId() != this.getId()) {
                return false;
            } else if (d.getId() == 0) {
                if (d.toString().equals(this.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;

        } else {
            return false;
        }
    }

    public GeoLocationSettings getGeo_location_settings() {
        return geo_location_settings;
    }

    public void setGeo_location_settings(GeoLocationSettings geo_location_settings) {
        this.geo_location_settings = geo_location_settings;
    }
    
    
}