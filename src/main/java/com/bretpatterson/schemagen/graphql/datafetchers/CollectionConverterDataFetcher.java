package com.bretpatterson.schemagen.graphql.datafetchers;

import com.google.common.collect.ImmutableList;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

import java.util.Collection;

/**
 * This converts any Collection object to a ImmutableList of the objects.
 * Additionally this converts a null value to an empty list.
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
