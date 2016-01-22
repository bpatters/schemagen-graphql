package com.schemagen.graphql.mappers.com.google.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Optional;
import com.schemagen.graphql.GraphQLObjectMapper;
import com.schemagen.graphql.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

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
	public GraphQLOutputType getOutputType(GraphQLObjectMapper objectMapper, Type type) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		return objectMapper.getObjectType(parameterizedType.getActualTypeArguments()[0]);
	}

	@Override
	public GraphQLInputType getInputType(GraphQLObjectMapper objectMapper, Type type) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		return objectMapper.getInputObjectType(parameterizedType.getActualTypeArguments()[0]);
	}
}
