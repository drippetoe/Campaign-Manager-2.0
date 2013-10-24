package com.proximus.data.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author ejohansson
 */
public final class Logging
{
    private final long FORTYEIGHT_HOURS = 1000 * 60 * 60 * 48;
    @XmlAttribute(name = "rotation")
    public long rotation = 120000;

    /**
     * This method will try to create a cron job from the rotation value
     * Will only allow minimum 30 seconds and maximum 1 hour
     * If there is a mixture of minutes and seconds it will round up to the nearest minute
     * 
     * Thus you can specify to run at 30-60 seconds and then from 1-60 minutes
     * 
     * @return 
     */
    public String getCronExpression()
    {
        //If rotation is less than 1 minute 
        if (rotation < 60000) {
            return "0 0/1 * * * ?";
        }
        //If rotation is greater than 48 hour
        //Just rotate every 2 days
        if (rotation > FORTYEIGHT_HOURS) {
            return "* * * 1-31/2 * ?";
        }

        long ms = rotation;
        long sec = ms / 1000;
        long min = sec / 60;
        long hour = min / 60;
        min = min % 60;
        sec = sec % 60;


        String cron;
        if (hour > 0) {

            if (min > 30) {
                hour++;
                min = 0;
            }
            //Returning an hourly cron
            cron = "0 0 */" + hour + " * * ?";
        } else {
            if (sec > 30) {
                min++;
                sec = 0;
            }
            //Returning a minutely cron
            cron = "0 0/" + min + " * * * ?";
        }
//        System.out.println("Giving Cron: " + cron);
        return cron;

    }

}
