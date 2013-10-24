package com.proximus.icap.server;

/**
 * *****************************************************************************
 * Copyright 2012 Michael Mimo Moratti
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import ch.mimo.netty.handler.codec.icap.DefaultIcapResponse;
import ch.mimo.netty.handler.codec.icap.IcapChunkAggregator;
import ch.mimo.netty.handler.codec.icap.IcapHeaders;
import ch.mimo.netty.handler.codec.icap.IcapMethod;
import ch.mimo.netty.handler.codec.icap.IcapRequest;
import ch.mimo.netty.handler.codec.icap.IcapResponse;
import ch.mimo.netty.handler.codec.icap.IcapResponseStatus;
import ch.mimo.netty.handler.codec.icap.IcapVersion;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

public class IcapServerHandler extends SimpleChannelUpstreamHandler {

    private static final Pattern PATTERN = Pattern.compile("(.*?<body.*?>)(.*?)(</body>.*?)");
    private boolean continueWasSent;

    static {
        AmazonUtilInjection.createOfferMaps();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        IcapResponse response = null;
        if (message instanceof IcapRequest) {
            IcapRequest request = (IcapRequest) message;
            //System.out.println("");
            //System.out.println("---------------------------- receiving " + request.getMethod() + " ----------------------------");
            //System.out.print(message.toString());
            if (request.getMethod().equals(IcapMethod.OPTIONS)) {
                response = new DefaultIcapResponse(IcapVersion.ICAP_1_0, IcapResponseStatus.OK);
                response.addHeader("Options-TTL", "3600");
                response.addHeader("Service-ID", "Proximus Icap Server");
                response.addHeader("Allow", "204");
                //response.addHeader("Preview", "1024");
                response.addHeader("Methods", "REQMOD, RESPMOD");
                //response.addHeader("Methods", "RESPMOD");

            } else if (request.isPreviewMessage()) {
                response = new DefaultIcapResponse(IcapVersion.ICAP_1_0, IcapResponseStatus.NO_CONTENT);
            } else {
                //System.out.print(message.toString());
                response = new DefaultIcapResponse(IcapVersion.ICAP_1_0, IcapResponseStatus.OK);
                response.addHeader(IcapHeaders.Names.ISTAG, "Proximus-Server-1.0");

                /*
                 * DEBUG
                 */
                
                //debugHeaders(request);
                
                /**
                 * If an ICAP server receives a request that does not have
                 * "Allow: 204", it MUST NOT reply with a 204. In this case, an
                 * ICAP server MUST return the entire message back to the
                 * client, even though it is identical to the message it
                 * received.
                 */
                //response.setHttpResponse(myResponse(request));
                if (request.getMethod().equals(IcapMethod.REQMOD)) {

                    //filterCookies(request);
                    //if (!allowRedirect(request)) {
                    /*if (allowRedirect(request)) {
                     //System.err.println("*********************************************************************************************");
                     //System.err.println("*********************************************************************************************");
                     //System.err.println("*********************************************************************************************");
                     HttpResponse myResponse = myInjectiveResponse(request, jsADInject());
                     if (myResponse != null) {
                     myResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                     //System.out.println(myResponse.toString());
                     response.setHttpResponse(myResponse);
                     } else {
                     response.setHttpResponse(request.getHttpResponse());
                     }
                     } else {
                     HttpResponse myResponse = getAd();
                     myResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                     //System.out.println(myResponse.toString());
                     response.setHttpResponse(myResponse);
                     //response.setHttpResponse(request.getHttpResponse());
                     }*/
                    /*
                     * gzip;q=0, identity;q=1.0
                     */
                    //System.err.println("Got uri:" + getSolrQuery(request));
                    
                    String uncompressed = "gzip";
                    HttpRequest httpRequest = request.getHttpRequest();
                    httpRequest.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, uncompressed);

                    response.setHttpRequest(httpRequest);
                    response.setHttpResponse(request.getHttpResponse());
                }
                //iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 3128
                if (request.getMethod().equals(IcapMethod.RESPMOD)) {
                    //filterCookies(request);
                    /*HttpResponse myResponse = getAd();
                     myResponse.setStatus(HttpResponseStatus.OK);
                     response.setHttpResponse(myResponse);*/
                    //response.setHttpResponse(request.getHttpResponse());
                    //if (allowRedirect(request)) {
                    /**
                     * Injective
                     */
                    
                    /**
                     * Captive
                     */
                    //IcapChunkAggregator ica = new IcapChunkAggregator(maxContentLength);
                    ChannelBuffer httpBody = IcapChunkAggregator.extractHttpBodyContentFromIcapMessage(request);
                    if (httpBody != null && httpBody.readable()) {
                        if (request.getHttpResponse().containsHeader(HttpHeaders.Names.CONTENT_TYPE)) {
                            if (request.getHttpResponse().getHeader(HttpHeaders.Names.CONTENT_TYPE).contains("text/html")) {
                                String aq = isAmazonQuery(request);

                                debugHeaders(request);
                                if (aq != null) {
                                    System.out.println("\t" + request.getHttpRequest().getUri());
                                    HttpResponse httpResponse = request.getHttpResponse();
                                    httpResponse = injectAd(httpResponse, httpBody, aq);
                                    Set<String> headerNames = httpResponse.getHeaderNames();
                                } else {
                                    System.out.println(request.getHeader(IcapHeaders.Names.HOST) + "\t" + request.getHttpRequest().getUri());
                                }

                                /*for (String header : headerNames) {
                                 System.out.println("\t" + header + ": " + httpResponse.getHeader(header));
                                 }*/

                            }
                        }
                    }

                    //extractHttpBodyContentFromIcapMessage(IcapMessage message)
                    HttpResponse myResponse = myInjectiveResponse(request);
                    if (myResponse != null) {

                        myResponse.setStatus(HttpResponseStatus.OK);
                        //System.out.println(myResponse.toString());
                        response.setHttpResponse(myResponse);
                    } else {
                        response.setHttpResponse(request.getHttpResponse());
                    }

                    /*} else {
                     HttpResponse myResponse = getAd();
                     myResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                     //System.out.println(myResponse.toString());
                     response.setHttpResponse(myResponse);
                     //response.setHttpResponse(request.getHttpResponse());
                     }*/
                    /*if (!allowRedirect(request)) {
                     HttpResponse myResponse = getAd();
                     myResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                     //System.out.println(myResponse.toString());
                     response.setHttpResponse(myResponse);
                     } else {
                     response.setHttpResponse(request.getHttpResponse());
                     }*/
                }
                //response.setHttpResponse(request.getHttpResponse());
            }
            //System.out.println("");
            //System.out.println("---------------------------- sending " + response.getStatus() + " ----------------------------");
            //System.out.print(response.toString());
            //System.out.println("IP " + ctx.getChannel().getRemoteAddress().toString());
            ctx.getChannel().write(response);
        }
    }

    private void debugHeaders(IcapRequest request) {
        Set<String> headerNames = request.getHeaderNames();
        System.out.println("*************** ICAP HEADERS ***************");
        for (String header : headerNames) {
            System.out.println("\t" + header + ": " + request.getHeader(header));
        }

        headerNames = request.getHttpRequest().getHeaderNames();
        System.out.println("*************** REQUEST HEADERS ***************");
        for (String header : headerNames) {
            System.out.println("\t" + header + ": " + request.getHttpRequest().getHeader(header));
        }
        headerNames = request.getHttpResponse().getHeaderNames();
        System.out.println("*************** RESPONSE HEADERS ***************");
        for (String header : headerNames) {
            System.out.println("\t" + header + ": " + request.getHttpResponse().getHeader(header));
        }
    }

    private String gzip(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;
        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(input.getBytes("UTF-8"));
        } catch (Exception ex) {
        } finally {
            if (gzos != null) {
                try {
                    gzos.close();
                } catch (Exception ignore) {
                }
            };
        }
        String result = baos.toString();
        if (result != null) {
            return result;
        }
        return input;
    }

    public String gunzip(ChannelBuffer content) {
        try {
            InputStream in = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(content.array())));
            return convertStreamToString(in);
        } catch (Exception e) {
            System.err.println("Unable to unzip");
            e.printStackTrace();
        }
        return content.toString(CharsetUtil.UTF_8);
    }

    private HttpResponse injectAd(HttpResponse httpResponse, ChannelBuffer httpBody, String query) {
        String body = null;

        if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE).contains("charset")) {
            String[] split = httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE).split("charset=");
            body = httpBody.toString(Charset.forName(split[split.length - 1]));
            //System.out.println("Got charset: " + split[split.length - 1]);
        } else {
            body = httpBody.toString(CharsetUtil.UTF_8);
        }
        /**
         * UNCOMPRESS IF NEEDED
         */
        if (httpResponse.containsHeader(HttpHeaders.Names.CONTENT_ENCODING)) {
            /* GZIP */
            if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_ENCODING).contains("gzip")) {
                body = gunzip(httpBody);
                //System.out.println("\tUnzipping");
            }
        }
        if (body != null) {
            if (StringUtils.containsIgnoreCase(body, "</body>")) {
                String js = "";
                try {
                    js = AmazonUtilInjection.adInjectionForAmazonUrl(query);
                    if (js != null) {
                        String newBody = null;
                        String[] split = body.split("</body>");
                        if (split.length > 1) {
                            newBody = split[0] + js + "</body>" + split[1];
                        }
                        if (newBody != null) {
                            body = newBody;
                        }
                    } else {
                        System.err.println("NO Ads");
                        return httpResponse;
                    }
                } catch (Exception ex) {
                    System.err.println(ex);
                    return httpResponse;
                }
                /**
                 * COMPRESS IF NEEDED
                 */
                /*
                 if (httpResponse.containsHeader(HttpHeaders.Names.CONTENT_ENCODING)) {
                 //
                 if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_ENCODING).contains("gzip")) {
                 body = gzip(body);
                 System.out.println("\tZipping");
                 }
                 }*/
                ChannelBuffer newContent = ChannelBuffers.copiedBuffer(body.getBytes());
                if (newContent != null) {
                    httpResponse.setContent(newContent);
                    //System.out.println("After SET SIZE: " + httpResponse.getHeader(HttpHeaders.Names.CONTENT_LENGTH));
                    httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, (body.getBytes().length));
                    httpResponse.removeHeader(HttpHeaders.Names.CONTENT_ENCODING);
                    System.out.println("\tReturning new content");
//                    System.out.println("/*****************************************************************/");
//                    System.out.println(body);
//                    System.out.println("/*****************************************************************/");
                    return httpResponse;
                }
            } else {
                System.out.println("Failed to find <body> TAG");
            }
        } else {
            System.err.println("NULLBODY");

        }

        return httpResponse;
    }

    public void filterCookies(IcapRequest icapRequest) {
        /*
         HttpResponse httpResponse = icapRequest.getHttpResponse();
         HttpRequest httpRequest = icapRequest.getHttpRequest();
         try {
            
         String cookieString = httpRequest.getHeader(HttpHeaders.Names.COOKIE);
         //System.out.println("Found cookie string: [" + cookieString + "]");
         // Encode the cookie.
         if (cookieString != null) {
         CookieDecoder cookieDecoder = new CookieDecoder();
         Set<Cookie> cookies = cookieDecoder.decode(cookieString);
         if (!cookies.isEmpty()) {
         // Reset the cookies if necessary.
         CookieEncoder cookieEncoder = new CookieEncoder(true);
         for (Cookie cookie : cookies) {
         cookieEncoder.addCookie(cookie);
         //System.out.println("Found cookie: [" + cookie.getName() + " = " + cookie.getValue() + "]");
         //response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
         }
         }
         } else {
         // Browser sent no cookie.  Add some.
         //System.out.println("Creating cookies!");
         CookieEncoder cookieEncoder = new CookieEncoder(true);
         cookieEncoder.addCookie("erics-cookie", "value1");
         response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
         cookieEncoder.addCookie("proximus-cookie", "value2");
         response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
         }
         } catch (Exception ex) {
         //System.err.println(ex);
         }
         * */
    }

    public HttpResponse myResponse(IcapRequest request) {
        HttpResponse httpResponse = request.getHttpResponse();
        try {
            //System.out.println("------------------------------------------------BUILDING RESPONSE------------------------------------------");
            if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE).contains("text/html")) {
                //System.out.println("Contains html content");
                ChannelBuffer content = httpResponse.getContent();
                try {
                    if (httpResponse.getHeader(HttpHeaders.Names.ACCEPT_ENCODING).contains("gzip")) {
                        //System.out.println("*** GZIPPED!!");
                        InputStream channelBufferInputStream = new ChannelBufferInputStream(content);
                        GZIPInputStream gzipInputStream = new GZIPInputStream(channelBufferInputStream);
                        String unzippedContent = convertStreamToString(gzipInputStream);
                        //System.out.println("*** UNZIPPED: " + unzippedContent);
                    }
                } catch (Exception ex) {
                    //System.out.println("\tNot compressed***");
                }

                if (content.readable()) {
                    String xml = content.toString(CharsetUtil.UTF_8);

                    if (StringUtils.containsIgnoreCase(xml, "<html")) {
                        ////System.out.println(xml);
                        //System.out.println("SECRET INJECT");
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        Random rand = new Random();
                        int adNr = rand.nextInt(2);
                        byte[] fileBuffer = FileUtils.readFileToByteArray(new File("/home/eric/ad" + adNr + ".html"));
                        ChannelBuffer newContent = ChannelBuffers.copiedBuffer(fileBuffer);
                        //ChannelBuffer newContent = ChannelBuffers.copiedBuffer("<html><body><H1>SECRET INJECT</h1></body></html>".getBytes(), CharsetUtil.UTF_8);
                        response.setChunked(false);
                        response.setContent(newContent);
                        List<Entry<String, String>> headers = httpResponse.getHeaders();
                        for (Entry<String, String> entry : headers) {
                            response.setHeader(entry.getKey(), entry.getValue());
                        }
                        response.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.DEFLATE);
                        //System.out.println("------------------------------------------------RETURNING------------------------------------------");
                        //System.out.println(response.toString());

                        return response;
                    }

                }


            }
        } catch (Exception ex) {
            //System.out.println("\n------> Error: " + ex.getMessage() + "\n\n");
        }
        return httpResponse;
    }

    /*
     * solr
     */
    public String isAmazonQuery(IcapRequest request) {
        String[] allowed = {"amazon.com"};
        List<String> allowedUrls = Arrays.asList(allowed);
        String uri = null;
        uri = request.getHttpRequest().getUri();
        if (uri != null) {
            String domain = "";
            try {
                domain = IcapServerUtil.getDomainFromURI(uri);
            } catch (MalformedURLException ex) {
                //System.err.println(ex);
                return null;
            }
            domain = domain.toLowerCase().trim();
            for (String test : allowedUrls) {
                if (domain.contains(test)) {
                    if (request.getHttpRequest().getUri().contains("field-keyword") || request.getHttpRequest().getUri().contains("&k=") || request.getHttpRequest().getUri().contains("?k=")) {
                        return request.getHttpRequest().getUri();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Injective
     *
     */
    public HttpResponse myInjectiveResponse(IcapRequest request) {
        HttpResponse httpResponse = request.getHttpResponse();
        /**
         * cnn.com and google.com sends redirects return and follow
         */
        if (httpResponse.getStatus().getCode() > 200) {
            //System.err.println("RETURNING");
            return null;
        }
        try {
            //System.out.println("------------------------------------------------BUILDING RESPONSE------------------------------------------");
            if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE).contains("text/html")) {
                //System.out.println("Contains html content");
                ChannelBuffer content = httpResponse.getContent();



                if (content.readable()) {
                    String xml = content.toString(CharsetUtil.UTF_8);

                    boolean zipped = false;
                    if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_ENCODING) != null && httpResponse.getHeader(HttpHeaders.Names.CONTENT_ENCODING).contains("gzip")) {
                        //System.out.println("-----------GZIPPED RESPONSE ****----****---***--");
                        try {
                            InputStream in = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(content.array())));
                            String unzippedContent = convertStreamToString(in);
                            ////System.err.println(unzippedContent);
                            xml = unzippedContent;
                            zipped = true;
                        } catch (Exception ex) {
                            //System.err.println(ex);
                        }
                    }
                    //System.out.println(xml);
                    if (StringUtils.containsIgnoreCase(xml, "<html")) {
                        ////System.out.println(xml);
                        //System.out.println("SECRET INJECT");
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        Random rand = new Random();
                        int adNr = rand.nextInt(2);
                        String[] split = xml.split("</body>");
                        if (split.length > 0) {
                            String stringData = split[0] + jsADInject() + "</body>" + split[1];
                            ChannelBuffer newContent = null;
                            if (zipped) {
                                //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ZIPPING $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                                //System.out.println(stringData);
                                try {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    GZIPOutputStream gzos = new GZIPOutputStream(baos);
                                    gzos.write(stringData.getBytes());
                                    gzos.flush();
                                    //System.out.println("########################ZIPPED############################");
                                    newContent = ChannelBuffers.copiedBuffer(baos.toByteArray());
                                    gzos.close();
                                } catch (Exception ex) {
                                    //System.err.println(ex);
                                }

                            } else {
                                newContent = ChannelBuffers.copiedBuffer(stringData.getBytes());
                            }
                            //response.setChunked(false);

                            response.setContent(newContent);
                            response.setContent(content);
                            List<Entry<String, String>> headers = httpResponse.getHeaders();
                            for (Entry<String, String> entry : headers) {
                                response.setHeader(entry.getKey(), entry.getValue());
                            }
                            response.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
                            //System.out.println("------------------------------------------------RETURNING------------------------------------------");
                            //System.out.println(response.toString());

                            return response;
                        }
                        return null;
                    }

                } else {
                    return null;
                }


            }
        } catch (Exception ex) {
            //System.out.println("\n------> Error: " + ex.getMessage() + "\n\n");
        }
        return null;
    }

    /**
     * If return null then continue
     *
     * @param request
     * @return
     */
    public boolean allowRedirect(IcapRequest request) {
        String[] allowed = {"cnn.com", "turner.com", "amazon.com", "google.com", "software-engineering.se"};
        List<String> allowedUrls = Arrays.asList(allowed);
        String uri = null;
        uri = request.getHttpRequest().getUri();
        if (uri != null) {
            String domain = "";
            try {
                domain = IcapServerUtil.getDomainFromURI(uri);
            } catch (MalformedURLException ex) {
                //System.err.println(ex);
                return false;
            }
            domain = domain.toLowerCase().trim();
            for (String test : allowedUrls) {
                if (domain.contains(test)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean injectJSOn(IcapRequest request, String[] sites) {
        //String[] allowed = {"cnn.com", "turner.com", "amazon.com"};
        List<String> allowedUrls = Arrays.asList(sites);
        String uri = null;
        uri = request.getHttpRequest().getUri();
        if (uri != null) {
            String domain = "";
            try {
                domain = IcapServerUtil.getDomainFromURI(uri);
            } catch (MalformedURLException ex) {
                //System.err.println(ex);
                return false;
            }
            domain = domain.toLowerCase().trim();
            for (String test : allowedUrls) {
                if (domain.contains(test)) {
                    return true;
                }
            }
        }
        return false;
    }

    public HttpResponse getAd() {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);
        try {
            Random rand = new Random();
            int adNr = rand.nextInt(2);
            byte[] fileBuffer = FileUtils.readFileToByteArray(new File("/home/eric/ad" + adNr + ".html"));
            ChannelBuffer newContent = ChannelBuffers.copiedBuffer(fileBuffer);
            //ChannelBuffer newContent = ChannelBuffers.copiedBuffer("<html><body><H1>SECRET INJECT</h1></body></html>".getBytes(), CharsetUtil.UTF_8);
            response.setChunked(false);
            response.setContent(newContent);
            return response;
        } catch (Exception ex) {
            Logger.getLogger(IcapServerHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NO_CONTENT);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        //System.out.println("");
        //System.out.println("---------------------------- exception ----------------------------");
        e.getCause().printStackTrace();

    }

    public static String jsADInject() {
        return "<script type='text/javascript'>"
                + "var pBody = document.body;\n"
                + "var htmlNode = document.createElement(\"div\");\n"
                + "htmlNode.innerHTML = '<div style=\"opacity : 0.7; filter:alpha(opacity=70); position: fixed; top: 0; left: 0; bottom: 0; right: 0; background-color: black; width: 100%; z-index: 9000; height: auto;\"></div>\\n\\\n"
                + "<div style=\"line-height: 1.2; vertical-align:baseline; position: absolute; top: 0; background-color: white; width: 100%; height: auto; margin: 0 auto 0 auto; z-index: 9005; font-family: helvetica, sans-serif; font-size: 16pt; font-weight: normal; font-style: normal;\" id=\"pmbox\"><table style=\"width: 100%; height: 60%; background-color: white; border: 1px solid black; z-index: 9005;\"><tr><td style=\"width: 8%;\"></td><td style=\"width: 30%;\">"
                + "<center><img src=\"http://www.homedepot.com/catalog/productImages/300/e2/e27005c4-1886-48d9-80d6-ace3ccb46a74_300.jpg\"/></center>"
                + "<div style=\"font-size: 1.6em; text-align: center;\"><div style=\"opacity: 0.7; filter:alpha(opacity=70); text-decoration: line-through; color: black; display: inline; width: 50%;\">$89.00</div><div style=\"color: green; text-decoration: none; display: inline; width: 50%;\">$80.10</div><div>"
                + "Ryobi 1/2 in. 18-Volt <br/>Cordless Drill Kit"
                + "</div><div>171 Reviews</div></div></td><td style=\"width: 54%;\"><center><img style=\"position: static; vertical-align: top; max-width: 30%;\" src=\"http://static.mybanktracker.com/bank-news/wp-content/uploads/2010/08/home-depot-logo.jpg\"/><br/><span style=\"font-size: 2.6em;\">Wait just a second...</span><br/><span style=\"font-size: 1.6em;\">We can offer you a 10% discount<br/>if you buy this item in-store today!</span></center></td><td style=\"width: 8%;\"></td></tr><tr><td colspan=\"4\"><a style=\"text-decoration: none;color: white;\" href=\"#\"><div style=\"font-size: 2.5em; font-weight: bold; color: white; padding: 3%; text-decoration: none; text-align: center; -webkit-border-radius: 28px; -moz-border-radius: 28px; behavior: url(/css/border-radius.htc); border-radius: 28px; background: #f26323; width: 80%; margin: 3% auto 3% auto;\">Take me to the offer</div></a></td></tr><tr><td colspan=\"4\"><a style=\"text-decoration: none;color: white;\" href=\"#\"><div id=\"pmCloseLink\" style=\"font-size: 2.5em; font-weight: bold; color: white; padding: 3%; text-decoration: none; text-align: center; -webkit-border-radius: 28px; -moz-border-radius: 28px; behavior: url(/css/border-radius.htc); border-radius: 28px; background-color: grey; width: 80%; margin: 0 auto 3% auto;\">I don\\'t want the offer</div></a></td></tr></table></div>';\n"
                + "var closeLink = document.getElementById(\"pmCloseLink\");\n"
                + "if(closeLink===null){\n"
                + "    pBody.appendChild(htmlNode);\n"
                + "    closeLink = document.getElementById(\"pmCloseLink\");\n"
                + "}\n"
                + "(function() {\n"
                + "    pBody.onclick = function() {};\n"
                + "    closeLink.onclick = function(e) {\n"
                + "        pBody.removeChild(htmlNode);\n"
                + "    };\n"
                + "})();"
                + "</script>";
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public HttpResponse captiveResponse(IcapRequest request) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.FORBIDDEN);
        try {
            Random rand = new Random();
            int adNr = rand.nextInt(2);
            File file = new File("/home/eric/ad" + adNr + ".html");
            response.setChunked(false);
            ChannelBuffer content = IcapServerUtil.getFileAsContent(file);
            response.setContent(content);
            return response;






        } catch (Exception ex) {
            Logger.getLogger(IcapServerHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NO_CONTENT);
    }
}
