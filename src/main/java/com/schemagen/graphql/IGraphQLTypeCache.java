package com.schemagen.graphql;

import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/23/16.
 */
public interface IGraphQLTypeCache<T> {
	boolean containsKey(Type key);

	T put(Type type, T value);

	T get(Type type);

	T remove(Type t);
}
