package com.bretpatterson.schemagen.graphql.typemappers.com.google.base;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.base.Optional;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/21/16.
 */
@GraphQLTypeMapper(type= Optional.class)
public class OptionalMapper implements IGraphQLTypeMapper {
	@Override
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		if (type instanceof  ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			return parameterizedType.getRawType() == Optional.class;
		}

		return false;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper objectMapper, Type type) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		return objectMapper.getOutputType(parameterizedType.getActualTypeArguments()[0]);
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper objectMapper, Type type) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		return objectMapper.getInputType(parameterizedType.getActualTypeArguments()[0]);
	}
}
