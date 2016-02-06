package com.bretpatterson.schemagen.graphql.datafetchers;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.google.common.base.Optional;
import graphql.schema.DataFetcher;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Implementation of a DataFetcher that contains knowledge of how to call into a method.
 * This includes the Type of all parameters, the Target Object, the field Name and
 * a reference to the ITypeFactory for converting from Generic GraphQL deserialized Types
 * into the parameter types of the method.
 */
public interface IMethodDataFetcher extends DataFetcher {

	/**
	 * Called to let the data fetcher know about the Type factory registered for
	 * converting GraphQL deserialized data types to your Application method specific data types.
	 * {@link ITypeFactory}
	 * @param typeFactory
	 */
	void setTypeFactory(ITypeFactory typeFactory);

	/**
	 * The field name that is used to invoke the query.{@link GraphQLQuery#name()}
	 *
	 * @param fieldName
	 */
	void setFieldName(String fieldName);

	/**
	 * A reference to the method we will be invoking for this data fetcher.
	 * @param method
	 */
	void setMethod(Method method);

	/**
	 * The target object the method will be invoked upon.
	 * @param targetObject
	 */
	void setTargetObject(Object targetObject);

	/**
	 * When parsing the method to build it's signature we will make repeated calls
	 * to this method to add a parameter. The invocation order will be the exact order
	 * of the parameters on the method.
	 * @param name the parameter name
	 * @param type the generic type of the parametterj
	 * @param defaultValue optionally the default value defined
	 */
	void addParam(String name, Type type, Optional<Object> defaultValue);
}
