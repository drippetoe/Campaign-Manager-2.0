/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gilberto Gaxiola Util Data structure to put list of data that will be
 * useful to choose from on the View
 */
public class ListChoosers {

    private static ArrayList<String> states = new ArrayList<String>();
    private static ArrayList<String> timezones = new ArrayList<String>();
    private static ArrayList<LanguageSelector> languages = new ArrayList<LanguageSelector>();

    public static List<String> getStates() {
        if (states.isEmpty()) {
            states.add("AL");
            states.add("AK");
            states.add("AZ");
            states.add("AR");
            states.add("CA");
            states.add("CO");
            states.add("CT");
            states.add("DE");
            states.add("DC");
            states.add("FL");
            states.add("GA");
            states.add("HI");
            states.add("ID");
            states.add("IL");
            states.add("IN");
            states.add("IA");
            states.add("KS");
            states.add("KY");
            states.add("LA");
            states.add("ME");
            states.add("MD");
            states.add("MA");
            states.add("MI");
            states.add("MN");
            states.add("MS");
            states.add("MO");
            states.add("MT");
            states.add("NE");
            states.add("NV");
            states.add("NH");
            states.add("NJ");
            states.add("NM");
            states.add("NY");
            states.add("NC");
            states.add("ND");
            states.add("OH");
            states.add("OK");
            states.add("OR");
            states.add("PA");
            states.add("RI");
            states.add("SC");
            states.add("SD");
            states.add("TN");
            states.add("TX");
            states.add("UT");
            states.add("VT");
            states.add("VA");
            states.add("WA");
            states.add("WV");
            states.add("WI");
            states.add("WY");
        }
        return states;
    }

   

    public static List<String> getTimezones() {
        if (timezones.isEmpty()) {
            timezones.add("Hawaiian");
            timezones.add("Alaskan");
            timezones.add("Pacific");
            timezones.add("Mountain");
            timezones.add("Central");
            timezones.add("Eastern");
        }
        return timezones;
    }

    public static List<LanguageSelector> getLanguages() throws UnsupportedEncodingException {
        if (languages.isEmpty()) {
            languages.add(new LanguageSelector("Deutsch", "de", "de"));
            languages.add(new LanguageSelector("English", "en", "us"));
            languages.add(new LanguageSelector("Español", "es", "es"));
            languages.add(new LanguageSelector("Français", "fr", "fr"));
            languages.add(new LanguageSelector("Svenska", "sv", "se"));
        }
        return languages;
    }
}
