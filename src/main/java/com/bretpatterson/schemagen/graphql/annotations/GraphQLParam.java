package com.bretpatterson.schemagen.graphql.annotations;

import com.bretpatterson.schemagen.graphql.utils.AnnotationUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bpatterson on 1/18/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface GraphQLParam {
	String name();
	String defaultValue() default AnnotationUtils.DEFAULT_NULL;
	boolean required() default false;
}
