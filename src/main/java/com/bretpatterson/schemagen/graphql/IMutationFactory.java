package com.bretpatterson.schemagen.graphql;

import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * An interface for a Factory that knows how to convert an instance object into a List of mutations.
 * {@link com.bretpatterson.schemagen.graphql.impl.DefaultQueryAndMutationFactory}
 */
public interface IMutationFactory {

	List<GraphQLFieldDefinition> newMethodMutationsForObject(IGraphQLObjectMapper graphQLObjectMapper, Object sourceObject);
}
