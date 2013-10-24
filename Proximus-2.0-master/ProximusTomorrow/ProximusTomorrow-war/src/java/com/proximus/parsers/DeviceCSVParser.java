/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.parsers;

import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

/**
 *
 * Parses a CSV file, storing the appropriate data in the DB
 *
 * Filenames are of the form: device_****.csv where **** is anything that makes
 * sense Current Format: serial_number,mac_address,platform,name
 *
 * @author dshaw
 */
public class DeviceCSVParser {

    static final Logger logger = Logger.getLogger(DeviceCSVParser.class.getName());

    public int parse(File f, DeviceManagerLocal deviceMgr, CompanyManagerLocal companyMgr, Company company) {
        BufferedReader csvFile = null;
        int parseCount = 0;
        try {
            csvFile = new BufferedReader(new FileReader(f));
            String csvFileLine = "";


            while ((csvFileLine = csvFile.readLine()) != null) {
                String[] details = csvFileLine.split(",");

                // line is flexible in length, so that new fields can be added later
                // min length is 3, current length is 4 (name is optional)

                if (details.length < 3) {
                    logger.warn("Line was too short: " + csvFileLine);
                    continue;
                }

                String serialNumber = details[0];
                String macAddress = details[1].replace(":", "").toUpperCase();
                String platform = details[2];

                Device device = new Device(serialNumber, macAddress);

                if (details.length > 3) {
                    device.setName(details[3]);
                } else {
                    // give it a non-empty name if one was not provided
                    device.setName(platform + "-" + macAddress);
                    device.setPlatform(platform);
                }


                if (company != null) {
                    device.setCompany(company);
                }

                // set some sensible defaults
                device.setReconnectInterval(300000L); // 5 min reconnect interval
                device.setKeepAlive(20000L); // HTTP keep-alive 20 seconds
                device.setRotation(14400000L); // 4 hour log rotation

                device.setBuild(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("BuildNumber")));
                device.setMajor(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("MajorVersion")));
                device.setMinor(Long.parseLong(ResourceBundle.getBundle("/resources/Bundle").getString("MinorVersion")));


                deviceMgr.create(device);
                parseCount++;
            }
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            try {
                csvFile.close();

                return parseCount;
            } catch (IOException ex) {
                logger.fatal(ex);
                return parseCount;
            }
        }
    }
}
