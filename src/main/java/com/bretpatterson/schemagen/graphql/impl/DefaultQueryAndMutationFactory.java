package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.IMutationFactory;
import com.bretpatterson.schemagen.graphql.IQueryFactory;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.utils.AnnotationUtils;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Default implementation of the query/mutation factory. Converts a Method signature on an object into a:
 * <OL>
 * <LI>List of GraphQL field definitions</LI>
 * <LI>GraphQLOutput type for the return type of the method.</LI>
 * <LI>Configures the Datafetcher based on the Object Instance, Field Name, Method Signature, and Type Factory</LI>
 * </OL>
 */
public class DefaultQueryAndMutationFactory implements IQueryFactory, IMutationFactory {

	@Override
	public List<GraphQLFieldDefinition> newMethodMutationsForObject(IGraphQLObjectMapper graphQLObjectMapper, Object targetObject) {
		List<GraphQLFieldDefinition> results = Lists.newLinkedList();

		results.addAll(graphQLObjectMapper.getGraphQLFieldDefinitions(
				Optional.of(targetObject),
				targetObject.getClass(),
				targetObject.getClass(),
				Optional.of(AnnotationUtils.getFieldsWithAnnotation(targetObject.getClass(), GraphQLMutation.class)),
				Optional.of(AnnotationUtils.getMethodsWithAnnotation(targetObject.getClass(), GraphQLMutation.class))));

		return results;
	}

	/**
	 * Generates field definitions, with arguments, from all {@link GraphQLQuery} annotated methods on the source object.
	 * 
	 * @param graphQLObjectMapper the current IGraphQLObjectMapper
	 * @param targetObject the target object the DataFetcher will invoke the method on.
	 * @return
	 */
	@Override
	public List<GraphQLFieldDefinition> newMethodQueriesForObject(IGraphQLObjectMapper graphQLObjectMapper, Object targetObject) {
		List<GraphQLFieldDefinition> results = Lists.newLinkedList();

		results.addAll(graphQLObjectMapper.getGraphQLFieldDefinitions(
				Optional.of(targetObject),
				targetObject.getClass(),
				targetObject.getClass(),
				Optional.of(AnnotationUtils.getFieldsWithAnnotation(targetObject.getClass(), GraphQLQuery.class)),
				Optional.of(AnnotationUtils.getMethodsWithAnnotation(targetObject.getClass(), GraphQLQuery.class))));

		return results;
	}

	protected GraphQLOutputType getReturnType(IGraphQLObjectMapper graphQLObjectMapper, Method method) {
		return graphQLObjectMapper.getOutputType(method.getGenericReturnType());
	}

}
