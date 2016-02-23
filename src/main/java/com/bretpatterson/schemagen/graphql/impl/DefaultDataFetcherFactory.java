package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.datafetchers.IDataFetcher;
import com.bretpatterson.schemagen.graphql.IDataFetcherFactory;
import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLDataFetcher;
import com.bretpatterson.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

/**
 * The Default DataFetcher Factory. This only supports custom datafetchers of type IMethodDataFetcher
 */
public class DefaultDataFetcherFactory implements IDataFetcherFactory {
	@Override
	public IDataFetcher newFieldDataFetcher(final IGraphQLObjectMapper objectMapper, final Field field, final GraphQLDataFetcher dataFetcher) {
		return null;
	}

	@Override
	public IDataFetcher newMethodDataFetcher(final IGraphQLObjectMapper graphQLObjectMapper, final Object targetObject, final Method method, final String fieldName, final Class dataFetcher) {
		IDataFetcher dataFetcherObject;
		try {
			dataFetcherObject = (IDataFetcher) dataFetcher.newInstance();
			if (!IMethodDataFetcher.class.isAssignableFrom(dataFetcher)) {
				throw new InvalidParameterException("This Data Fetcher Factory only supports IMethodDataFetchers");
			}
			IMethodDataFetcher methodDataFetcher = (IMethodDataFetcher) dataFetcherObject;
			methodDataFetcher.setFieldName(fieldName);
			methodDataFetcher.setTypeFactory(graphQLObjectMapper.getTypeFactory());
			methodDataFetcher.setTargetObject(targetObject);
			methodDataFetcher.setMethod(method);
		} catch (Exception ex) {
			throw Throwables.propagate(ex);
		}
		return dataFetcherObject;
	}
}
