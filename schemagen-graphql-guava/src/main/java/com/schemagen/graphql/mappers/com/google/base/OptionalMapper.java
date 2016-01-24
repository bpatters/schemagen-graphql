package com.schemagen.graphql.mappers.com.google.base;

import com.google.common.base.Optional;
import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
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
	public boolean handlesType(Type type) {
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
