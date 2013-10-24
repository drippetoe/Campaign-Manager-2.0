/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Company;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "keyword")
public class Keyword implements Serializable {

    //current sources for keywords
    public static final String SOURCE_WEBSITE = "WEBSITE";
    public static final String SOURCE_KEYWORD = "KEYWORD";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "keyword")
    private String keyword;
    @ManyToOne
    private ShortCode shortCode;
    @Column(name = "kaze_keyword_id")
    private String kazeKeywordId;
    @Column(name = "source")
    private String source;
    @Column(name = "deleted")
    private boolean deleted;
    @ManyToOne
    private Company company;
    @OneToOne
    private Property property;
    @ManyToOne
    SourceType sourceType;
    @OneToOne
    Locale locale;

    public Keyword() {
        this.id = 0L; 
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        if (keyword != null) {
            this.keyword = keyword.toUpperCase();
        }
    }

    public ShortCode getShortCode() {
        return shortCode;
    }

    public void setShortCode(ShortCode shortCode) {
        this.shortCode = shortCode;
    }

    public String getKazeKeywordId() {
        return kazeKeywordId;
    }

    public void setKazeKeywordId(String kazeKeywordId) {
        this.kazeKeywordId = kazeKeywordId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }

    
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Keyword other = (Keyword) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.keyword == null) ? (other.keyword != null) : !this.keyword.equals(other.keyword)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    @Override
    public String toString() {
        return this.keyword;
    }
}
