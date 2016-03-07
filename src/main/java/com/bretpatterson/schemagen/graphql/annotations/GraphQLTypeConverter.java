package com.bretpatterson.schemagen.graphql.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bretpatterson.schemagen.graphql.datafetchers.DefaultTypeConverter;

/**
 * Fields annotated with this use the specified type converter for type conversion
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GraphQLTypeConverter {

	/**
	 * A custom type converter you would like to use for this field.
	 * @return
	 */
	Class<? extends DefaultTypeConverter> typeConverter();
}
