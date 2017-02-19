package com.bretpatterson.schemagen.graphql;

import java.lang.reflect.Type;

/**
 * Interface for implementing custom type naming strategies for schema generation.
 */
public interface ITypeNamingStrategy {

	/**
	 * Get the GraphQL type name for the specified type
	 * @param graphQLObjectMapper
	 * @param type
	 * @return
	 */
	String getTypeName(IGraphQLObjectMapper graphQLObjectMapper, Type type);

	/**
	 * String to append to GraphQL InputType's
	 * @return
	 */
	String getInputTypePostfix();

	/**
	 * Delimiter used for separating sections of a type name
	 * @return
	 */
	String getDelimiter();
}
