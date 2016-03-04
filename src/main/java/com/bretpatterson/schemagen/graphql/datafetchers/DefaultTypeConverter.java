package com.bretpatterson.schemagen.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * Created by bpatterson on 3/3/16.
 */
public abstract class DefaultTypeConverter implements DataFetcher {
	private DataFetcher dataFetcher;

	public DefaultTypeConverter(DataFetcher dataFetcher) {
		this.dataFetcher = dataFetcher;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		return convert(dataFetcher.get(environment));
	}

	public Object convert(Object rv) {
		return rv;
	}
}
