/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.response;

import java.util.List;
import net.aixum.webservice.Language;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class LanguagesResponseModel extends BaseResponseModel {

    private List<Language> languages;

    public LanguagesResponseModel(List<Language> languages) {
        this.languages = languages;
    }

    public LanguagesResponseModel(List<Language> languages, boolean success, String message) {
        super(success, message);
        this.languages = languages;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }
}
