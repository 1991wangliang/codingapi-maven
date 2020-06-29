package com.codingapi.maven.uml.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE,FIELD,METHOD})
@Documented
public @interface GraphRelation {

    String value();

    Class<?> type();

}
