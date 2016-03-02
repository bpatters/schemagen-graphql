package com.bretpatterson.schemagen.graphql.datafetchers;

import com.bretpatterson.schemagen.graphql.ITypeFactory;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import graphql.schema.DataFetchingEnvironment;

import java.lang.reflect.Method;

/**
 * Implementation of a DataFetcher that contains knowledge of how to call into a method.
 * This includes the Type of all parameters, the Target Object, the field Name and
 * a reference to the ITypeFactory for converting from Generic GraphQL deserialized Types
 * into the parameter types of the method.
 */
public interface IMethodDataFetcher extends IDataFetcher {

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
	 * Invokes the method on the target object with the specified arguments
	 * @param method the method to invoke
	 * @param targetObject the target object
	 * @param arguments the arguments to the method
	 * @return return value
	 */
	Object invokeMethod(DataFetchingEnvironment environment, Method method, Object targetObject, Object[] arguments);
}
