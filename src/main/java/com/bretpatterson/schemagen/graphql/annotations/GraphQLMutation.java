package com.bretpatterson.schemagen.graphql.annotations;

import com.bretpatterson.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.MethodDataFetcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this method will be processed as field mutations and can be
 * configured with the specified datafetcher or {@link MethodDataFetcher} by default.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GraphQLMutation {
	String name();
	Class<? extends IMethodDataFetcher> dataFetcher() default MethodDataFetcher.class;
}
