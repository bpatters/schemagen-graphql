package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLTypeCache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache of GraphQL Types we've discovered during parsing.
 */
public class DefaultGraphQLTypeCache<T> implements IGraphQLTypeCache<T> {
	Map<Type, T> cache = new HashMap<Type, T>();

	@Override
	public boolean containsKey(Type key) {
		return cache.containsKey(key);
	}

	@Override
	public T put(Type type, T value) {
		// we can't cache parameterized types because Generic fields of Generic objects with same Generic variable will have same type,
		// but the variable is under a different context so resolves differently.
		if (!(type instanceof ParameterizedType)) {
			cache.put(type, value);
		}
		return value;
	}

	@Override
	public T get(Type type) {
		return cache.get(type);
	}

	@Override
	public T remove(Type t) {
		return cache.remove(t);
	}
}
