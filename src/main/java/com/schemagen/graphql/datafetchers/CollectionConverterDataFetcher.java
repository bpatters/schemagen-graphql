package com.schemagen.graphql.datafetchers;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

/**
 * Created by bpatterson on 1/21/16.
 */
public class CollectionConverterDataFetcher extends PropertyDataFetcher {

	public CollectionConverterDataFetcher(String propertyName) {
		super(propertyName);
	}
	@Override
	public Object get(DataFetchingEnvironment environment) {
		Collection rv = (Collection)super.get(environment);

		return ImmutableList.copyOf(rv);
	}
}
