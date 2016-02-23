package com.bretpatterson.schemagen.graphql.annotations;

import com.bretpatterson.schemagen.graphql.IMutationFactory;
import com.bretpatterson.schemagen.graphql.IQueryFactory;
import com.bretpatterson.schemagen.graphql.impl.DefaultQueryAndMutationFactory;
import com.bretpatterson.schemagen.graphql.utils.AnnotationUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with this method are scanned for methods defined as queryable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GraphQLController {


	/**
	 * When using relay this must be set, otherwise it's an optional object name to wrapper
	 * this controllers top level queries within.
	 * @return
	 */
	String rootQueriesObjectName() default AnnotationUtils.DEFAULT_NULL;

	/**
	 * When using relay this must be set, otherwise it's an optional object name to wrapper
	 * this controllers top level mutations within.
	 * @return
	 */
	String rootMutationsObjectName() default AnnotationUtils.DEFAULT_NULL;

	/**
	 *
	 * This factory that will be used to generate queries for this object, if any.
	 * Default factory scans the object for {@link GraphQLQuery} annotated methods and turns
	 * the methods into queries.
	 * @return
	 */
	Class<? extends IQueryFactory> queryFactory() default DefaultQueryAndMutationFactory.class;

	/**
	 * This factory that will be used to generate mutations for this object, if any.
	 * Default factory scans the object for {@link GraphQLMutation} annotated methods and turns
	 * the methods into mutations.
	 * @return
	 */
	Class<? extends IMutationFactory> mutationFactory() default DefaultQueryAndMutationFactory.class;
}
