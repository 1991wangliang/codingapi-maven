package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, TYPE_PARAMETER, TYPE_USE})
@Documented
public @interface GraphRelation {
    String value();
}
