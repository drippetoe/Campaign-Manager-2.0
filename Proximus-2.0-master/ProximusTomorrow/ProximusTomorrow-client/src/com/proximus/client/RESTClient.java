/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client;

import com.proximus.util.TimeConstants;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 *
 * @author Eric Johansson
 */
public class RESTClient implements ConnectionKeepAliveStrategy
{

    private long keepAlive;
    private DefaultHttpClient httpClient;

    public RESTClient(long keepAlive)
    {
        this.keepAlive = keepAlive;
        this.httpClient = new DefaultHttpClient();
        this.httpClient.setKeepAliveStrategy(this);
    }

    public HttpResponse GETRequest(URI uri) throws IOException
    {
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = null;
        response = httpClient.execute(httpGet);        
        return response;
    }

    public HttpResponse POSTRequest(URI uri, HttpEntity contentEntity) throws IOException
    {
        HttpResponse response = null;
        //Create POST request
        HttpPost httpPost = new HttpPost(uri);
        //Add FORM to request
        httpPost.setEntity(contentEntity);
        //Execute POST request
        response = httpClient.execute(httpPost);
        return response;
    }

    public HttpResponse PUTRequest(URI uri, HttpEntity contentEntity) throws IOException
    {
        HttpResponse response = null;
        //Create PUT request
        HttpPut httpPut = new HttpPut(uri);
        //Add HttpEntity to request
        httpPut.setEntity(contentEntity);
        //Execute POST request
        response = httpClient.execute(httpPut);
        return response;
    }

    public HttpResponse DELETERequest(URI uri) throws IOException
    {
        HttpResponse response = null;
        //Create DELETE request
        HttpDelete httpDelete = new HttpDelete(uri);
        response = httpClient.execute(httpDelete);
        return response;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        this.httpClient.getConnectionManager().shutdown();
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context)
    {
        // Honor 'keep-alive' header
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext())
        {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout"))
            {
                try
                {
                    return Long.parseLong(value) * TimeConstants.ONE_SECOND_MILLIS;
                } catch (NumberFormatException ignore)
                {
                }
            }
        }
        HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
//        if ("localhost".equalsIgnoreCase(target.getHostName()))
//        {
//            // Keep alive for 5 seconds only
//            return 5 * 1000;
//        } else
//        {
        // otherwise keep alive for 30 seconds
        return this.keepAlive;
//        }
    }
}
