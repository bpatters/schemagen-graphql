package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.ITypeFactory;

import java.lang.reflect.Type;

/**
 * Created by bpatterson on 3/1/16.
 */
public class SimpleTypeFactory implements ITypeFactory {

	@Override
	public Object convertToType(final Type type, final Object arg) {
		return arg;
	}
}
