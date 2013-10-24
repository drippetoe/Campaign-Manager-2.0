/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.*;
import javax.ejb.Local;

/**
 *
 * @author dshaw
 */
@Local
public interface GatewayManagerLocal extends AbstractManagerInterface<GatewayUser> {
    
    public GatewayUser getGatewayUserByMacAddress(Company company, String macAddress);
    
    public GatewayPartnerFields getGatewayPartnerFieldsByCampaign(Campaign campaign);
    public GatewayPartnerFields createGatewayPartnerFields(GatewayPartnerFields entity);
    public GatewayPartnerFields updateGatewayPartnerFields(GatewayPartnerFields entity);
    public void deleteGatewayPartnerFields(GatewayPartnerFields entity);
}
