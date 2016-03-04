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
public class MapConverterDataFetcher extends DefaultTypeConverter {

	public MapConverterDataFetcher(DataFetcher dataFetcher) {
		super(dataFetcher);
	}

	public Object convert(Object value) {

		if (value == null) {
			return ImmutableList.of();
		}
		Map<Object, Object> valueMap = (Map) value;
		// build an accessible copy of the entries to ensure we can get them via property datafetcher
		ImmutableList.Builder<Map.Entry> valueList = ImmutableList.builder();
		for (final Map.Entry<Object, Object> entry : valueMap.entrySet()) {
			valueList.add(new Entry(entry.getKey(), entry.getValue()));
		}
		return valueList.build();
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
