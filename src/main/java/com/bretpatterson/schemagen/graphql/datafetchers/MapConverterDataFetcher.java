package com.bretpatterson.schemagen.graphql.datafetchers;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

/**
 * This converts all Maps into a List of Entries that can be fetched
 */
public class MapConverterDataFetcher extends PropertyDataFetcher {

	public MapConverterDataFetcher(String propertyName) {
		super(propertyName);
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		Map rv = (Map) super.get(environment);

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
