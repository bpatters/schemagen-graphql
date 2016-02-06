package com.bretpatterson.schemagen.graphql;

import java.lang.reflect.Type;

/**
 * A simple abstraction around a key/value cache
 */
public interface IGraphQLTypeCache<T> {
	boolean containsKey(Type key);

	T put(Type type, T value);

	T get(Type type);

	T remove(Type t);
}
