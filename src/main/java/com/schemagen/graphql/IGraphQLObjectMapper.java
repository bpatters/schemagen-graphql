package com.schemagen.graphql;

import com.schemagen.graphql.datafetchers.IObjectMapper;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;

/**
 * A GraphQLObjectMapper that knows how to build a GraphQLDefinition for objects.
 */
public interface IGraphQLObjectMapper {

	/**
	 * Get an input definition for the specified type.
	 * @param type
	 */
	GraphQLInputType getInputType(Type type);

	/**
	 * Get an output definition for the specified type.
	 * @param type
	 */
	GraphQLOutputType getOutputType(Type type);

	/**
	 * Get the Input Type cache. This is useful for custom type mappers who
	 * might need to add TypeReference's to the cache when they need to process
	 * an object that contains a two way dependency between itself and another object.
	 */
	IGraphQLTypeCache<GraphQLInputType> getInputTypeCache();

	/**
	 * Get the Input Type cache. This is useful for custom type mappers who
	 * might need to add TypeReference's to the cache when they need to process
	 * an object that contains a two way dependency between itself and another object.
	 */
	IGraphQLTypeCache<GraphQLOutputType> getOutputTypeCache();

	/**
	 * Returns the object responsible for object type conversion.
	 * @return
	 */
	IObjectMapper getObjectMapper();
}
