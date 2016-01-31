package com.bretpatterson.schemagen.graphql.typemappers.java.util;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type= Date.class)
public class DateMapper implements IGraphQLTypeMapper {
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return type == Date.class;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLInt;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper objectMapper,  Type type) {
		return Scalars.GraphQLInt;
	}

}
