/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice;

import java.util.List;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class ContactDetails {

    private String address;
    private String openingHours;
    private Hotline hotline;
    private List<PhoneNumber> phoneNumbers;
    private List<EmailAddress> emailAddresses;
    private List<Website> websites;

    public ContactDetails(String address, String openingHours, Hotline hotline, List<PhoneNumber> phoneNumbers, List<EmailAddress> emailAddresses, List<Website> websites) {
        this.address = address;
        this.openingHours = openingHours;
        this.hotline = hotline;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
        this.websites = websites;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Hotline getHotline() {
        return hotline;
    }

    public void setHotline(Hotline hotline) {
        this.hotline = hotline;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public List<Website> getWebsites() {
        return websites;
    }

    public void setWebsites(List<Website> websites) {
        this.websites = websites;
    }
}
