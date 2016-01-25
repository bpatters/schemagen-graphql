package com.bretpatterson.schemagen.graphql.typemappers.java.util;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.exceptions.NotMappableException;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type = Map.class)
public class MapMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		Class typeClass = (Class) (type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type);
		return Map.class.isAssignableFrom(typeClass);
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class rawClass = (Class)parameterizedType.getRawType();

			Type[] paramTypes = parameterizedType.getActualTypeArguments();
			if (((Class) paramTypes[0]).isEnum()) {
				GraphQLObjectType.Builder glType = GraphQLObjectType.newObject().name(rawClass.getSimpleName());
				Class enumClassType = (Class) paramTypes[0];
				for (Object value : EnumSet.allOf(enumClassType)) {
					glType.field(GraphQLFieldDefinition.newFieldDefinition()
							.name(value.toString())
							.type(graphQLObjectMapper.getOutputType(paramTypes[1]))
							.build());
				}

				return glType.build();
			} else {
				throw new NotMappableException(String.format("%s is not mappable to GraphQL", type.getClass().getName()));
			}
		} else {
			throw new NotMappableException(String.format("%s is not mappable to GraphQL", type.getClass().getName()));
		}
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class rawClass = (Class) parameterizedType.getRawType();

			Type[] paramTypes = parameterizedType.getActualTypeArguments();
			if (((Class) paramTypes[0]).isEnum()) {
				GraphQLInputObjectType.Builder glType = GraphQLInputObjectType.newInputObject().name(rawClass.getSimpleName());
				Class enumClassType = (Class) paramTypes[0];
				for (Object value : EnumSet.allOf(enumClassType)) {
					glType.field(GraphQLInputObjectField.newInputObjectField()
							.name(value.toString())
							.type(graphQLObjectMapper.getInputType(paramTypes[1]))
							.build());
				}

				return glType.build();
			} else {
				throw new NotMappableException(String.format("%s is not mappable to GraphQL", type.getClass().getName()));
			}
		} else {
			throw new NotMappableException(String.format("%s is not mappable to GraphQL", type.getClass().getName()));
		}
	}

}
