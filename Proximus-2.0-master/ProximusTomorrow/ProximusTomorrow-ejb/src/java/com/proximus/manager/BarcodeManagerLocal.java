/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.Barcode;
import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ronald
 */
@Local
public interface BarcodeManagerLocal extends AbstractManagerInterface<Barcode> {

    public List<Barcode> findAllByCompany(Company company);
    public void createListBarcodeLogs(List<Barcode> logs);
    public List<Barcode> fetchBarcodeOffers(Date start, Date end, Company company, Campaign campaign, Device device);
}
