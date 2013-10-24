/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.bluetooth;

import com.proximus.client.config.SystemWriter;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Eric Johansson
 */
public class BluetoothFileData {

    private static final Logger logger = Logger.getLogger(BluetoothFileData.class.getName());
    private Long campaignId;
    private byte[] data;
    private String title;
    private String mime;

    public BluetoothFileData(Long campaignId, byte[] data, String title, String mime) {
        this.campaignId = campaignId;
        this.data = data;
        this.title = title;
        this.mime = mime;
    }

    public static List<BluetoothFileData> loadFromProperties() {
        String campaign = SystemWriter.loadBluetoothCampaignId();
        ArrayList<BluetoothFileData> files = new ArrayList<BluetoothFileData>();
        if (campaign!=null || campaign.equalsIgnoreCase("-1")) {
            files = (ArrayList<BluetoothFileData>) folderToBluetoothFileData(Long.getLong(campaign), ClientURISettings.CAMPAIGNS_ROOT_DIR+"/"+campaign+"/bluetooth");
        }
        return files;
    }

    public static List<BluetoothFileData> folderToBluetoothFileData(Long campaignId, String folderPath) {
        ArrayList<BluetoothFileData> files = new ArrayList<BluetoothFileData>();
        File folder = new File(folderPath);
        for (File file : FileUtils.listFiles(folder, ClientURISettings.SUPPORTED_FILETYPES, false)) {
            try {
                logger.fatal("depoying file: "+file.getName());
                files.add(BluetoothFileData.fileToBluetoothFileData(campaignId, file, FilenameUtils.getBaseName(file.getName())));
            } catch (IOException ex) {
                Logger.getLogger(BluetoothFileData.class.getName()).log(Priority.ERROR, ex);
            }
        }
        return files;
    }

    public static BluetoothFileData fileToBluetoothFileData(Long campaignId, File file, String title) throws IOException {
        return new BluetoothFileData(campaignId, FileUtils.readFileToByteArray(file), title + "." + FilenameUtils.getExtension(file.getName()), new MimetypesFileTypeMap().getContentType(file));
    }

    public byte[] getData() {
        return data;
    }

    public String getMime() {
        return mime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return this.data.length;
    }
}
