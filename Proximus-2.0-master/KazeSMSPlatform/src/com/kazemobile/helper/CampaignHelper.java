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
 * Jersey REST client generated for REST resource:CampaignExtFacadeREST
 * [campaigns]<br>
 *  USAGE:
 * <pre>
 *        CampaignHelper client = new CampaignHelper();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rwalker
 */
public class CampaignHelper {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://sms.kazemobile.com:41919/partners/api";

    public CampaignHelper() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        client.addFilter(new LoggingFilter());
        webResource = client.resource(BASE_URI).path("campaigns");
    }

    public <T> T createCampaignNotice_XML(String requestSignature, Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/notices", new Object[]{campaignid}))
                          .header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    public <T> T createCampaignNotice_JSON(Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/notices", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }
	
	public <T> T findCustomizedSystemMessage_XML(Class<T> responseType, String campaignid, String customizedMessageId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/customized-system-messages/{1}", new Object[]{campaignid, customizedMessageId}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findCustomizedSystemMessage_JSON(Class<T> responseType, String campaignid, String customizedMessageId) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/customized-system-messages/{1}", new Object[]{campaignid, customizedMessageId}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void updateCampaignNotice_XML(String requestSignature, Object requestEntity, String campaignid, String noticeid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/notices/{1}", new Object[]{campaignid, noticeid}))
                .header("x-account-signature", requestSignature)
                .type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateCampaignNotice_JSON(Object requestEntity, String campaignid, String noticeid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/notices/{1}", new Object[]{campaignid, noticeid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
 }

    public <T> T findCampaignNotices_XML(String requestSignature, Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/notices", new Object[]{campaignid}));
        return resource.header("x-account-signature", requestSignature)
                .accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findCampaignNotices_JSON(Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/notices", new Object[]{campaignid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void removeCampaign(String campaignid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{campaignid})).delete();
    }

    public <T> T createCampaign_XML(String requestSignature, Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        return webResource.header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    public <T> T createCampaign_JSON(Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }

    public <T> T findCampaignNotice_XML(Class<T> responseType, String campaignid, String noticeid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/notices/{1}", new Object[]{campaignid, noticeid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findCampaignNotice_JSON(Class<T> responseType, String campaignid, String noticeid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/notices/{1}", new Object[]{campaignid, noticeid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findCustomizedSystemMessages_XML(String requestSignature, Class<T> responseType, String campaignid) throws UniformInterfaceException {
         return webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages", new Object[]{campaignid}))
                            .header("x-account-signature", requestSignature)
                            .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                            .get(responseType);
       
    }

    public <T> T findCustomizedSystemMessages_JSON(Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/customized-system-messages", new Object[]{campaignid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }
    public <T> T findCampaignSteps_XML(String requestSignature, Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/steps", new Object[]{campaignid}));
        return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                       .get(responseType);
    }

    public <T> T findCampaignSteps_JSON(Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/steps", new Object[]{campaignid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }
	
	 public void updateCustomizedSystemMessage_XML(Object requestEntity, String campaignid, String customizedMessageId) throws UniformInterfaceException {
     webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages/{1}", new Object[]{campaignid, customizedMessageId})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateCustomizedSystemMessage_JSON(Object requestEntity, String campaignid, String customizedMessageId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages/{1}", new Object[]{campaignid, customizedMessageId})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    
    }

    public void removeCampaignNotice(String campaignid, String noticeid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/notices/{1}", new Object[]{campaignid, noticeid})).delete();
    }

    public void removeCampaignStep(String campaignid, String stepid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/steps/{1}", new Object[]{campaignid, stepid})).delete();
    }

    public void updateCampaign_XML(Object requestEntity, String campaignid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateCampaign_JSON(Object requestEntity, String campaignid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    }

    public <T> T findCampaign_XML(String requestSignature, Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{campaignid}));
        return resource.header("x-account-signature", requestSignature)
                       .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                       .get(responseType);
    }

    public <T> T findCampaign_JSON(Class<T> responseType, String campaignid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{campaignid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void updateCampaignStep_XML(Object requestEntity, String campaignid, String stepid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/steps/{1}", new Object[]{campaignid, stepid})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void updateCampaignStep_JSON(Object requestEntity, String campaignid, String stepid) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/steps/{1}", new Object[]{campaignid, stepid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    }

    public String countCampaigns() throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path("count");
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }
	 public void removeCustomizedSystemMessage(String campaignid, String customizedMessageId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages/{1}", new Object[]{campaignid, customizedMessageId})).delete();
    
    }

    public <T> T findCampaigns_XML(Class<T> responseType, String pg, String pgSize) throws UniformInterfaceException {
        WebResource resource = webResource;
        if (pg != null) {
            resource = resource.queryParam("pg", pg);
        }
        if (pgSize != null) {
            resource = resource.queryParam("pgSize", pgSize);
        }
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findCampaigns_JSON(Class<T> responseType, String pg, String pgSize) throws UniformInterfaceException {
        WebResource resource = webResource;
        if (pg != null) {
            resource = resource.queryParam("pg", pg);
        }
        if (pgSize != null) {
            resource = resource.queryParam("pgSize", pgSize);
        }
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findCampaignStep_XML(Class<T> responseType, String campaignid, String stepid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/steps/{1}", new Object[]{campaignid, stepid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findCampaignStep_JSON(Class<T> responseType, String campaignid, String stepid) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/steps/{1}", new Object[]{campaignid, stepid}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

 	public <T> T createCustomizedSystemMessage_XML(String requestSignature, Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages", new Object[]{campaignid}))
                          .header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    public <T> T createCustomizedSystemMessage_JSON(Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/customized-system-messages", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }
    public <T> T createCampaignStep_XML(String requestSignature, Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/steps", new Object[]{campaignid}))
                          .header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .post(responseType, requestEntity);
    }

    public <T> T createCampaignStep_JSON(Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/steps", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }
    
     public <T> T promptCampaignEvent_XML(String requestSignature, Class<T> responseType, Object requestEntity, String prompt) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("prompts/{0}", new Object[]{prompt}))
                          .header("x-account-signature", requestSignature)
                          .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                          .accept(javax.ws.rs.core.MediaType.TEXT_PLAIN)
                          .post(responseType, requestEntity);
    }
      public <T> T createCampaignKeywordAlias_XML(String requestSignature, Class<T> responseType, Object requestEntity, String campaignid) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}/keyword-aliases", new Object[]{campaignid})).type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                            .header("x-account-signature", requestSignature)
                            .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                            .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                            .post(responseType, requestEntity);
    }
      public void removeAlias(String requestSignature, String campaignId, String aliasId) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/keyword-aliases/{1}", new Object[]{campaignId, aliasId}))
                   .header("x-account-signature", requestSignature)
                   .delete();
    }


    public void close() {
        client.destroy();
    }
    
}

