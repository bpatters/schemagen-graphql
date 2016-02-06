package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generates type names using the full package name with . replaced with _
 */
public class FullTypeNamingStrategy implements ITypeNamingStrategy {

	public String getTypeName(Type type) {
		Class theClass = (Class) ((type instanceof ParameterizedType) ? ((ParameterizedType) type).getRawType().getClass() :  type);

		return String.format("%s_%s",theClass.getPackage().getName().replace(".","_"), theClass.getSimpleName());
	}
}
