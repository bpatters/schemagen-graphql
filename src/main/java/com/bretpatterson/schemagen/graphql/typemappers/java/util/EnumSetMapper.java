package com.bretpatterson.schemagen.graphql.typemappers.java.util;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type = EnumSet.class)
public class EnumSetMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		Class typeClass = (Class) (type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type);
		return EnumSet.class.isAssignableFrom(typeClass);
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		Class classType = (Class) type;
		Class enumClassType = classType.getComponentType();
		GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(enumClassType.getSimpleName());

		for (Object value : EnumSet.allOf(enumClassType)) {
			enumType.value(value.toString(), value);
		}
		return new GraphQLList(enumType.build());

	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		Class classType = (Class) type;
		Class enumClassType = classType.getComponentType();
		GraphQLEnumType.Builder enumType = GraphQLEnumType.newEnum().name(enumClassType.getSimpleName());

		for (Object value : EnumSet.allOf(enumClassType)) {
			enumType.value(value.toString(), value);
		}
		return new GraphQLList(enumType.build());
	}

}
