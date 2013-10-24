/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.icap.server;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Gilberto Gaxiola
 */
public class AmazonUtilInjection {

    private static HashMap<Long, String> keywordMap;
    private static final String SOLR_URL = "http://padawan.proximusmobility.net:8080/solr/";
    private static final String RESPONSE_REGEX = "(.*)\"response\":\\{\"numFound\":(.),(.*)\\[(.*)\\](.*)";
    private static final DecimalFormat df = new DecimalFormat("#.##");

    private static String createMessage(double price, String description, String imagePath) {

        String actualPrice = df.format(price);
        double discount = (price - (price * .10f));
        String discountPrice = df.format(discount);
        String result = "<script type='text/javascript'>"
                + "\n"
                + "var pBody = document.body;\n"
                + "var htmlNode = document.createElement(\"div\");\n"
                + "htmlNode.innerHTML = '<div style=\"opacity : 0.7; filter:alpha(opacity=70); position: absolute; top: 0; left: 0; bottom: 0; right: 0; background-color: black; width: 100%; z-index: 9000; height: auto;\"></div>\\n\\\n"
                + "<div style=\"line-height: 1.2; vertical-align:baseline; position: absolute; top: 0; background-color: white; width: 100%; height: auto; margin: 0 auto 0 auto; z-index: 9005; font-family: helvetica, sans-serif; font-size: 16pt; font-weight: normal; font-style: normal;\" id=\"pmbox\"><table style=\"width: 100%; height: 60%; background-color: white; border: 1px solid black; z-index: 9005;\"><tr><td style=\"width: 8%;\"></td><td style=\"width: 30%;\">"
                + "<img src=\"" + imagePath + "\"/>"
                + "<div style=\"font-size: 1.6em; text-align: center;\"><div style=\"opacity: 0.7; filter:alpha(opacity=70); text-decoration: line-through; color: black; display: inline; width: 50%;\">$" + actualPrice + "</div><div style=\"color: green; text-decoration: none; display: inline; width: 50%;\">$" + discountPrice + "</div><div>"
                + description + "<br/>"
                + "</div><div>171 Reviews</div></div></td><td style=\"width: 54%;\"><center><img style=\"position: static; vertical-align: top; max-width: 30%;\" src=\"http://static.mybanktracker.com/bank-news/wp-content/uploads/2010/08/home-depot-logo.jpg\"/><br/><span style=\"font-size: 2.6em;\">Wait just a second...</span><br/><span style=\"font-size: 1.6em;\">We can offer you a 10% discount<br/>if you buy this item in-store today!</span></center></td><td style=\"width: 8%;\"></td></tr><tr><td colspan=\"4\"><a style=\"text-decoration: none;color: white;\" href=\"#\"><div style=\"font-size: 2.5em; font-weight: bold; color: white; padding: 3%; text-decoration: none; text-align: center; -webkit-border-radius: 28px; -moz-border-radius: 28px; behavior: url(/css/border-radius.htc); border-radius: 28px; background: #f26323; width: 80%; margin: 3% auto 3% auto;\">Take me to the offer</div></a></td></tr><tr><td colspan=\"4\"><a style=\"text-decoration: none;color: white;\" href=\"#\"><div id=\"pmCloseLink\" style=\"font-size: 2.5em; font-weight: bold; color: white; padding: 3%; text-decoration: none; text-align: center; -webkit-border-radius: 28px; -moz-border-radius: 28px; behavior: url(/css/border-radius.htc); border-radius: 28px; background-color: grey; width: 80%; margin: 0 auto 3% auto;\">I don\\'t want the offer</div></a></td></tr></table></div>';\n"
                + "pBody.appendChild(htmlNode);\n"
                + "var closeLink = document.getElementById(\"pmCloseLink\");\n"
                + "(function() {\n"
                + "    pBody.onclick = function() {};\n"
                + "    closeLink.onclick = function(e) {\n"
                + "        pBody.removeChild(htmlNode);\n"
                + "    };\n"
                + "})();"
                + "</script>";

        return result;
    }

