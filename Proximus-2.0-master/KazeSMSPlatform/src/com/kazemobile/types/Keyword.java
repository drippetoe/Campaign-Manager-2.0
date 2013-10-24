/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.types;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rwalker
 */
@XmlRootElement
public class Keyword implements Serializable {
    private int keywordId;
    private int shortCodeId;
    private String keyword;
    
    public Keyword() {
        keywordId = 0;
        shortCodeId = 1;
        keyword = "";
    }

    public int getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    public int getShortCodeId() {
        return shortCodeId;
    }

    public void setShortCodeId(int shortCodeId) {
        this.shortCodeId = shortCodeId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.keywordId;
        return hash;
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
        if (this.keywordId != other.keywordId) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Keyword{" + "keywordId=" + keywordId + ", shortCodeId=" + shortCodeId + ", keyword=" + keyword + '}';
    }
}
