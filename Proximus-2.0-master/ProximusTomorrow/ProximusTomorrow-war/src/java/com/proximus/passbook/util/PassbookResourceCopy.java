/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * 
 * @author dshaw
 */
public class PassbookResourceCopy {

    private static final File passbookSourceTemplate = new File("/home/proximus/server/passbook/template");
    
    public static void copyResourcesToDirectory(File destinationDir) throws FileNotFoundException, IOException
    {
        if ( ! destinationDir.exists() && destinationDir.isDirectory())
        {
            throw new FileNotFoundException(destinationDir.getAbsolutePath() + " does not exist");
        }
        for (File file : passbookSourceTemplate.listFiles()) {
            FileUtils.copyFileToDirectory(file, destinationDir);
        }
    }
}
