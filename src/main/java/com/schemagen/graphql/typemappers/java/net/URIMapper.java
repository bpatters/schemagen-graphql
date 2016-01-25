package com.schemagen.graphql.typemappers.java.net;

import com.schemagen.graphql.IGraphQLObjectMapper;
import com.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.schemagen.graphql.typemappers.IGraphQLTypeMapper;
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
	public GraphQLOutputType getOutputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

	@Override
	public GraphQLInputType getInputType(IGraphQLObjectMapper objectMapper, Type type) {
		return Scalars.GraphQLString;
	}

}
