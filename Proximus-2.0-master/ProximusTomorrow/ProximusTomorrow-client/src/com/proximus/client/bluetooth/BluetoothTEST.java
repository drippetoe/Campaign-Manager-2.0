package com.proximus.client.bluetooth;


import com.proximus.client.Main;
import com.proximus.client.bluetooth.dreamplug.BluetoothDeviceDiscovery;
import com.proximus.client.config.SystemWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author eric
 */
public class BluetoothTEST {

    private static String btFolder = "/home/proximus/bt";
    private static final Logger logger = Logger.getLogger(BluetoothTEST.class.getName());

    /**
     * Init log4J configuration
     */
    private void initLog4J() {
        PropertyConfigurator.configure(BluetoothTEST.class.getResource("/log4j.properties"));
    }

   

    public static void start() throws DBusException {
        SystemWriter.touchBluetoothLogProperties();                
        PropertyConfigurator.configure(Main.class.getResource("log4j.properties"));

        BluetoothDeviceDiscovery bdd = new BluetoothDeviceDiscovery();

        String[] fileTypes = new String[]{"jpg", "png","txt"};
        ArrayList<BluetoothFileData> files = new ArrayList<BluetoothFileData>();
        File folder = new File(btFolder);

        for (Object obj : FileUtils.listFiles(folder, fileTypes, true)) {
            File file = (File) obj;
            try {
                files.add(BluetoothFileData.fileToBluetoothFileData(1L,file, FilenameUtils.getBaseName(file.getName())));
            } catch (IOException ex) {
                Logger.getLogger(BluetoothTEST.class.getName()).log(Priority.ERROR, ex);
            }
        }
        bdd.start();
        //bdd.update(files);
        try {
            bdd.join();
        } catch (InterruptedException ex) {
        }
        logger.debug("Done!");

    }
}
