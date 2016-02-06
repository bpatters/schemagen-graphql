package com.bretpatterson.schemagen.graphql.datafetchers;

import com.google.common.collect.ImmutableList;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

import java.util.Collection;

/**
 * Add this to all Collection fields that can return null in your model. This will
 * convert a null field into an empty List.
 */
public class CollectionConverterDataFetcher extends PropertyDataFetcher {

	public CollectionConverterDataFetcher(String propertyName) {
		super(propertyName);
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		Collection rv = (Collection)super.get(environment);

		if (rv == null)  {
			return ImmutableList.of();
		}

		return ImmutableList.copyOf(rv);
	}
}
