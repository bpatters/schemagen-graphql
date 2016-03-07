package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IDataFetcherFactory;
import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import graphql.schema.DataFetcher;
import graphql.schema.PropertyDataFetcher;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

/**
 * The Default DataFetcher Factory. This only supports custom datafetchers of type IMethodDataFetcher
 */
public class DefaultDataFetcherFactory implements IDataFetcherFactory {

	@Override
	public DataFetcher newFieldDataFetcher(final IGraphQLObjectMapper objectMapper, final Optional<Object> targetObject, final Field field, String fieldName, Class<? extends DataFetcher> dataFetcher) {
		if (dataFetcher != null) {
			try {
				return dataFetcher.newInstance();
			} catch (IllegalAccessException | InstantiationException ex) {
				throw Throwables.propagate(ex);
			}
		} else {
			return new PropertyDataFetcher(fieldName);
		}
	}

	@Override
	public DataFetcher newMethodDataFetcher(final IGraphQLObjectMapper graphQLObjectMapper,
			final Optional<Object> targetObject,
			final Method method,
			final String fieldName,
			final Class<? extends DataFetcher> dataFetcher) {
		DataFetcher dataFetcherObject;
		try {
			dataFetcherObject = dataFetcher.newInstance();
			if (!IMethodDataFetcher.class.isAssignableFrom(dataFetcher)) {
				throw new InvalidParameterException("This Data Fetcher Factory only supports IMethodDataFetchers");
			}
			IMethodDataFetcher methodDataFetcher = (IMethodDataFetcher) dataFetcherObject;
			methodDataFetcher.setFieldName(fieldName);
			methodDataFetcher.setTypeFactory(graphQLObjectMapper.getTypeFactory());
			if (targetObject.isPresent()) {
				methodDataFetcher.setTargetObject(targetObject.get());
			}
			methodDataFetcher.setMethod(method);
		} catch (Exception ex) {
			throw Throwables.propagate(ex);
		}
		return dataFetcherObject;
	}
}
