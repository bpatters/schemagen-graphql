package com.bretpatterson.schemagen.graphql;

import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/22/16.
 */
public interface ITypeFactory {
	/**
	 * This object needs to know how to convert objects to the specified type. This is used
	 * to convert query Parameters to the correct type before method invocation.
	 * Object type of the arg can be any object type within java we exposed through GraphQL as a parameter.
	 * IE everyone of your query/mutation parameter objects must be handled by this object.
	 * Jackson object mapper is a simple way of doing this. IE:
	 * <pre>
	 * {@code
	 * {@liter @Override}
	 * public Object convertToType(Type type, Object arg) {
	 *		try {
	 *			return objectMapper.readValue(objectMapper.writeValueAsString(arg), TypeFactory.defaultInstance().constructType(type));
	 *		} catch(IOException ex) {
	 *			return Throwables.propagate(ex);
	 *		}
	 *
	 *	}
	 * }</pre>
	 *
	 * @param type The type to convert the object to. This is a ParameterizedType if the original value is so you can recursively determine
	 *             what value you should convert to. IE: {@code List<List<Object>>}
	 * @param arg The GraphQL version of the argument value IE: Primitive, Object, List of ...
	 * @return
	 */
	Object convertToType(Type type, Object arg);
}
