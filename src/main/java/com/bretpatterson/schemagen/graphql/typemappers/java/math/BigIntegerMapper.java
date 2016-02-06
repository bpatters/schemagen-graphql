package com.bretpatterson.schemagen.graphql.typemappers.java.math;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * DefaultType Mapper to convert a BigInteger to a GraphQLLong data type.
 */
@GraphQLTypeMapper(type= BigInteger.class)
public class BigIntegerMapper implements IGraphQLTypeMapper{
	@Override
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return type == BigInteger.class;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return Scalars.GraphQLLong;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		return Scalars.GraphQLLong;
	}
}
