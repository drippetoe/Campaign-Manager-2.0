/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 *
 * @author dshaw
 */

public class PassbookDateJSONSerializer extends JsonSerializer<Date>{

    private static final String format = "yyyy-MM-dd'T'HH:mm-05:00"; //     private static final String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";


    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String formattedDate = dateFormat.format(date);
        gen.writeString(formattedDate);
    }

    public static void main(String[] args) {
        Date now = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        System.out.println(dateFormat.format(now));
    }
}
