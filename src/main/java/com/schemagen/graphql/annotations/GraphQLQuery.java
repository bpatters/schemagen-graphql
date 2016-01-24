package com.schemagen.graphql.annotations;

import com.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.schemagen.graphql.datafetchers.MethodDataFetcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this method will be processed as field queries and be
 * configured with the specified datafetcher or {@link com.schemagen.graphql.datafetchers.MethodDataFetcher} by default.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GraphQLQuery {
	String name();
	Class<? extends IMethodDataFetcher> dataFetcher() default MethodDataFetcher.class;
}
