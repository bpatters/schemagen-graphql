package com.schemagen.graphql.datafetchers;

import com.google.common.base.Optional;
import graphql.schema.DataFetcher;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/19/16.
 */
public interface IMethodDataFetcher extends DataFetcher {

	void setFieldName(String fieldName);
	String getFieldName();

	void setMethod(Method method);
	Method getMethod();

	void setSourceObject(Object sourceObject);
	Object getSourceObject();

	void addParam(String name, Type type, Optional<Object> defaultValue);
}
