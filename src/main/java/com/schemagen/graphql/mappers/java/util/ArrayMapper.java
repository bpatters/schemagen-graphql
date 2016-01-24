package com.schemagen.graphql.mappers.java.util;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type = Array.class)
public class ArrayMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		Class typeClass = (Class) (type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type);
		return Array.class.isAssignableFrom(typeClass);
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		Class classType = (Class) type;
		return new GraphQLList(graphQLObjectMapper.getOutputType(classType.getComponentType()));
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		Class classType = (Class) type;
		return new GraphQLList(graphQLObjectMapper.getInputType(classType.getComponentType()));
	}

}
