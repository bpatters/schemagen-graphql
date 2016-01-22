package com.schemagen.graphql.datafetchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bpatterson on 1/19/16.
 */
public class MethodDataFetcher implements IMethodDataFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodDataFetcher.class);
	ObjectMapper mapper = new ObjectMapper();

	private Method method;
	private String fieldName;
	Object sourceObject;
	LinkedHashMap<String, Type> parameters = new LinkedHashMap<>();
	Map<String, Object> parameterDefaultValue = Maps.newHashMap();

	@Override
	public void setSourceObject(Object sourceObject) {
		this.sourceObject = sourceObject;
	}

	@Override
	public Object getSourceObject() {
		return sourceObject;
	}

	@Override
	public void addParam(String name, Type type, Optional<Object> defaultValue) {
		parameters.put(name, type);
		if (defaultValue.isPresent()) {
			parameterDefaultValue.put(name, defaultValue.get());
		}
	}
	private Object getDefaultValue(String name) throws Exception {
		if (parameterDefaultValue.containsKey(name)) {
			return convertToType(parameters.get(name),parameterDefaultValue.get(name));
		} else {
			return null;
		}
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {

		try {
			for (Field field : environment.getFields()) {
				if (field.getName().equals(fieldName)) {
					Object[] arguments = new Object[parameters.size()];
					int index = 0;
					for (String param : parameters.keySet()) {
						Object arg = environment.getArgument(param);
						Type paramType = parameters.get(param);

						arg = convertToCollection(paramType, arg);

						arguments[index] = arg == null ? getDefaultValue(param) : mapper.readValue(mapper.writeValueAsString(arg), (Class) (paramType instanceof ParameterizedType ? ((ParameterizedType)paramType).getRawType() : paramType) );
						index++;
					}
					return method.invoke(sourceObject, (Object[])arguments);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Unexpected error.", ex);
			Throwables.propagate(ex);
		}
		return null;
	}

	public Object convertToType(Type type, Object value) throws Exception  {
		return mapper.readValue(value.toString(), (Class) (type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type));
	}

	public Object convertToCollection(Type type, Object arg) {
		if (arg != null && Collection.class.isAssignableFrom(arg.getClass())) {

			return ImmutableList.copyOf((Collection)arg);
		}

		return arg;
	}
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
