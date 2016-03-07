package com.bretpatterson.schemagen.graphql.datafetchers;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import graphql.schema.DataFetcher;

/**
 * This converts all Maps into a List of Entries who's key/values are accessible
 * This gets added to all Map's by default so they can be exposed through GraphQL
 */
public class MapConverterDataFetcher extends DefaultTypeConverter {

	public MapConverterDataFetcher(DataFetcher dataFetcher) {
		super(dataFetcher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object value) {

		if (value == null) {
			return ImmutableList.of();
		}
		Map<Object, Object> valueMap = (Map<Object, Object>) value;
		// build an accessible copy of the entries to ensure we can get them via property datafetcher
		ImmutableList.Builder<Map.Entry<Object, Object>> valueList = ImmutableList.builder();
		for (final Map.Entry<Object, Object> entry : valueMap.entrySet()) {
			valueList.add(new Entry(entry));
		}
		return valueList.build();
	}

	/**
	 * This holds a Map.Entry instance that we use to hold the Map.Entry in maps that we have remapped to List<Entry> objects.
	 */
	public class Entry implements Map.Entry<Object, Object> {

		Object key;
		Object value;

		public Entry(Map.Entry<Object, Object> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
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
