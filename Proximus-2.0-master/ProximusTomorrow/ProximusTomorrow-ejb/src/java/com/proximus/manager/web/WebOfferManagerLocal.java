/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.manager.web;

import com.proximus.data.web.WebOffer;
import com.proximus.manager.AbstractManagerInterface;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gilberto Gaxiola
 */
@Local
public interface WebOfferManagerLocal extends AbstractManagerInterface<WebOffer> {

    public WebOffer getWebOfferByItsMobileOfferSibling(Long mobileOfferId);

    public List<Long> getWebOffersByProperty(String languageCode, String propertyWebHash);
}
