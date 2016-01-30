package com.bretpatterson.schemagen.graphql;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.google.common.collect.ImmutableList;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLSchemaBuilderTest {


	@GraphQLTypeMapper(type = TestType.class)
	private class TestTypeMapper implements IGraphQLTypeMapper {

		@Override
		public boolean handlesType(Type type) {
			return type == GraphQLSchemaBuilderTest.class;
		}

		@Override
		public GraphQLOutputType getOutputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return GraphQLObjectType.newObject().name("FakeTestType").build();
		}

		@Override
		public GraphQLInputType getInputType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
			return GraphQLInputObjectType.newInputObject().name("FakeTestType").build();
		}
	}

	private class TestType {
	}

	@GraphQLTypeName(name="RenamedTestInputType")
	private class TestInputType {
	}

	@GraphQLController
	private class TestController {

		@GraphQLQuery(name = "testType")
		public TestType getTestType() {
			return new TestType();
		}

		@GraphQLQuery(name="testQueryArguments")
		public String testQueryArguments(@GraphQLParam(name="string")
										 String stringType,
										 @GraphQLParam(name="int")
										 Integer intType,
										 @GraphQLParam(name="test")
										 TestInputType testType) {
			return "";
		}
	}

	@Test
	public void testCustomTypeMappers() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController()))
				.registerTypeMappers(ImmutableList.<IGraphQLTypeMapper> of(new TestTypeMapper()))
				.build();
		assertEquals(GraphQLObjectType.class, schema.getType("FakeTestType").getClass());
	}

	@Test
	public void testController() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController()))
				.build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testType");
		assertEquals("testType", queryType.getName());
		assertEquals("TestType", queryType.getType().getName());
	}

	@Test
	public void testQueryArguments() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController()))
				.build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testQueryArguments");
		assertEquals("testQueryArguments", queryType.getName());
		assertEquals(Scalars.GraphQLString, queryType.getArgument("string").getType());
		assertEquals(Scalars.GraphQLInt, queryType.getArgument("int").getType());
		assertEquals(GraphQLInputObjectType.class, queryType.getArgument("test").getType().getClass());
		assertEquals("RenamedTestInputType", queryType.getArgument("test").getType().getName());
	}
}
