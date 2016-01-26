package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/25/16.
 */
public class SimpleTypeNamingStrategy implements ITypeNamingStrategy {

	public String getTypeName(Type type) {
		Class theClass = (Class) ((type instanceof ParameterizedType) ? ((ParameterizedType) type).getRawType().getClass() :  type);

		return theClass.getSimpleName();
	}
}
