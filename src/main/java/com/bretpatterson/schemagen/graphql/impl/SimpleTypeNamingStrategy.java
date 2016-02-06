package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type naming strategy that uses the simple class name as the GraphQL type.
 */
public class SimpleTypeNamingStrategy implements ITypeNamingStrategy {

	public String getTypeName(Type type) {
		Class theClass = (Class) ((type instanceof ParameterizedType) ? ((ParameterizedType) type).getRawType().getClass() :  type);
		GraphQLTypeName typeName = (GraphQLTypeName) theClass.getAnnotation(GraphQLTypeName.class);
		if (typeName != null) {
			return typeName.name();
		}

		return theClass.getSimpleName();
	}
}
