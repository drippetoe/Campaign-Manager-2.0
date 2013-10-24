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
 * Jersey REST client generated for REST resource:DistributionListExtFacadeREST
 * [distribution-lists]<br>
 *  USAGE:
 * <pre>
 *        DistributionHelper client = new DistributionHelper();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rwalker
 */
public class DistributionHelper {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://sms.kazemobile.com:41919/partners/api";

    public DistributionHelper() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        client.addFilter(new LoggingFilter());
        webResource = client.resource(BASE_URI).path("distribution-lists");
    }

    // public <T> T createDistributionListEntry_XML(Class<T> responseType, Object requestEntity, String distributionListId) throws UniformInterfaceException {
    //    return webResource.path(java.text.MessageFormat.format("{0}/entries", new Object[]{distributionListId})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).post(responseType, requestEntity);
    // }
    public <T> T createDistributionListEntry_XML(String requestSignature, Class<T> responseType, Object requestEntity, String distributionListId) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/entries", new Object[]{distributionListId}))
                          .header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    
    public <T> T createDistributionListEntry_JSON(Class<T> responseType, Object requestEntity, String distributionListId) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/entries", new Object[]{distributionListId})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }

    public <T> T findDistributionLists_XML(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findDistributionLists_JSON(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T createDistributionList_XML(String requestSignature, Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        return webResource.header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    public <T> T createDistributionList_JSON(Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }

    public void updateDistributionListEntry_XML(Object requestEntity, String distributionListId, String entryid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/entries/{1}", new Object[]{distributionListId, entryid})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateDistributionListEntry_JSON(Object requestEntity, String distributionListId, String entryid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/entries/{1}", new Object[]{distributionListId, entryid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    }

    public void removeDistributionListEntry(String requestSignature, String distributionListId, String entryid) throws UniformInterfaceException {
      
        webResource.path(java.text.MessageFormat.format("{0}/entries/{1}", new Object[]{distributionListId, entryid}))
                .header("x-account-signature", requestSignature)
                .delete();
    }   
    public <T> T findDistributionListEntry_XML(Class<T> responseType, String distributionListId, String entryid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/entries/{1}", new Object[]{distributionListId, entryid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findDistributionListEntry_JSON(Class<T> responseType, String distributionListId, String entryid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/entries/{1}", new Object[]{distributionListId, entryid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void removeDistributionList(String distributionListId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{distributionListId})).delete();
    }

    public <T> T findDistributionListEntries_XML(String requestSignature, Class<T> responseType, String distributionListId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/entries", new Object[]{distributionListId}));
        return resource.header("x-account-signature", requestSignature).accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findDistributionListEntries_JSON(Class<T> responseType, String distributionListId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/entries", new Object[]{distributionListId}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public String countDistributionListEntries(String distributionListId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/entries/count", new Object[]{distributionListId}));
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public <T> T findDistributionList_XML(String requestSignature, Class<T> responseType, String distributionListId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{distributionListId}));
        return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                       .get(responseType);
    }

    public <T> T findDistributionList_JSON(Class<T> responseType, String distributionListId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{distributionListId}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public String countDistributionLists() throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path("count");
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public void updateDistributionList_XML(Object requestEntity, String distributionListId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{distributionListId})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateDistributionList_JSON(Object requestEntity, String distributionListId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{distributionListId})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    }

    public void close() {
        client.destroy();
    }
    
}
