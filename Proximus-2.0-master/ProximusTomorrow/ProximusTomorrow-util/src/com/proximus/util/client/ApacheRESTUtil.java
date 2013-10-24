/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.util.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author ejohansson
 */
public class ApacheRESTUtil
{
    private static final Logger logger = Logger.getLogger(ApacheRESTUtil.class.getName());
    
    public static void PrintResponse(HttpResponse response) throws IOException
    {
        
        HttpEntity responseEntity = response.getEntity();
        logger.log(Priority.DEBUG, response.getStatusLine().toString());
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String output;
        while ((output = br.readLine()) != null) {
            logger.log(Priority.DEBUG, output);
        }
        EntityUtils.consume(responseEntity);
    }
    
    public static String ResponseToString(HttpResponse response) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();     
        
        HttpEntity responseEntity = response.getEntity();
        //logger.log(Priority.DEBUG, response.getStatusLine().toString());
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String output;
        while ((output = br.readLine()) != null) {
            stringBuilder.append(output);
            //logger.log(Priority.DEBUG, output);
        }
        return stringBuilder.toString();
    }

    public static HttpEntity CreateFormEntity(List<NameValuePair> formParams) throws UnsupportedEncodingException
    {
        return new UrlEncodedFormEntity(formParams);
    }

    public static HttpEntity CreateXMLEntity(String xml) throws UnsupportedEncodingException
    {
        return new InputStreamEntity(new ByteArrayInputStream(xml.getBytes("UTF-8")), xml.getBytes().length);
    }

    public static HttpEntity CreateFileUploadEntity(File file) throws UnsupportedEncodingException
    {
        FileBody fileBody = new FileBody(file);
        StringBody fileName = new StringBody(file.getName());
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("body", fileBody);
        reqEntity.addPart("metadata", fileName); 
        
        return reqEntity;

    }

    public static HttpEntity CreateFileEntity(File file)
    {
        return new FileEntity(file, MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * Helper method download file from URI
     * @param response
     * @param path
     * @return  
     */
    public static boolean downloadFile(HttpResponse response, File path)
    {
        InputStream input = null;
        OutputStream output = null;
        byte[] buffer = new byte[1024];
        try {
            input = response.getEntity().getContent();
            path.getParentFile().mkdirs();
            output = new FileOutputStream(path);
            for (int length; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            return true;
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        } catch (IllegalStateException ex) {
            logger.log(Priority.ERROR, ex);
        } finally {
            try {
                EntityUtils.consume(response.getEntity());
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException ex) {
                        logger.log(Priority.ERROR, ex);
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        logger.log(Priority.ERROR, ex);
                    }
                }
            } catch (IOException ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
        return false;

    }
}
