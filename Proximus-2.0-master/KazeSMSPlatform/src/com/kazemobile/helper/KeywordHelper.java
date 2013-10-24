/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kazemobile.helper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * Jersey REST client generated for REST resource:KeywordFacadeREST
 * [keywords]<br>
 *  USAGE:
 * <pre>
 *        KeywordHelper client = new KeywordHelper();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rwalker
 */
public class KeywordHelper {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://sms.kazemobile.com:41919/partners/api";

    public KeywordHelper() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        client.addFilter(new LoggingFilter());
        webResource = client.resource(BASE_URI).path("keywords");
    }

    public String keywordMatchCount(String requestSignature, String keyword_requested, String which_short_code) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("count/{0}/short-codes/{1}", new Object[]{keyword_requested, which_short_code}));
        return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.TEXT_PLAIN)
                       .get(String.class);
    }

    public void remove(String requestSignature, String id) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                   .header("x-account-signature", requestSignature)
                   .delete();
    }

    public String countREST(String requestSignature) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path("count");
        return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public <T> T findAll_XML(Class<T> responseType, String requestSignature) throws UniformInterfaceException {
        WebResource resource = webResource;
       return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                       .get(responseType);
    }

    public <T> T findAll_JSON(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T create_XML(String requestSignature,Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.header("x-account-signature", requestSignature)
                       .type(javax.ws.rs.core.MediaType.APPLICATION_XML).post(responseType, requestEntity);
    }

    public <T> T create_JSON(Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }

    public <T> T find_XML(Class<T> responseType, String id) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T find_JSON(Class<T> responseType, String id) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.destroy();
    }
    
}

