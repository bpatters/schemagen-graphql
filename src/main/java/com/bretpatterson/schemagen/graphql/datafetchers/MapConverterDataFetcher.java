package com.bretpatterson.schemagen.graphql.datafetchers;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

/**
 * This converts all Maps into a List of Entries who's key/values are accessible
 * This gets added to all Map's by default so they can be exposed through GraphQL
 */
public class MapConverterDataFetcher implements IDataFetcher {
	DataFetcher parentDataFetcher;

	public MapConverterDataFetcher(DataFetcher parentDataFetcher) {
		this.parentDataFetcher = parentDataFetcher;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		Map rv = (Map) parentDataFetcher.get(environment);

		if (rv == null) {
			return ImmutableList.of();
		}
		Map<Object, Object> rvMap = (Map) rv;
		// build an accessible copy of the entries to ensure we can get them via property datafetcher
		ImmutableList.Builder<Map.Entry> rvList = ImmutableList.builder();
		for (final Map.Entry<Object, Object> entry : rvMap.entrySet()) {
			rvList.add(new Entry(entry.getKey(), entry.getValue()));
		}
		return rvList.build();
	}

	@Override
	public void addParam(final String name, final Type type, final Optional<Object> defaultValue) {
		if (IDataFetcher.class.isAssignableFrom(parentDataFetcher.getClass())) {
			((IDataFetcher)parentDataFetcher).addParam(name, type, defaultValue);
		}
	}

	/**
	 * This holds a Map.Entry instance that we use to hold the Map.Entry in maps that we have remapped to List<Entry> objects.
	 */
	public class Entry implements Map.Entry {

		Object key;
		Object value;

		public Entry(Object key, Object value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(final Object value) {
			throw new IllegalAccessError("Not implemented");
		}
	}
}
