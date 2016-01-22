package com.schemagen.graphql.mappers.java.util;

import com.schemagen.graphql.GraphQLObjectMapper;
import com.schemagen.graphql.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type= Date.class)
public class DateMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		return type == Date.class;
	}

	@Override
	public GraphQLOutputType getOutputType(GraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLInt;
	}

	@Override
	public GraphQLInputType getInputType(GraphQLObjectMapper objectMapper,  Type type) {
		return Scalars.GraphQLInt;
	}

}
