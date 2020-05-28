package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, METHOD, TYPE})
@Documented
public @interface Ignore {

}
