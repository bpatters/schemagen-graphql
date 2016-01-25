package com.schemagen.graphql.datafetchers;

import com.google.common.base.Optional;
import graphql.schema.DataFetcher;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/19/16.
 */
public interface IMethodDataFetcher extends DataFetcher {

	void setObjectMapper(ITypeFactory objectMapper);

	void setFieldName(String fieldName);

	void setMethod(Method method);

	void setTargetObject(Object targetObject);

	void addParam(String name, Type type, Optional<Object> defaultValue);
}
