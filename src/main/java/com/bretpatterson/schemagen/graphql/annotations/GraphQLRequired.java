package com.bretpatterson.schemagen.graphql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate that a field of an input object is required
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GraphQLRequired {

	/**
	 * The name of the object or field
	 * @return
	 */
	boolean value();
}
