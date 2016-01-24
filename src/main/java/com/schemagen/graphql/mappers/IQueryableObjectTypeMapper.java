package com.schemagen.graphql.mappers;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLQueryable;
import graphql.schema.GraphQLFieldDefinition;

import java.util.List;

/**
 * Created by bpatterson on 1/23/16.
 */
public interface IQueryableObjectTypeMapper {

	List<GraphQLFieldDefinition> generateQueriesFromObject(IGraphQLObjectMapper graphQLObjectMapper, GraphQLQueryable graphQLQueryable, Object sourceObject);
}
