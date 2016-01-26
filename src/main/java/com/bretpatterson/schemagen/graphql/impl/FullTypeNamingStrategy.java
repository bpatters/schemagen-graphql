package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generates type names using the full package name
 */
public class FullTypeNamingStrategy implements ITypeNamingStrategy {

	public String getTypeName(Type type) {
		Class theClass = (Class) ((type instanceof ParameterizedType) ? ((ParameterizedType) type).getRawType().getClass() :  type);

		return theClass.getPackage().getName() + "." + theClass.getSimpleName();
	}
}