    public static void createOfferMaps() {
        keywordMap = new HashMap<Long, String>();
        keywordMap.put(1L, createMessage(59.50, "Makita 18v cordless drill", "http://www.homedepot.com/catalog/productImages/300/35/35b16e5b-d3da-4879-a6aa-cacaab958ae4_300.jpg"));
        keywordMap.put(2L, createMessage(17.25, "Werner 4ft. Fiberglass Step Ladder with 250lb. Load Capacity", "http://www.homedepot.com/catalog/productImages/300/b5/b5228156-21d6-4d9f-80d7-ef354c3c346b_300.jpg"));
        keywordMap.put(3L, createMessage(28.98, "BERH 1-Gal. Redwood Semi-Transparent Wood Stain", "http://www.homedepot.com/catalog/productImages/300/dd/dde56c3a-ed3c-451e-8a69-2492ac3e1d1c_300.jpg"));


    }

    private static String createSolrQuery(String[] keywords) throws UnsupportedEncodingException {
        String params = "";
        for (int i = 0; i < keywords.length - 1; i++) {
            params += URLEncoder.encode(keywords[i], "UTF-8") + URLEncoder.encode(",", "UTF-8");
        }
        params += URLEncoder.encode(keywords[keywords.length - 1], "UTF-8");
        String result = "select?q=" + params + "&defType=edismax&qf=item_brand^20+item_name^25+item_desc^10+item_price^0.3&wt=json&indent=true";

        return result;

    }

    public static String[] getMobileAmazonParameters(String url) throws UnsupportedEncodingException {
        String encodedUrl = URLEncoder.encode(url, "UTF-8");
        String amp = URLEncoder.encode("&", "UTF-8");
        String plus = URLEncoder.encode("+", "UTF-8");
        String[] result = null;
        String regex = ".*k" + URLEncoder.encode("=", "UTF-8") + "(.*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(encodedUrl);
        if (matcher.matches()) {
            String match = matcher.group(1);
            if (matcher.group(1).contains(amp)) {
                match = match.substring(0, match.indexOf(amp));
            }
            String[] split = match.split(plus);
            if (split.length >= 1) {
                result = split;
            }
        }
        return result;

    }

    public static String[] getAmazonParameters(String url) throws UnsupportedEncodingException {
        String encodedUrl = URLEncoder.encode(url, "UTF-8");
        String amp = URLEncoder.encode("&", "UTF-8");
        String plus = URLEncoder.encode("+", "UTF-8");
        String[] result = null;
        String regex = ".*field-keywords" + URLEncoder.encode("=", "UTF-8") + "(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(encodedUrl);
        if (matcher.matches()) {
            String match = matcher.group(1);
            if (matcher.group(1).contains(amp)) {
                match = match.substring(0, match.indexOf(amp));
            }
            String[] split = match.split(plus);
            if (split.length >= 1) {
                result = split;
            }
        }
        return getMobileAmazonParameters(url);

    }

    public static String adInjectionForAmazonUrl(String amazonUrl) throws Exception {
        if (keywordMap == null) {
            System.out.println("Keyword map is not initalized with offers");
            return null;
        }
        String result = null;
        String[] params = AmazonUtilInjection.getAmazonParameters(amazonUrl);
        if (params != null) {
            String url = SOLR_URL + AmazonUtilInjection.createSolrQuery(params);
            //System.out.println(url);
            URL myURL = new URL(url);
            URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            StringWriter writer = new StringWriter();
            IOUtils.copy(myURL.openStream(), writer, "UTF-8");
            String response = writer.toString();
            Pattern pattern = Pattern.compile(RESPONSE_REGEX, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);

            if (matcher.matches()) {
                int matchesFound = Integer.valueOf(matcher.group(2).trim());
                if (matchesFound > 0) {
                    String subResults = matcher.group(4).trim().replaceAll("\\s+", " ");
                    String[] lists = subResults.split("\\}.*?\\{");
                    if (lists.length >= 1) {
                        //Getting first result;
                        String temp = lists[0];
                        temp = temp.replaceAll("\\{", "").trim().replaceAll("\\}", "");
                        Item item = new Item(temp);
                        result = "Found a Match getting Offer from Map (if this shows in the AD then we couldn't find a match)";
                        if (keywordMap.containsKey(item.getId())) {
                            result = keywordMap.get(item.getId());
                            return result;
                        }
                    }
                } else {
                    System.out.println("No matches");
                }
            } else {
                System.out.println("Error Parsing SolrResponse");
            }





        }
        return result;
    }

    public static void main(String[] args) {
        try {

            AmazonUtilInjection.createOfferMaps();
            String message = AmazonUtilInjection.adInjectionForAmazonUrl("http://www.amazon.com/s/ref=nb_sb_noss_1?url=search-alias%3Ddigital-text&field-keywords=werner+drill+makita+ladder+aluminum");
            System.out.println("Message is: " + message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
