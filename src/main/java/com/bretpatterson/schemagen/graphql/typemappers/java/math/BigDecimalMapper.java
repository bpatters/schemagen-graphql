package com.bretpatterson.schemagen.graphql.typemappers.java.math;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by bpatterson on 2/3/16.
 */
@GraphQLTypeMapper(type= BigDecimal.class)
public class BigDecimalMapper implements IGraphQLTypeMapper{
	@Override
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return type == BigDecimal.class;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return Scalars.GraphQLFloat;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return Scalars.GraphQLFloat;
	}
}
