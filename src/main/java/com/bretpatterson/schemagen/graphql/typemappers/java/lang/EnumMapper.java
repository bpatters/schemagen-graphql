package com.bretpatterson.schemagen.graphql.typemappers.java.lang;

import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

import java.lang.reflect.Type;

/**
 * Typemapper for Enum. Maps it to a generic string type.
 */
@GraphQLTypeMapper(type = Enum.class)
public class EnumMapper implements IGraphQLTypeMapper {

	@Override
	public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return Enum.class.isAssignableFrom(graphQLObjectMapper.getClassFromType(type));
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
