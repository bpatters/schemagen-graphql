package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.IMutationFactory;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.datafetchers.IMethodDataFetcher;
import com.bretpatterson.schemagen.graphql.utils.AnnotationUtils;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Default implementation of the mutation factory. Generates a mutation method using the specified data fetcher.
 */
public class DefaultMutationFactory implements IMutationFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMutationFactory.class);

	protected <T> T findAnnotation(Annotation[] annotations, Class<T> type) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == type) {
				return (T) annotation;
			}
		}
		return null;
	}

	/**
	 * Generates field definitions, with arguments, from all {@link GraphQLQuery} annotated methods
	 * on the source object.
	 * @param graphQLObjectMapper the current IGraphQLObjectMapper
	 * @param targetObject the target object the DataFetcher will invoke the method on.
	 * @return
	 */
	public List<GraphQLFieldDefinition> newMethodMutationsForObject(IGraphQLObjectMapper graphQLObjectMapper, Object targetObject) {

		ImmutableList.Builder<GraphQLFieldDefinition> mutations = ImmutableList.builder();

		for (Method method : targetObject.getClass().getDeclaredMethods()) {
			try {
				GraphQLMutation graphQLMutationAnnotation = method.getAnnotation(GraphQLMutation.class);
				if (graphQLMutationAnnotation != null) {
					IMethodDataFetcher dataFetcher = graphQLMutationAnnotation.dataFetcher().newInstance();
					dataFetcher.setObjectMapper(graphQLObjectMapper.getObjectMapper());
					dataFetcher.setTargetObject(targetObject);
					dataFetcher.setMethod(method);
					dataFetcher.setFieldName(graphQLMutationAnnotation.name());

					GraphQLFieldDefinition.Builder newField = GraphQLFieldDefinition.newFieldDefinition()
							.name(graphQLMutationAnnotation.name())
							.type(getReturnType(graphQLObjectMapper, method));
					newField.dataFetcher(dataFetcher);
					List<GraphQLArgument> arguments = getMethodArguments(graphQLObjectMapper, Optional.of(dataFetcher), method);

					newField.argument(arguments);

					mutations.add(newField.build());
				}
			} catch (Exception ex) {
				Throwables.propagate(ex);
			}
		}

		return mutations.build();
	}

	protected GraphQLOutputType getReturnType(IGraphQLObjectMapper graphQLObjectMapper, Method method) {
		return graphQLObjectMapper.getOutputType(method.getGenericReturnType());
	}

	/**
	 * Generates a list of GraphQLArgument objects for the specified method while passing parameter information to the datafetcher for use
	 * during fetching.
	 * 
	 * @param graphQLObjectMapper the grpahQLObject mapper
	 * @param dataFetcher The Data fetcher that will be used for this method.
	 * @param method
	 * @return
	 * @throws Exception
	 */
	protected List<GraphQLArgument> getMethodArguments(IGraphQLObjectMapper graphQLObjectMapper, Optional<IMethodDataFetcher> dataFetcher, Method method)
			throws Exception {

		Type returnType = method.getGenericReturnType();

		ImmutableList.Builder<GraphQLArgument> argumentBuilder = ImmutableList.builder();
		GraphQLArgument.Builder paramBuilder;
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		int index = 0;
		for (Type paramType : method.getGenericParameterTypes()) {
			GraphQLParam graphQLParam = findAnnotation(parameterAnnotations[index], GraphQLParam.class);

			if (graphQLParam == null) {
				LOGGER.error("Missing @GraphParam annotation on parameter index {} for method {}", index, method.getName());
				continue;
			}

			paramBuilder = GraphQLArgument.newArgument().name(graphQLParam.name()).type(graphQLObjectMapper.getInputType(paramType));
			if (dataFetcher.isPresent()) {
				dataFetcher.get().addParam(graphQLParam.name(),
						paramType,
						Optional.<Object> fromNullable(AnnotationUtils.isNullValue(graphQLParam.defaultValue()) ? null : graphQLParam.defaultValue()));
			}

			argumentBuilder.add(paramBuilder.build());
			index++;
		}

		return argumentBuilder.build();
	}

}
