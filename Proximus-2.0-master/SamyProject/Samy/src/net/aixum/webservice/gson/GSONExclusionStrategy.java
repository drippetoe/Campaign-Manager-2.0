/*
 * Copyright © 2012 Proximus Mobility LLC
 */
package net.aixum.webservice.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
public class GSONExclusionStrategy implements ExclusionStrategy {

    private final Class<?> typeToSkip;

    public GSONExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(GSONTransient.class) != null;
    }
}