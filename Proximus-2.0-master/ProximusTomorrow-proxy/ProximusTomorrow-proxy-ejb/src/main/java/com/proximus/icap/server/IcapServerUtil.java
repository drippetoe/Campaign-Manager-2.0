/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package com.proximus.icap.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class IcapServerUtil {

    /**
     * Helper function to load content from file.
     *
     * @param file
     * @return #ChannelBuffer buffer to be enbeddded into
     * #HttpResponse.setContent()
     * @throws IOException
     * @see #getStringAsContent
     */
    public static ChannelBuffer getFileAsContent(File file) throws IOException {
        byte[] fileBuffer = FileUtils.readFileToByteArray(file);
        ChannelBuffer fileContent = ChannelBuffers.copiedBuffer(fileBuffer);
        return fileContent;
    }

    /**
     * Helper function to create content from a string.
     *
     * @param string Html text to be enbedded into
     * @return ChannelBuffer buffer to be enbeddded into
     * HttpResponse.setContent()
     * @see #getFileAsContent
     */
    public static ChannelBuffer getStringAsContent(String string) {
        ChannelBuffer content = ChannelBuffers.copiedBuffer(string.getBytes());
        return content;
    }
    

    /**
     * 
     * @param uri "http://www.example.com:80/dir/page.html"
     * @return String "www.example.com" or "example.com" or "subdomain.example.com"
     * @throws MalformedURLException 
     */
    public static String getDomainFromURI(String uri) throws MalformedURLException {
        URL url = new URL(uri);
        return url.getHost();
    }

    
}
