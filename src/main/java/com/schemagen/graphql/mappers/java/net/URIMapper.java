package com.schemagen.graphql.mappers.java.net;

import com.schemagen.graphql.GraphQLObjectMapper;
import com.schemagen.graphql.GraphQLTypeMapper;
import com.schemagen.graphql.mappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;
import java.net.URI;


/**
 * Created by bpatterson on 1/19/16.
 */
@GraphQLTypeMapper(type=URI.class)
public class URIMapper implements IGraphQLTypeMapper {

	public boolean handlesType(Type type) {
		return type == URI.class;
	}
	@Override
	public GraphQLOutputType getOutputType(GraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

	@Override
	public GraphQLInputType getInputType(GraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

}
