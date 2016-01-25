package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLTypeCache;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLTypeCache<T> implements IGraphQLTypeCache<T> {
	Map<Type, T> cache = new HashMap<Type, T>();

	@Override
	public boolean containsKey(Type key) {
		return cache.containsKey(key);
	}

	@Override
	public T put(Type type, T value) {
		return cache.put(type, value);
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
