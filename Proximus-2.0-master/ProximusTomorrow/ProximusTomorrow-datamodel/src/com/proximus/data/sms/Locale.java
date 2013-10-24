/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.FacebookUserLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ronald
 *
 */
@XmlRootElement(name = "locale")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "locale", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name"
    })
})
public class Locale implements Serializable {

    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "language_code")
    private String languageCode;
    @XmlTransient
    @ManyToMany(mappedBy = "locales")
    private List<Subscriber> subscribers;

    public Locale() {
        this.id = 0L;
        this.name = java.util.Locale.ENGLISH.getDisplayName();
        this.languageCode = java.util.Locale.ENGLISH.getLanguage();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    public void addSubscriber(Subscriber subscriber) {
        if (this.subscribers == null) {
            this.subscribers = new ArrayList<Subscriber>();
        }
        this.subscribers.add(subscriber);
    }

    public void clearSubscriber() {
        this.subscribers = new ArrayList<Subscriber>();
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Locale other = (Locale) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.languageCode == null) ? (other.languageCode != null) : !this.languageCode.equals(other.languageCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.languageCode != null ? this.languageCode.hashCode() : 0);
        return hash;
    }
}
