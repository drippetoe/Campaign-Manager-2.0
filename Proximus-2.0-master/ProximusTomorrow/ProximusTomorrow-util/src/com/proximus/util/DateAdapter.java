/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

/**
 *
 * @author ejohansson
 */
import com.proximus.util.client.ClientURISettings;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date>
{
    private SimpleDateFormat dateFormat = new SimpleDateFormat(ClientURISettings.DATEFORMAT);

    @Override
    public String marshal(Date v) throws Exception
    {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception
    {
        return dateFormat.parse(v);
    }
    
    /**
     * helper method to get a Date object out of a String representation
     * @param stringDate could either be of the format "MM/dd/yyyy" or "MM/dd/yyy HH:mm:ss"
     * @return 
     */
    public static Date getDate(String stringDate)
    {
        DateFormat formatter;
        String[] split = stringDate.split("\\s+");
        if(split.length == 0 ) {
            formatter = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        }
        
        Date date;
        try {
            date = formatter.parse(stringDate);
            return date;
        } catch (ParseException ex) {
            return null;
        }
    }
}
