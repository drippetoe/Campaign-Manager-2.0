/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Gaxiola
 */
public class ZipUnzip {
    
    static final Logger logger = Logger.getLogger(ZipUnzip.class.getName());
    
    public static void CopyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        
        in.close();
        out.close();
    }
    
    private static void CopyInputStreamWithoutClose(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }
    private static final IOFileFilter dirs = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
        
        @Override
        public boolean accept(File file, String string) {
            return new File(file, string).isDirectory();
        }
    };
    private static final IOFileFilter allButZip = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            return !FilenameUtils.isExtension(file.getName(), "zip");
        }
        
        @Override
        public boolean accept(File file, String string) {
            return !FilenameUtils.isExtension(new File(file, string).getName(), "zip");
        }
    };
    private static final IOFileFilter allButIgnoreDir = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            try {
                if (file.getCanonicalPath().contains("_ignore") || file.getCanonicalPath().contains("__MACOSX") || file.getCanonicalPath().contains(".DS_Store")) {
                    return false;
                }
                return !FilenameUtils.isExtension(file.getName(), "zip");
                
            } catch (IOException ex) {
                return false;
            }
            
        }
        
        @Override
        public boolean accept(File file, String string) {
            try {
                if (file.getCanonicalPath().contains("_ignore") || string.contains("_ignore") || file.getCanonicalPath().contains("__MACOSX") || string.contains("__MACOSX") || string.contains(".DS_Store") || file.getCanonicalPath().contains(".DS_Store")) {
                    return false;
                }
                return !FilenameUtils.isExtension(new File(file, string).getName(), "zip");
            } catch (IOException ex) {
                return false;
            }
        }
    };

    /**
     *
     * @param basePath
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void Zip(String basePath, String filename) throws FileNotFoundException, IOException {
        File baseFolder = new File(basePath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(baseFolder, filename)));
        out.setLevel(Deflater.BEST_COMPRESSION);
        out.setComment("Zipped by ZipUnzip.java");
        List<File> files = new ArrayList<File>(FileUtils.listFiles(baseFolder, allButZip, dirs));
        for (File file : files) {
            String zippedName = file.getAbsolutePath().substring(new File(basePath).getAbsolutePath().length());
            FileInputStream fis = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(zippedName));
            CopyInputStreamWithoutClose(fis, out);
            out.closeEntry();
            fis.close();
        }
        out.close();
    }

    /**
     *
     * @param fileToZip
     * @param fileName The name of the real file inside of the zipped file
     * @return The zipped version of the fileToZip
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File GetZippedFile(File fileToZip, String fileName) throws FileNotFoundException, IOException {
        String ext = FilenameUtils.getExtension(fileToZip.getName());
        File zippedResult = new File(fileToZip.getAbsolutePath().replace(ext, "zip"));
        InputStream stream = new FileInputStream(fileToZip);
        ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(zippedResult));
        outStream.setLevel(Deflater.BEST_COMPRESSION);
        outStream.putNextEntry(new ZipEntry(fileName));
        CopyInputStream(stream, outStream);
        return zippedResult;
    }
    
    public static void ZipAllButIgnore(String basePath, String filename) throws FileNotFoundException, IOException {
        File baseFolder = new File(basePath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(baseFolder, filename)));
        out.setLevel(Deflater.BEST_COMPRESSION);
        out.setComment("Zipped by ZipUnzip.java");
        List<File> files = new ArrayList<File>(FileUtils.listFiles(baseFolder, allButIgnoreDir, dirs));
        for (File file : files) {
            String zippedName = file.getAbsolutePath().substring(new File(basePath).getAbsolutePath().length());
            FileInputStream fis = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(zippedName));
            CopyInputStreamWithoutClose(fis, out);
            out.closeEntry();
            fis.close();
        }
        out.close();
    }

    /**
     * Helper utility to unzip files to
     *
     * @param basePath the current directory
     * @param zipName the zip file in the basePath
     */
    public static void Unzip(String basePath, String zipName) {
        
        Enumeration entries;
        ZipFile zipFile;
        try {
            if (!basePath.endsWith(System.getProperty("file.separator"))) {
                basePath += System.getProperty("file.separator");
            }
            
            
            zipFile = new ZipFile(basePath + zipName);
            
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                
                ZipEntry entry = (ZipEntry) entries.nextElement();
                
                
                String filename = entry.getName();
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    //HACK when running Server on Windows
                    if (entry.getName().contains(":")) {
                        filename = entry.getName().substring(entry.getName().indexOf(":\\") + 2);
                    }
                    
                }
                logger.debug("filename in Zip entry: " + filename);
                File destFile = new File(basePath, filename);
                
                File destinationParent = destFile.getParentFile();
                destinationParent.mkdirs();
                if (!entry.isDirectory()) {
                    InputStream in = zipFile.getInputStream(entry);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(basePath + entry.getName()));
                    CopyInputStream(in, out);
                }
            }
            
            zipFile.close();
        } catch (IOException ioe) {
            logger.error(ioe);
            
        }
    }
    
    public static void UnzipSpecial(String basePath, String newBasePath, String zipName) {
        
        String fileSep = System.getProperty("file.separator");
        
        Enumeration entries;
        ZipFile zipFile;
        try {
            if (!basePath.endsWith(System.getProperty("file.separator"))) {
                basePath += System.getProperty("file.separator");
            }
            
            
            zipFile = new ZipFile(basePath + zipName);
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                
                ZipEntry entry = (ZipEntry) entries.nextElement();
                
                
                String filename = entry.getName();
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    //HACK when running Server on Windows
                    if (entry.getName().contains(":")) {
                        filename = entry.getName().substring(entry.getName().indexOf(":\\") + 2);
                    }
                    
                }
                int len = filename.length();
                if (len > newBasePath.length()) {
                    filename = filename.substring(newBasePath.length(), len);
                }
                //System.err.println("Filename:" + filename + " | Entry name: " + entry.getName());
                File destFile = new File(basePath, filename);
                File destinationParent = destFile.getParentFile();
                
                destinationParent.mkdirs();
                if (!entry.isDirectory()) {
                    InputStream in = zipFile.getInputStream(entry);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(basePath + filename));
                    CopyInputStream(in, out);
                }
            }
            
            zipFile.close();
        } catch (IOException ioe) {
            logger.error(ioe);
            
        }
    }
    
    public static void main(String[] args) {
        String zipName = "/Chick-fil-A_May8.zip";
        String basePath = "/home/eric/Desktop/zip";
        String newBasePath = ZipUnzip.findIndexBasePath(basePath, zipName);
        ZipUnzip.UnzipSpecial(basePath, newBasePath, zipName);
    }
    
    public static String findIndexBasePath(String basePath, String zipName) {

        //Separator on  Zip Files is always front slash (OS agnostic)
        String fileSep = "/";
        Enumeration entries;
        ZipFile zipFile;
        String foundAt = null;
        try {
            if (!basePath.endsWith(System.getProperty("file.separator"))) {
                basePath += System.getProperty("file.separator");
            }
            
            System.err.println("Got path " + basePath + zipName);
            zipFile = new ZipFile(basePath + zipName);
            
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                
                ZipEntry entry = (ZipEntry) entries.nextElement();
                
                
                String filename = entry.getName();
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    //HACK when running Server on Windows
                    if (entry.getName().contains(":")) {
                        filename = entry.getName().substring(entry.getName().indexOf(":\\") + 2);
                    }
                    
                }
                String name = entry.getName().toLowerCase();
                if (name.contains("index.html") || name.contains("index.php") || name.contains("index.htm")) {
                    int indexOf = entry.getName().lastIndexOf(fileSep);
                    String found;
                    if (indexOf > 0) {
                        found = entry.getName().substring(0, entry.getName().lastIndexOf(fileSep));
                    } else {
                        found = "";
                    }
                    int count = StringUtils.countMatches(found, fileSep);
                    if (foundAt == null) {
                        foundAt = found;
                    } else {
                        int countAt = StringUtils.countMatches(foundAt, fileSep);
                        if (count < countAt) {
                            foundAt = found;
                        }
                    }
                    
                }
            }
            
            zipFile.close();
        } catch (IOException ioe) {
            logger.error(ioe);
            
        }
        return foundAt;
    }

    /**
     * Will do checksum of a zip folder and folders within the basePath
     *
     * @param zipName
     * @return
     */
    public static long checksumZipFile(String zipName) {
        Enumeration entries;
        ZipFile zipFile;
        String basePath = new File(zipName).getParent();
        File baseFile = new File(basePath);
        File[] files = baseFile.listFiles();
        if (files.length < 1) {
            return -1;
        }
        long checksum = 0;
        try {
            if (!basePath.endsWith("/")) {
                basePath += "/";
            }
            zipFile = new ZipFile(basePath + zipName);
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                checksum += entry.getCrc();
            }
            zipFile.close();
        } catch (IOException ioe) {
            logger.error(ioe);
            return -1;
        }
        return checksum;
    }
}
