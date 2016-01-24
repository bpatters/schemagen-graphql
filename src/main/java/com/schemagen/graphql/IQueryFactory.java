package com.schemagen.graphql;

import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * Created by bpatterson on 1/23/16.
 */
public interface IQueryFactory {

	List<GraphQLFieldDefinition> newMethodQueriesForObject(IGraphQLObjectMapper graphQLObjectMapper, Object sourceObject);
}
