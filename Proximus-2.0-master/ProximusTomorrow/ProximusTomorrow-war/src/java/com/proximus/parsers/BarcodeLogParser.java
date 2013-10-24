/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.Barcode;
import com.proximus.data.Device;
import com.proximus.manager.BarcodeManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
public class BarcodeLogParser {

    static final Logger logger = Logger.getLogger(Barcode.class.getName());

    public boolean parse(File f, BarcodeManagerLocal barcodeMgr, DeviceManagerLocal deviceMgr) {
        BufferedReader in = null;
        List<Barcode> barcodes = new ArrayList<Barcode>();

        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("barcode")) {
            logger.warn("File " + filename + " is not appropriate for this parser");
            return false;
        }
        String[] split = filename.split("_");
        String macAddr = split[1];
        Device d = deviceMgr.getDeviceByMacAddress(macAddr);
        try {
            in = new BufferedReader(new FileReader(f));

            String line;
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            while ((line = in.readLine()) != null) {
                String[] details = line.split(",");

                if (details.length < 4) {
                    logger.debug("Line was too short: " + line);
                    return false;
                }

                Date eventDate = formatter.parse(details[0]);
                String type = details[1];
                String mac = details[2];
                String barcodeValue = details[3];

                Barcode barcode = new Barcode(eventDate, mac, type, barcodeValue);
                barcode.setCompany(d.getCompany());
                barcodes.add(barcode);
            }
            in.close();
            barcodeMgr.createListBarcodeLogs(barcodes);
            return true;
        } catch (Exception ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex2) {
                }
            }
            logger.fatal("Parser Error", ex);
            return false;
        } finally {
        }
    }
}
