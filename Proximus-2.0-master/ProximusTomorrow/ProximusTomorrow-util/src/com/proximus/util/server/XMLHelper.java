package com.proximus.util.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class XMLHelper
{
    private static final Logger logger = Logger.getLogger(XMLHelper.class.getName());
    private static String prettyXsl = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"	xmlns:xalan=\"http://xml.apache.org/xslt\"> "
            + "<xsl:output method=\"xml\" encoding=\"UTF-8\" indent=\"yes\" xalan:indent-amount=\"4\"/> "
            + "<xsl:strip-space elements=\"*\"/>"
            + "<xsl:template match=\"@*|node()\">"
            + "<xsl:copy>"
            + "<xsl:apply-templates select=\"@*|node()\"/>"
            + "</xsl:copy>"
            + "</xsl:template>"
            + "</xsl:stylesheet>";

    public static String prettyPrint(String inputXml)
    {
        String prettyXml = inputXml;
        try {
            StreamSource xformsource = new StreamSource(new StringReader(prettyXsl));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xformsource);
            StreamSource source = new StreamSource(new StringReader(inputXml));
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            prettyXml = result.getWriter().toString();
        } catch (Exception e) {
            logger.warn("XMLHelper.prettyPrint - " + e.toString());

        }
        return prettyXml;
    }

    public static String readFileContents(String filename) throws IOException
    {
        DataInputStream in = new DataInputStream(new FileInputStream(filename));
        byte[] b = new byte[in.available()];
        in.readFully(b);
        in.close();
        String fileContents = new String(b, 0, b.length, "UTF-8");
        return fileContents;
    }

    /**
     * Get XML element attribute by name
     * 
     * @param node - The XML element
     * @param name - The name of the attribute
     * @param bRequired - If true, throw an IllegalArgumentException.
     * @param defaultValue - If bRequired is false and the attribute does not
     * 		exist, return this default value instead.
     * @return - String
     */
    public static String getAttribute(Element node, String name, boolean bRequired, String defaultValue)
    {
        String ret = defaultValue;
        if (node.hasAttribute(name)) {
            ret = node.getAttribute(name);
        } else {
            if (bRequired) {
                throw new IllegalArgumentException("Element missing required '" + name + "' attribute.");
            }
        }
        return ret;
    }

    /**
     * Get XML element attribute by name.  Value of "0" returns false, anything
     * else returns true.
     * 
     * @param node - The XML element
     * @param name - The name of the attribute
     * @param bRequired - If true, throw an IllegalArgumentException.
     * @param bDefaultValue - If bRequired is false and the attribute does not
     * 		exist, return this default value instead.
     * @return - boolean
     */
    public static boolean getAttributeAsBoolean(Element node, String name, boolean bRequired, boolean bDefaultValue)
    {
        boolean bRet = bDefaultValue;
        if (node.hasAttribute(name)) {
            if (node.getAttribute(name).equals("0")) {
                bRet = false;
            } else {
                bRet = true;
            }
        } else {
            if (bRequired) {
                throw new IllegalArgumentException("Element missing required '" + name + "' attribute.");
            }
        }
        return bRet;
    }

    public static String loadXmlStringFromFile(String fileName) throws IOException
    {
        String xml = null;
        File file = new File(fileName);
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length) && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            return null;
        }

        is.close();
        xml = new String(bytes, "UTF8");
        return xml;
    }

    public static String loadXmlStringFromStream(InputStream is, long length) throws IOException
    {
        String xml = null;
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length) && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            return null;
        }

        is.close();
        xml = new String(bytes, "UTF-8");
        return xml;
    }
}
