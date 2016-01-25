package com.bretpatterson.schemagen.graphql.datafetchers;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bpatterson on 1/19/16.
 */
public class MethodDataFetcher implements IMethodDataFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodDataFetcher.class);
	private ITypeFactory objectMapper;

	private Method method;
	private String fieldName;
	private Object targetObject;
	private LinkedHashMap<String, Type> parameters = new LinkedHashMap<>();
	private Map<String, Object> parameterDefaultValue = Maps.newHashMap();

	@Override
	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
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
			return objectMapper.convertToType(parameters.get(name), parameterDefaultValue.get(name));
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

						arg = objectMapper.convertToType(paramType, arg);

						arguments[index] = arg == null ? getDefaultValue(param)
								: objectMapper.convertToType(
										(Class) (paramType instanceof ParameterizedType ? ((ParameterizedType) paramType).getRawType() : paramType), arg);
						index++;
					}
					return method.invoke(targetObject, (Object[]) arguments);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Unexpected error.", ex);
			Throwables.propagate(ex);
		}
		return null;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public void setObjectMapper(ITypeFactory objectMapper) {
		this.objectMapper = objectMapper;
	}
}
