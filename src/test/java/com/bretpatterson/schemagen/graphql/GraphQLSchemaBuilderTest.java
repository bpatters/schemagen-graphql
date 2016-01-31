package com.bretpatterson.schemagen.graphql;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeMapper;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;
import com.bretpatterson.schemagen.graphql.impl.common.JacksonTypeFactory;
import com.bretpatterson.schemagen.graphql.typemappers.IGraphQLTypeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by bpatterson on 1/23/16.
 */
public class GraphQLSchemaBuilderTest {

	@GraphQLTypeMapper(type = TestType.class)
	private class TestTypeMapper implements IGraphQLTypeMapper {

		@Override
		public boolean handlesType(IGraphQLObjectMapper graphQLObjectMapper, Type type) {
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

	@GraphQLTypeName(name = "RenamedTestInputType")
	private class RenameMe {

	}

	@GraphQLController
	public class TestController {

		String name = "";

		@GraphQLQuery(name = "testType")
		public TestType getTestType() {
			return new TestType();
		}

		@GraphQLQuery(name = "testQueryArguments")
		public String testQueryArguments(@GraphQLParam(name = "string") String stringType,
				@GraphQLParam(name = "int") Integer intType,
				@GraphQLParam(name = "test") RenameMe testType) {
			return "";
		}

		@GraphQLMutation(name = "name")
		public String setName(@GraphQLParam(name = "name") String name) {
			this.name = name;

			return this.name;
		}

		@GraphQLQuery(name = "name")
		public String getName() {
			return name;
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
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder().registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController())).build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testType");
		assertEquals("testType", queryType.getName());
		assertEquals("TestType", queryType.getType().getName());
	}

	@Test
	public void testQueryArguments() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder().registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController())).build();

		GraphQLFieldDefinition queryType = schema.getQueryType().getFieldDefinition("testQueryArguments");
		assertEquals("testQueryArguments", queryType.getName());
		assertEquals(Scalars.GraphQLString, queryType.getArgument("string").getType());
		assertEquals(Scalars.GraphQLInt, queryType.getArgument("int").getType());
		assertEquals(GraphQLInputObjectType.class, queryType.getArgument("test").getType().getClass());
		assertEquals("RenamedTestInputType", queryType.getArgument("test").getType().getName());
	}

	@Test
	public void testMutation() {
		GraphQLSchema schema = GraphQLSchemaBuilder.newBuilder()
				.registerTypeFactory(new JacksonTypeFactory(new ObjectMapper()))
				.registerGraphQLContollerObjects(ImmutableList.<Object> of(new TestController()))
				.build();

		GraphQLFieldDefinition mutationType = schema.getMutationType().getFieldDefinition("name");
		assertEquals("name", mutationType.getName());
		assertEquals(Scalars.GraphQLString, mutationType.getArgument("name").getType());

		ExecutionResult result = new GraphQL(schema).execute("mutation M { name(name: \"The new name\") }");

		String newName = (String) ((Map) result.getData()).get("name");
		assertEquals(0, result.getErrors().size());
		assertEquals("The new name", newName);

		result = new GraphQL(schema).execute("query Q { name }");

		newName = (String) ((Map) result.getData()).get("name");
		assertEquals(0, result.getErrors().size());
		assertEquals("The new name", newName);
	}
}
