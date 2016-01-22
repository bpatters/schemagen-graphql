package com.schemagen.graphql.mappers.org.joda.time;

import com.schemagen.graphql.GraphQLObjectMapper;
import com.schemagen.graphql.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
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
	public GraphQLOutputType getOutputType(GraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLInt;
	}

	@Override
	public GraphQLInputType getInputType(GraphQLObjectMapper objectMapper,  Type type) {
		return Scalars.GraphQLInt;
	}

}
