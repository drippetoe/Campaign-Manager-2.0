package com.proximus.util;

/**
 *
 * @author dshaw
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBUtil
{
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(Class<T> objClass, String xmlStr) throws Exception
    {
        if (xmlStr == null || xmlStr.isEmpty()) {
            return objClass.newInstance();
        }
        JAXBContext context = JAXBContext.newInstance(objClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T obj = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
        return obj;
    }

    public static <T> String toXml(Class<T> objClass, T obj) throws Exception
    {
        JAXBContext context = JAXBContext.newInstance(objClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    public static <T> String toXml(Class<T> objClass, T obj, boolean bOmitXmlDeclaration) throws Exception
    {
        JAXBContext context = JAXBContext.newInstance(objClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, bOmitXmlDeclaration);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    public static <T> T loadFromFile(Class<T> objClass, String fileName) throws Exception
    {
        String xml = XMLHelper.loadXmlStringFromFile(fileName);
        T obj = fromXml(objClass, xml);
        return obj;
    }

    public static <T> T loadFromStream(Class<T> objClass, InputStream is, long length) throws Exception
    {
        String xml = XMLHelper.loadXmlStringFromStream(is, length);
        T obj = fromXml(objClass, xml);
        return obj;
    }

    public static <T> void saveToFile(Class<T> objClass, T obj, String fileName) throws Exception
    {
        String xml = toXml(objClass, obj);
        File file = new File(fileName);
        OutputStream os = new FileOutputStream(file);

        byte[] bytes = xml.getBytes("UTF8");
        os.write(bytes);
        os.close();
    }
}
