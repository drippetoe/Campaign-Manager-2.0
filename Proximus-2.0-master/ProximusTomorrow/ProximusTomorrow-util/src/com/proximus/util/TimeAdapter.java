/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ejohansson
 */
public class TimeAdapter extends XmlAdapter<String, Date>
{
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm z");

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }
}
