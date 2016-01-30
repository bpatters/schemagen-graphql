package com.bretpatterson.schemagen.graphql.impl;

import com.bretpatterson.schemagen.graphql.GraphQLSchemaBuilder;
import com.bretpatterson.schemagen.graphql.IGraphQLObjectMapper;
import com.bretpatterson.schemagen.graphql.ITypeNamingStrategy;
import com.bretpatterson.schemagen.graphql.datafetchers.ITypeFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import graphql.Scalars;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLObjectMapperTest {

	@Mock
	ITypeFactory objectMapper;
	IGraphQLObjectMapper graphQLObjectMapper = new GraphQLObjectMapper(objectMapper,
			GraphQLSchemaBuilder.getDefaultTypeMappers(),
			Optional.<ITypeNamingStrategy> of(new FullTypeNamingStrategy()), ImmutableList.<Class>of());

	@Before
	public void setup() {
		objectMapper = mock(ITypeFactory.class);
	}

	private void assertTypeMapping(String name, GraphQLOutputType expectedType, GraphQLOutputType graphQLOutputType) {
		assertEquals(name, graphQLOutputType.getName());
		assertEquals(expectedType.getClass(), graphQLOutputType.getClass());
	}


	@Test
	public void testOutputMapperPrimitives() {

		assertTypeMapping(Scalars.GraphQLString.getName(), Scalars.GraphQLString, graphQLObjectMapper.getOutputType(String.class));
		assertTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(Integer.class));
		assertTypeMapping(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(int.class));
		assertTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(Long.class));
		assertTypeMapping(Scalars.GraphQLLong.getName(), Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(long.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Float.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(float.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(Boolean.class));
		assertTypeMapping(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(boolean.class));

	}

	@Test
	public void testFullTypeMappingStrategy() {

		assertTypeMapping("String", Scalars.GraphQLString, graphQLObjectMapper.getOutputType(String.class));
		assertTypeMapping("Int", Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(Integer.class));
		assertTypeMapping("Int", Scalars.GraphQLInt, graphQLObjectMapper.getOutputType(int.class));
		assertTypeMapping("Long", Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(Long.class));
		assertTypeMapping("Long", Scalars.GraphQLLong, graphQLObjectMapper.getOutputType(long.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Float.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(float.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(Double.class));
		assertTypeMapping("Float", Scalars.GraphQLFloat, graphQLObjectMapper.getOutputType(double.class));
		assertTypeMapping("Boolean", Scalars.GraphQLBoolean, graphQLObjectMapper.getOutputType(Boolean.class));
		assertTypeMapping("com.bretpatterson.schemagen.graphql.impl.GraphQLObjectMapperTest", GraphQLObjectType.newObject().name("com.bretpatterson.schemagen.graphql.impl.GraphQLObjectMapperTest").build(), graphQLObjectMapper.getOutputType(this.getClass()));
	}


}
