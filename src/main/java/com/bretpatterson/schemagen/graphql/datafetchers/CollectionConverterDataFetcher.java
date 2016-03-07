package com.bretpatterson.schemagen.graphql.datafetchers;

import com.google.common.collect.ImmutableList;

import graphql.schema.DataFetcher;

import java.util.Collection;

/**
 * This converts any Collection object to a ImmutableList of the objects. Additionally this converts a null value to an empty list.
 */
public class CollectionConverterDataFetcher extends DefaultTypeConverter {

	public CollectionConverterDataFetcher(DataFetcher dataFetcher) {
		super(dataFetcher);
	}

	@Override
	public Object convert(Object value) {
		Collection<?> rv = (Collection<?>) value;

		if (rv == null) {
			return ImmutableList.of();
		}

		return ImmutableList.copyOf(rv);
	}

}
