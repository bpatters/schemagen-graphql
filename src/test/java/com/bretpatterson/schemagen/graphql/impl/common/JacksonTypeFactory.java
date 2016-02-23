package com.bretpatterson.schemagen.graphql.impl.common;

import com.bretpatterson.schemagen.graphql.ITypeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * A very simple object mapper that uses Jackson JSon serialization
 */
public class JacksonTypeFactory implements ITypeFactory {
	ObjectMapper objectMapper;

	public JacksonTypeFactory(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Object convertToType(Type type, Object arg) {
		try {
			return objectMapper.readValue(objectMapper.writeValueAsString(arg), TypeFactory.defaultInstance().constructType(type));
		} catch(IOException ex) {
			return Throwables.propagate(ex);
		}

	}
}
