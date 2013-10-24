/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook.util;

import java.io.IOException;
import java.io.StringWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author dshaw
 */
public class JSONFormatter {

    public static ObjectMapper getObjectMapper()
    {
        ObjectMapper m = new ObjectMapper();
        m.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        //m.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

        return m;
    }

    public static String toJson(Object pojo, boolean prettyPrint)
    throws JsonMappingException, JsonGenerationException, IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper m = getObjectMapper();

        JsonFactory jf = new JsonFactory();
        JsonGenerator jg = jf.createJsonGenerator(sw);
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        m.writeValue(jg, pojo);
        return sw.toString();
    }
}
