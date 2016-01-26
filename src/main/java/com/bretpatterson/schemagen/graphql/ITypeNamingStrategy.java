package com.bretpatterson.schemagen.graphql;

import java.lang.reflect.Type;

/**
 * Interface for implementing custom type naming strategies for schema generation.
 */
public interface ITypeNamingStrategy {

	/**
	 * Get the GraphQL type name for the specified type
	 * @param type
	 * @return
	 */
	String getTypeName(Type type);
}
