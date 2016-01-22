package com.schemagen.graphql.mappers;

import com.schemagen.graphql.GraphQLObjectMapper;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/19/16.
 */
public interface IGraphQLTypeMapper {

	boolean handlesType(Type type);

	GraphQLOutputType getOutputType(GraphQLObjectMapper objectMapper, Type type);

	GraphQLInputType getInputType(GraphQLObjectMapper objectMapper, Type type);
}
