package com.bretpatterson.schemagen.graphql.typemappers;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;

/**
 * Used to implement your own customer Object --> GraphQLType.
 */
public interface IGraphQLTypeMapper {

	/**
	 * Only used when you are seeking to handle all types that implement a specific interface.
	 * For example if you want to write a CollectionTypeMapper you would implement this method
	 * and then register your implementation as an interface based type mapper. These are more expensive
	 * to  process because we  don't map from type-->mapper but instead search for the first registered
	 * interface mapper that handles the specified type.
	 *
	 * @param graphQLObjectMapper
	 * @param type
	 * @return
	 */
	boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type);

	/**
	 * Convert the specified Type to to a GraphQLOutputType.
	 * @param graphQLObjectMapper
	 * @param type
	 * @return
	 */
	GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type);

	GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type);

}
