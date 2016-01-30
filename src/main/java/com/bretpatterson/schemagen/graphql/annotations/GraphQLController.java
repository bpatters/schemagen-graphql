package com.bretpatterson.schemagen.graphql.annotations;

import com.bretpatterson.schemagen.graphql.IMutationFactory;
import com.bretpatterson.schemagen.graphql.IQueryFactory;
import com.bretpatterson.schemagen.graphql.impl.DefaultMutationFactory;
import com.bretpatterson.schemagen.graphql.impl.DefaultQueryFactory;

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
	 *
	 * This factory that will be used to generate queries for this object, if any.
	 * Default factory scans the object for {@link GraphQLQuery} annotated methods and turns
	 * the methods into queries.
	 * @return
	 */
	Class<? extends IQueryFactory> queryFactory() default DefaultQueryFactory.class;

	Class<? extends IMutationFactory> mutationFactory() default DefaultMutationFactory.class;
}
