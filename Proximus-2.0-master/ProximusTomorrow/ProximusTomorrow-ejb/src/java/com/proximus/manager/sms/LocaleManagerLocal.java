/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager.sms;

import com.proximus.data.sms.Locale;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface LocaleManagerLocal extends AbstractManagerInterface<Locale> {

    public Locale getLocaleByName(String name);

    public Locale getLocaleByLanguageCode(String languageCode);

    public Locale getDefaultLocale();

    public List<Locale> getAllSortedByKeyword();

    public List<Locale> getAllSorted();
}
