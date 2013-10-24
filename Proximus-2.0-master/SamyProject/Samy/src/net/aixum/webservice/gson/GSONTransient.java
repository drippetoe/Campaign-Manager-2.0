/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.aixum.webservice.gson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GSONTransient {
    // Field tag only annotation
}
