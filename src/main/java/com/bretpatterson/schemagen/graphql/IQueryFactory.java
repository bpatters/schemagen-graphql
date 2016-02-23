package com.bretpatterson.schemagen.graphql;

import com.bretpatterson.schemagen.graphql.impl.DefaultQueryAndMutationFactory;
import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * An interface for a Factory that knows how to convert an instance object into a List of queries.
 * {@link DefaultQueryAndMutationFactory}
 */
public interface IQueryFactory {

	List<GraphQLFieldDefinition> newMethodQueriesForObject(IGraphQLObjectMapper graphQLObjectMapper, Object sourceObject);
}
