/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author Gaxiola
 */
public class FileChecksum
{
    public static void downloadFile(String path, String name, URI uri)
    {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(uri.toString());
            HttpResponse response = client.execute(get);

            InputStream input = null;
            OutputStream output = null;
            byte[] buffer = new byte[1024];

            try {
                input = response.getEntity().getContent();

                new File(path).mkdirs();
                output = new FileOutputStream(new File(path + name));
                for (int length; (length = input.read(buffer)) > 0;) {
                    output.write(buffer, 0, length);
                }
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
            }

        } catch (IOException ex) {
        }

    }

    /**
     * Will get the specific zip file and calculate the checksum of all the files
     * @param path the base directory
     * @param zipname the filename
     * @return the checksum
     * @throws Exception 
     */
    public static String readCheckSumZip(String path, String zipname) throws Exception
    {

        CheckedInputStream checksum = new CheckedInputStream(new FileInputStream(path + zipname), new Adler32());
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
        ZipEntry entry;

        String tempBase = path + "temp/";
        new File(tempBase).mkdirs();

        while ((entry = zis.getNextEntry()) != null) {
            String tempPath = tempBase;
            int size;
            byte[] buffer = new byte[2048];
            String actualName = "";
            if (entry.getName().contains("/")) {
                int pivot = entry.getName().lastIndexOf("/") + 1;
                actualName = entry.getName().substring(pivot, entry.getName().length());
                tempPath = tempPath + entry.getName().substring(0, pivot);
            } else {
                actualName = entry.getName();
            }
            new File(tempPath).mkdirs();
            if(actualName.isEmpty()) {
                continue;
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(new File(tempPath + actualName)), buffer.length);
            while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, size);
            }
            bos.flush();
            bos.close();
        }
        zis.close();
        FileUtils.deleteDirectory(new File(tempBase));
        return checksum.getChecksum().getValue() + "";

    }

    public static void main(String[] args) throws Exception
    {
        String path = "C:/proximus/campaigns/1/";
        String filename = "MirrorBox.zip";
        System.out.println(new File(path + filename).exists());
        String checksum = FileChecksum.readCheckSumZip(path, filename);
        System.out.println("Checksum " + checksum);

        checksum = FileChecksum.readCheckSumZip("C:/proximus/server/1/", "MirrorBox.zip");
        System.out.println("Checksum for MirrorBox is: " + checksum);

    }
}
