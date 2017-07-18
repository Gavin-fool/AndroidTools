package com.alier.com.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *@author GuannanYan
 *@version 2011.07.28
 */
//@Target(ElementType.FIELD)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mark {
    String value()default "";
}
