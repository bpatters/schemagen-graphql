package com.bretpatterson.schemagen.graphql.annotations;

import com.bretpatterson.schemagen.graphql.datafetchers.spring.SpringDataFetcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with this use the specified data fetcher for retrieval
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GraphQLSpringELDataFetcher {

	/**
	 * A custom datafetcher you would like to use for this field
	 * @return
	 */
	Class<? extends SpringDataFetcher> dataFetcher() default SpringDataFetcher.class;

	/**
	 * Spring EL expression that is executed as part of this datafetcher
	 * @return
	 */
	String value();
}

