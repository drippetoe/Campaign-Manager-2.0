/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.Date;
import net.aixum.webservice.Form;
import net.aixum.webservice.Hologram;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class MembershipCardDetailsResponseModel extends BaseResponseModel {

    private int Id;
    private String Name;
    private String Subline;
    private String RegistrationUrl;
    private String RegistrationTitle;
    private String RegistrationText;
    private Date ValidFrom;
    private Date ValidTo;
    private int Revision;
    private String CardHtml;
    private Hologram Hologram;
    private Form Form;

    public MembershipCardDetailsResponseModel(int Id, String Name, String Subline, String RegistrationUrl, String RegistrationTitle, String RegistrationText, Date ValidFrom, Date ValidTo, int Revision, String CardHtml, Hologram Hologram, Form Form) {
        this.Id = Id;
        this.Name = Name;
        this.Subline = Subline;
        this.RegistrationUrl = RegistrationUrl;
        this.RegistrationTitle = RegistrationTitle;
        this.RegistrationText = RegistrationText;
        this.ValidFrom = ValidFrom;
        this.ValidTo = ValidTo;
        this.Revision = Revision;
        this.CardHtml = CardHtml;
        this.Hologram = Hologram;
        this.Form = Form;
    }

    public MembershipCardDetailsResponseModel(int Id, String Name, String Subline, String RegistrationUrl, String RegistrationTitle, String RegistrationText, Date ValidFrom, Date ValidTo, int Revision, String CardHtml, Hologram Hologram, Form Form, boolean success, String message) {
        super(success, message);
        this.Id = Id;
        this.Name = Name;
        this.Subline = Subline;
        this.RegistrationUrl = RegistrationUrl;
        this.RegistrationTitle = RegistrationTitle;
        this.RegistrationText = RegistrationText;
        this.ValidFrom = ValidFrom;
        this.ValidTo = ValidTo;
        this.Revision = Revision;
        this.CardHtml = CardHtml;
        this.Hologram = Hologram;
        this.Form = Form;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSubline() {
        return Subline;
    }

    public void setSubline(String Subline) {
        this.Subline = Subline;
    }

    public String getRegistrationUrl() {
        return RegistrationUrl;
    }

    public void setRegistrationUrl(String RegistrationUrl) {
        this.RegistrationUrl = RegistrationUrl;
    }

    public String getRegistrationTitle() {
        return RegistrationTitle;
    }

    public void setRegistrationTitle(String RegistrationTitle) {
        this.RegistrationTitle = RegistrationTitle;
    }

    public String getRegistrationText() {
        return RegistrationText;
    }

    public void setRegistrationText(String RegistrationText) {
        this.RegistrationText = RegistrationText;
    }

    public Date getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(Date ValidFrom) {
        this.ValidFrom = ValidFrom;
    }

    public Date getValidTo() {
        return ValidTo;
    }

    public void setValidTo(Date ValidTo) {
        this.ValidTo = ValidTo;
    }

    public int getRevision() {
        return Revision;
    }

    public void setRevision(int Revision) {
        this.Revision = Revision;
    }

    public String getCardHtml() {
        return CardHtml;
    }

    public void setCardHtml(String CardHtml) {
        this.CardHtml = CardHtml;
    }

    public Hologram getHologram() {
        return Hologram;
    }

    public void setHologram(Hologram Hologram) {
        this.Hologram = Hologram;
    }

    public Form getForm() {
        return Form;
    }

    public void setForm(Form Form) {
        this.Form = Form;
    }
}
