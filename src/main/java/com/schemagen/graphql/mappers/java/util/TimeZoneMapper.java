package com.schemagen.graphql.mappers.java.util;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.util.TimeZone;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type=TimeZone.class)
public class TimeZoneMapper implements IGraphQLTypeMapper {

	public boolean handlesType(Type type) {
		return type == TimeZone.class;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

}
