package com.schemagen.graphql.datafetchers;

import com.google.common.collect.ImmutableList;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

import java.util.Collection;

/**
 * GraphQL only understands generic Collections and thus attempts to return a field
 * HashSet
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
