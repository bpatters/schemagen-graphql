package com.schemagen.graphql.annotations;

import com.schemagen.graphql.datafetchers.IMethodDataFetcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by bpatterson on 1/18/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GraphQuery {
	String name();
	Class<? extends IMethodDataFetcher> dataFetcher();
}
