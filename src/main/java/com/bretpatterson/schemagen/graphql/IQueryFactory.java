package com.bretpatterson.schemagen.graphql;

import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * An interface for a Factory that knows how to convert an instance object into a List of queries.
 * {@link com.bretpatterson.schemagen.graphql.impl.DefaultQueryFactory}
 */
public interface IQueryFactory {

	List<GraphQLFieldDefinition> newMethodQueriesForObject(IGraphQLObjectMapper graphQLObjectMapper, Object sourceObject);
}
