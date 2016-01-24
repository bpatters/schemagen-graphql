package com.schemagen.graphql.examples.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Throwables;
import com.schemagen.graphql.datafetchers.IObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * A very simple object mapper that uses Jackson JSon serialization
 */
public class JacksonObjectMapper implements IObjectMapper {
	ObjectMapper objectMapper;

	public JacksonObjectMapper(ObjectMapper objectMapper) {
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
