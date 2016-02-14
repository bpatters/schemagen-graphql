package com.bretpatterson.schemagen.graphql;

/**
 * A simple abstraction around a key/value cache
 */
public interface IGraphQLTypeCache<T> {

	boolean containsKey(String key);

	T put(String typeName, T value);

	T get(String typeName);

	T remove(String typeName);
}
