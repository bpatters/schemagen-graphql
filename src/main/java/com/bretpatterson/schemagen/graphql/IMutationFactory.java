package com.bretpatterson.schemagen.graphql;

import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * Created by bpatterson on 1/28/16.
 */
public interface IMutationFactory {

	List<GraphQLFieldDefinition> newMethodMutationsForObject(IGraphQLObjectMapper graphQLObjectMapper, Object sourceObject);
}
