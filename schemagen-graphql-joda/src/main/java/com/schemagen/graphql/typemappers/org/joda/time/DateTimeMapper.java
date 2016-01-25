package com.schemagen.graphql.typemappers.org.joda.time;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type=DateTime.class)
public class DateTimeMapper implements IGraphQLTypeMapper {
	public boolean handlesType(Type type) {
		return type == DateTime.class;
	}

	@Override
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper objectMapper,  Type type) {
		return Scalars.GraphQLString;
	}

}
