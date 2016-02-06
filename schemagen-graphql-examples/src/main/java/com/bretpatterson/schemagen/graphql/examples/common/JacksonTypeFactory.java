package com.bretpatterson.schemagen.graphql.examples.common;

import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * A very simple object mapper that uses Jackson Json serialization
 */
public class JacksonTypeFactory implements ITypeFactory {
	ObjectMapper objectMapper;

	public JacksonTypeFactory(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Object convertToType(Type type, Object arg) {
		try {
			// Here we simply use Jackson object mapper to first write the GraphQL generic structures to a string
			// then we read the string back in using jackson telling it to convert it to the specified type.
			return objectMapper.readValue(objectMapper.writeValueAsString(arg), TypeFactory.defaultInstance().constructType(type));
		} catch(IOException ex) {
			return Throwables.propagate(ex);
		}

	}
}
