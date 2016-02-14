package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLTypeCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache of GraphQL Types we've discovered during parsing.
 */
public class DefaultGraphQLTypeCache<T> implements IGraphQLTypeCache<T> {

	Map<String, T> nameCache = new HashMap<String, T>();

	@Override
	public boolean containsKey(String key) {
		return nameCache.containsKey(key);
	}

	@Override
	public T put(String name, T value) {
		nameCache.put(name, value);
		return value;
	}

	@Override
	public T get(String name) {
		return nameCache.get(name);
	}

	@Override
	public T remove(String t) {
		return nameCache.remove(t);
	}
}
