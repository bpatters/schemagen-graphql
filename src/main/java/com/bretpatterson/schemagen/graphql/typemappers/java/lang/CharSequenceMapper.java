package com.bretpatterson.schemagen.graphql.typemappers.java.lang;

import java.lang.reflect.Type;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;

import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

/**
 * Typemapper for Enum. Maps it to a generic string type.
 */
@GraphQLTypeMapper(type = CharSequence.class)
public class CharSequenceMapper implements IGraphQLTypeMapper {

	@Override
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return CharSequence.class.isAssignableFrom(graphQLObjectMapper.getClassFromType(type));
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
