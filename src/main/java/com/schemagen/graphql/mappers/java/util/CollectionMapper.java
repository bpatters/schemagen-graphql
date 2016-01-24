package com.schemagen.graphql.mappers.java.util;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type = Collection.class)
public class CollectionMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		Class typeClass = (Class) (type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type);
		return Collection.class.isAssignableFrom(typeClass);
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return new GraphQLList(graphQLObjectMapper.getOutputType(parameterizedType.getActualTypeArguments()[0]));
		} else {
			return new GraphQLList(graphQLObjectMapper.getOutputType(Object.class));
		}
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return new GraphQLList(graphQLObjectMapper.getOutputType(parameterizedType.getActualTypeArguments()[0]));
		} else {
			return new GraphQLList(graphQLObjectMapper.getOutputType(Object.class));
		}
	}

}
