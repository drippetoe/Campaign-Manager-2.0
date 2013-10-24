/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client;

import java.io.File;
import java.util.List;

/**
 *
 * @author Eric Johansson
 */
public class FilesystemChecker {
    public static boolean initDir(String dir) {
        return new File(dir).mkdirs();        
    }
    
    public static void initDirs(List<File> dirs) {
        for (File file : dirs) {
            file.mkdirs();
        }        
    }
}
