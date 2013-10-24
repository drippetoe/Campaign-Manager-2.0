package com.proximus.jsf.datamodel;

import com.proximus.bean.ImageFile;
import com.proximus.util.ServerURISettings;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.SelectableDataModel;

/**
 * DataModel to display java.io.File in a datatable
 *
 * @author eric
 */
public class FileDataModel extends ListDataModel<ImageFile> implements SelectableDataModel<ImageFile> {

    public final String BT_RPOPERTIES_FILE = "bt.conf";
    public String btPath;

    public FileDataModel() {
    }

    public FileDataModel(String btPath) {
        this.btPath = btPath;
    }

    public FileDataModel(File[] files) {
        List<ImageFile> list = new ArrayList<ImageFile>();
        for (File file : files) {
            if (!file.isDirectory()) {
                list.add(new ImageFile(file, null));
            }
        }
        if (list.size() > 0) {
            setWrappedData(list);
        }
    }

    public FileDataModel(List<ImageFile> list) {
        super(list);
    }



    public void rewriteBtConf() {
        //Need to rearrange the bt.conf file
        File metaData = new File(btPath + ServerURISettings.OS_SEP + BT_RPOPERTIES_FILE);
        if (metaData.exists()) {
            FileUtils.deleteQuietly(metaData);
        }
        try {
            FileWriter fw = new FileWriter(metaData, true);
            List<ImageFile> listFiles = (ArrayList<ImageFile>)this.getWrappedData();
            for (ImageFile file : listFiles) {
                fw.append(file.getName() + System.getProperty("line.separator"));
                fw.flush();
            }
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }

    public void setFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
        }
        List<ImageFile> list = new ArrayList<ImageFile>();
        for (File file : files) {
            if (!file.isDirectory()) {
                list.add(new ImageFile(file, null));
            }
        }
        if (list.size() > 0) {
            setWrappedData(list);
        }
    }

    /**
     * remove the ImageFile from the DataModel If the file is part of bluetooth
     * also take the file from the ordering configuration bt.conf
     *
     * @param i
     */
    public void removeFile(ImageFile i, String serverPath) throws FileNotFoundException, IOException {
        List<ImageFile> files = (List<ImageFile>) getWrappedData();
        if (files == null) {
            return;
        }
        if (files.contains(i)) {
            files.remove(i);
            setWrappedData(files);

            //If file to remove is a bluetooth file then take it from the metadata
            String ext = FilenameUtils.getExtension(i.getName());
            if (!ext.equalsIgnoreCase("zip")) {
                File metaData = new File(serverPath + BT_RPOPERTIES_FILE);
                File tempFile = new File(serverPath + "_temp.temp");
                BufferedReader reader = new BufferedReader(new FileReader(metaData));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String lineToRemove = i.getName();
                String curr;

                while ((curr = reader.readLine()) != null) {
                    curr = curr.trim();
                    if (curr.equals(lineToRemove)) {
                        continue;
                    } else {
                        writer.write(curr);
                        writer.newLine();
                    }
                }
                reader.close();
                writer.close();
                FileUtils.deleteQuietly(metaData);
                FileUtils.moveFile(tempFile, metaData);
            }
        }
    }

    @Override
    public Object getRowKey(ImageFile t) {
        return t.getName();
    }

    @Override
    public ImageFile getRowData(String rowKey) {
        List<ImageFile> files = (List<ImageFile>) getWrappedData();
        for (ImageFile file : files) {
            if (file.getName().equals(rowKey)) {
                return file;
            }
        }
        return null;
    }

    /**
     * helper function to adding bluetooth files into the bt.conf file
     *
     * @param serverPath
     * @param filename
     * @throws IOException
     */
    public void writeToBtConf(String filename) throws IOException {
        if (btPath != null && !btPath.isEmpty()) {
            File metaData = new File(btPath + ServerURISettings.OS_SEP + BT_RPOPERTIES_FILE);
            FileWriter fw = new FileWriter(metaData, true);
            fw.append(filename + System.getProperty("line.separator"));
            fw.flush();
            fw.close();
        } else {
            System.err.println("Bluetooth Path is not set and we can't write the bt.conf properties file");
        }
    }
}
