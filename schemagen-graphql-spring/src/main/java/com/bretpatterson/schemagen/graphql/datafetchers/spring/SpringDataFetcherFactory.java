package com.bretpatterson.schemagen.graphql.datafetchers.spring;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLSpringELDataFetcher;
import com.bretpatterson.schemagen.graphql.impl.DefaultDataFetcherFactory;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Supports methods/fields annotated with the @GraphQLSpringDataFetcher annotation. This allows you to execute Spring EL expressions as part
 * of your datafetching environment.
 */
public class SpringDataFetcherFactory extends DefaultDataFetcherFactory {

	ApplicationContext context;
	SpringBeanELResolver springBeanELResolver;

	public SpringDataFetcherFactory(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public DataFetcher newFieldDataFetcher(final IGraphQLObjectMapper graphQLObjectMapper, final Optional<Object> targetObject, final Field field, final String fieldName, Class<? extends DataFetcher> dataFetcher) {

		SpringDataFetcher dataFetcherObject = null;
		try {
			GraphQLSpringELDataFetcher springDataFetcher = field.getAnnotation(GraphQLSpringELDataFetcher.class);
			if (springDataFetcher != null) {
				dataFetcherObject = springDataFetcher.dataFetcher().newInstance();
				dataFetcherObject.setFieldName(field.getName());
				dataFetcherObject.setTypeFactory(graphQLObjectMapper.getTypeFactory());
				dataFetcherObject.setTargetObject(null);
				dataFetcherObject.setMethod(null);
				dataFetcherObject.setExpression(springDataFetcher.value());
				dataFetcherObject.setApplicationContext(context);
				return dataFetcherObject;
			}
		} catch (Exception ex) {
			throw Throwables.propagate(ex);
		}

		return super.newFieldDataFetcher(graphQLObjectMapper, targetObject, field, fieldName, dataFetcher);
	}

	@Override
	public DataFetcher newMethodDataFetcher(final IGraphQLObjectMapper graphQLObjectMapper,
			final Optional<Object> targetObject,
			final Method method,
			final String fieldName,
			final Class<? extends DataFetcher> dataFetcher) {
		checkNotNull(method);
		SpringDataFetcher dataFetcherObject = null;
		try {
			GraphQLSpringELDataFetcher springDataFetcher = method.getAnnotation(GraphQLSpringELDataFetcher.class);
			if (springDataFetcher != null) {
				dataFetcherObject = springDataFetcher.dataFetcher().newInstance();
				dataFetcherObject.setFieldName(fieldName);
				dataFetcherObject.setTypeFactory(graphQLObjectMapper.getTypeFactory());
				dataFetcherObject.setTargetObject(targetObject);
				dataFetcherObject.setMethod(method);
				dataFetcherObject.setExpression(springDataFetcher.value());
				dataFetcherObject.setApplicationContext(context);
				return dataFetcherObject;
			}
		} catch (Exception ex) {
			throw Throwables.propagate(ex);
		}
		return super.newMethodDataFetcher(graphQLObjectMapper, targetObject, method, fieldName, dataFetcher);
	}

}
